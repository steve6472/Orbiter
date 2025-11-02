package steve6472.orbiter.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.github.stephengold.joltjni.*;
import com.github.stephengold.joltjni.Character;
import com.github.stephengold.joltjni.enumerate.EActivation;
import com.github.stephengold.joltjni.enumerate.EMotionType;
import com.github.stephengold.joltjni.enumerate.EPhysicsUpdateError;
import com.github.stephengold.joltjni.readonly.ConstPlane;
import com.github.stephengold.joltjni.readonly.ConstShape;
import com.github.stephengold.joltjni.readonly.Vec3Arg;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import steve6472.core.util.Profiler;
import steve6472.flare.MasterRenderer;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Convert;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.audio.MovingSource;
import steve6472.orbiter.audio.Source;
import steve6472.orbiter.audio.WorldSounds;
import steve6472.orbiter.network.api.NetworkMain;
import steve6472.orbiter.player.PCPlayer;
import steve6472.orbiter.player.Player;
import steve6472.orbiter.rendering.snapshot.SnapshotPools;
import steve6472.orbiter.rendering.snapshot.WorldSnapshot;
import steve6472.orbiter.settings.Settings;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static steve6472.flare.render.debug.DebugRender.*;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class World implements EntityControl, EntityModify, WorldSounds
{
    // TODO: should be either in constants or configurable (in menu only)
    public static final int MAX_PARTICLES = 32767;

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    private final PhysicsSystem physics;
    private final TempAllocator tempAllocator;
    private final JobSystem jobSystem;
    private final JoltBodies joltBodies;
    private final Profiler physicsProfiler;
    private final BodyManagerDrawSettings settings = new BodyManagerDrawSettings();

    private final Engine ecsEngine;
    private final PooledEngine particleEngine;
    private final WorldSystems systems;
    private final ParticleSystems particleSystems;
    private final List<Source> soundSources;

    private static final boolean RENDER_X_WALL = false;

    public World()
    {
        physics = createPhysicsSystem();

        tempAllocator = new TempAllocatorMalloc();
        int numWorkerThreads = Runtime.getRuntime().availableProcessors();
        jobSystem = new JobSystemThreadPool(Jolt.cMaxPhysicsJobs, Jolt.cMaxPhysicsBarriers, numWorkerThreads);
        joltBodies = new JoltBodies();
        physicsProfiler = new Profiler(60);

        ecsEngine = new Engine();
        particleEngine = new PooledEngine(MAX_PARTICLES >> 4, MAX_PARTICLES, MAX_PARTICLES >> 4, MAX_PARTICLES);
        systems = new WorldSystems(this, ecsEngine);
        particleSystems = new ParticleSystems(this, particleEngine);
        soundSources = new ArrayList<>(256);
    }

    private PhysicsSystem createPhysicsSystem()
    {
        // For simplicity, use a single broadphase layer:
        int numBpLayers = 1;

        //noinspection ExtractMethodRecommender
        ObjectLayerPairFilterTable ovoFilter = new ObjectLayerPairFilterTable(Constants.Physics.NUM_OBJ_LAYERS);
        // Enable collisions between 2 moving bodies:
        ovoFilter.enableCollision(Constants.Physics.OBJ_LAYER_MOVING, Constants.Physics.OBJ_LAYER_MOVING);
        // Enable collisions between a moving body and a non-moving one:
        ovoFilter.enableCollision(Constants.Physics.OBJ_LAYER_MOVING, Constants.Physics.OBJ_LAYER_NON_MOVING);
        // Disable collisions between 2 non-moving bodies:
        ovoFilter.disableCollision(Constants.Physics.OBJ_LAYER_NON_MOVING, Constants.Physics.OBJ_LAYER_NON_MOVING);

        // Map both object layers to broadphase layer 0:
        BroadPhaseLayerInterfaceTable layerMap = new BroadPhaseLayerInterfaceTable(Constants.Physics.NUM_OBJ_LAYERS, numBpLayers);
        layerMap.mapObjectToBroadPhaseLayer(Constants.Physics.OBJ_LAYER_MOVING, 0);
        layerMap.mapObjectToBroadPhaseLayer(Constants.Physics.OBJ_LAYER_NON_MOVING, 0);

        /*
         * Pre-compute the rules for colliding object layers
         * with broadphase layers:
         */
        ObjectVsBroadPhaseLayerFilter ovbFilter = new ObjectVsBroadPhaseLayerFilterTable(layerMap, numBpLayers, ovoFilter, Constants.Physics.NUM_OBJ_LAYERS);

        PhysicsSystem result = new PhysicsSystem();

        // Set high limits, even though this sample app uses only 2 bodies:
        int maxBodies = 5_000;
        int numBodyMutexes = 0; // 0 means "use the default number"
        int maxBodyPairs = 65_536;
        int maxContacts = 20_480;
        result.init(maxBodies, numBodyMutexes, maxBodyPairs, maxContacts, layerMap, ovbFilter, ovoFilter);

        return result;
    }

    public void init(MasterRenderer renderer)
    {
        addPlane(new Vector3f(0, 1f, 0), 0);
        systems.init(renderer);
        particleSystems.init();
    }

    public void updateClientData(UUID uuid, Consumer<Entity> function)
    {
        systems.updateClientData.add(uuid, function);
    }

    @Override
    public PhysicsSystem physics()
    {
        return physics;
    }

    @Override
    public Engine ecsEngine()
    {
        return ecsEngine;
    }

    public PooledEngine particleEngine()
    {
        return particleEngine;
    }

    @Override
    public JoltBodies bodyMap()
    {
        return joltBodies;
    }

    @Override
    public NetworkMain network()
    {
        return OrbiterApp.getInstance().getNetwork();
    }

    public void tick(float frameTime)
    {
        tickSound();
        Player player = OrbiterApp.getInstance().getClient().player();

        float timePerStep = 1f / Constants.TICKS_IN_SECOND; // in seconds
        int collisionSteps = 1;
        systems.holdSystem.prePhysicsTickUpdate(timePerStep);

//        var a = executor.submit(() -> {
            physicsProfiler.start();
            int errors = physics.update(timePerStep, collisionSteps, tempAllocator, jobSystem);
            assert errors == EPhysicsUpdateError.None : errors;
            physicsProfiler.end();
//        });
//
//        var b = executor.submit(() -> {
//        });
//
//        try
//        {
//            a.get();
//            b.get();
//        } catch (InterruptedException | ExecutionException e)
//        {
//            throw new RuntimeException(e);
//        }

//        System.out.println("Bodies: %s, Last: %.4fms (%.4fms left) Avg: %.4fms Max: %.4fms".formatted(
//            physics.getNumBodies(),
//            physicsProfiler.lastMilli(),
//            ((1f / 60f) * 1e3) - physicsProfiler.lastMilli(),
//            physicsProfiler.averageMilli(),
//            physicsProfiler.maxEverMilli()
//        ));

        Character character = ((PCPlayer) player).character;
        character.postSimulation(0.01f);

        systems.updateStates();
        systems.runTickSystems(frameTime);
        particleSystems.runTickSystems(frameTime);
    }

    public WorldSnapshot createSnapshot(SnapshotPools pools)
    {
        WorldSnapshot snapshot = new WorldSnapshot();
        snapshot.particleSnapshots.createSnapshot(pools, particleEngine);

        return snapshot;
    }

    public void debugRender(float frameTime)
    {
        // Debug render of plane
        float density = 1f;
        int range = 64;
        float y = 0;
        float x = 0;
        for (int i = -range; i < range; i++)
        {
            addDebugObjectForFrame(line(new Vector3f(i * density, y, -range * density), new Vector3f(i * density, y, range * density), DARK_GRAY));
            addDebugObjectForFrame(line(new Vector3f(-range * density, y, i * density), new Vector3f(range * density, y, i * density), DARK_GRAY));

            if (RENDER_X_WALL)
            {
                addDebugObjectForFrame(line(new Vector3f(x, i * density, -range * density), new Vector3f(x, i * density, range * density), RED));
                addDebugObjectForFrame(line(new Vector3f(x, -range * density, i * density), new Vector3f(x, range * density, i * density), RED));
            }
        }

        addDebugObjectForFrame(line(new Vector3f(0, 0, 0), new Vector3f(1, 0, 0), RED));
        addDebugObjectForFrame(line(new Vector3f(0, 0, 0), new Vector3f(0, 1, 0), GREEN));
        addDebugObjectForFrame(line(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1), BLUE));

        systems.runRenderSystems(frameTime);

//        physics().drawBodies(settings, OrbiterApp.getInstance().physicsDebugRenderer);
        physics().drawConstraints(OrbiterApp.getInstance().physicsDebugRenderer);
        physics().drawConstraintLimits(OrbiterApp.getInstance().physicsDebugRenderer);
        physics().drawConstraintReferenceFrame(OrbiterApp.getInstance().physicsDebugRenderer);
        debugSounds();
    }

    private void debugSounds()
    {
        if (!Settings.VISUAL_SOUNDS.get())
            return;

        for (Source soundSource : soundSources)
        {
            Vector3f position = soundSource.getPosition();
            addDebugObjectForFrame(lineSphere(0.2f, 3, soundSource.isPlaying() ? LIGHT_GREEN : RED), new Matrix4f().translate(position));

            if (soundSource instanceof MovingSource)
            {
                Vector3f velocity = soundSource.getVelocity();
                addDebugObjectForFrame(line(position, new Vector3f(position).add(velocity), BLUE));
            }
        }
    }

    private void addPlane(Vector3f normal, float constant)
    {
        BodyInterface bi = physics.getBodyInterface();

        Vec3Arg norm = Convert.jomlToPhys(normal);
        ConstPlane plane = new Plane(norm, constant);
        ConstShape floorShape = new PlaneShape(plane);

        BodyCreationSettings bcs = new BodyCreationSettings();
        bcs.setMotionType(EMotionType.Static);
        bcs.setObjectLayer(Constants.Physics.OBJ_LAYER_NON_MOVING);
        bcs.setShape(floorShape);
        Body floor = bi.createBody(bcs);
        floor.setUserData(~Constants.PhysicsFlags.NEVER_DEBUG_RENDER);
        bi.addBody(floor, EActivation.DontActivate);

//        BodyCreationSettings bcs = new BodyCreationSettings();
//        bcs.setMotionType(EMotionType.Static);
//        bcs.setObjectLayer(Constants.Physics.OBJ_LAYER_NON_MOVING);
//        bcs.setShape(new BoxShape(new Vec3(100.0f, 1.0f, 100.0f)));
//        bcs.setPosition(new RVec3(0, -1, 0));
//        Body floor = physics.getBodyInterface().createBody(bcs);
//        physics.getBodyInterface().addBody(floor, EActivation.DontActivate);
    }

    @Override
    public List<Source> getSoundSources()
    {
        return soundSources;
    }

    public void cleanup()
    {
        executor.close();

        physics.removeAllBodies();
        bodyMap().clear();

        particleEngine().clearPools();
        clearAllSoundSources();
    }
}
