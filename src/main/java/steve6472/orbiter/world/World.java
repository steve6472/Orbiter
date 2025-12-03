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
import steve6472.core.log.Log;
import steve6472.core.util.MathUtil;
import steve6472.flare.Camera;
import steve6472.flare.MasterRenderer;
import steve6472.flare.tracy.FlareProfiler;
import steve6472.flare.tracy.Profiler;
import steve6472.orbiter.Client;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Convert;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.audio.MovingSource;
import steve6472.orbiter.audio.Source;
import steve6472.orbiter.audio.WorldSounds;
import steve6472.orbiter.network.NetworkTicker;
import steve6472.orbiter.network.api.NetworkMain;
import steve6472.orbiter.player.PCPlayer;
import steve6472.orbiter.player.Player;
import steve6472.orbiter.rendering.gizmo.Gizmos;
import steve6472.orbiter.rendering.snapshot.SnapshotPools;
import steve6472.orbiter.rendering.snapshot.WorldSnapshot;
import steve6472.orbiter.scheduler.Scheduler;
import steve6472.orbiter.settings.Keybinds;
import steve6472.orbiter.settings.Settings;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static steve6472.flare.render.debug.DebugRender.*;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class World implements EntityControl, EntityModify, WorldSounds
{
    private static final Logger LOGGER = Log.getLogger(World.class);
    // TODO: should be either in constants or configurable (in menu only, only changed on restart!!!)
    public static final int MAX_PARTICLES = 32767;

    private final PhysicsSystem physics;
    final TempAllocator tempAllocator;
    final JobSystem jobSystem;
    private final JoltBodies joltBodies;
    private final BodyManagerDrawSettings settings = new BodyManagerDrawSettings();

    private final Engine ecsEngine;
    private final PooledEngine particleEngine;
    private final WorldSystems systems;
    final ParticleSystems particleSystems;
    private final List<Source> soundSources;
    public long ticks;

    private final ScheduledExecutorService tickExecutor;

    private static final boolean RENDER_X_WALL = false;

    public World()
    {
        physics = createPhysicsSystem();

        tempAllocator = new TempAllocatorMalloc();
        int numWorkerThreads = Runtime.getRuntime().availableProcessors();
        jobSystem = new JobSystemThreadPool(Jolt.cMaxPhysicsJobs, Jolt.cMaxPhysicsBarriers, numWorkerThreads);
        joltBodies = new JoltBodies();

        ecsEngine = new Engine();
        particleEngine = new PooledEngine(MAX_PARTICLES >> 4, MAX_PARTICLES, MAX_PARTICLES >> 4, MAX_PARTICLES);
        systems = new WorldSystems(this, ecsEngine);
        particleSystems = new ParticleSystems(this, particleEngine);
        soundSources = new ArrayList<>(256);

        tickExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    public void startTicking()
    {
        tickExecutor.scheduleAtFixedRate(() ->
        {
            Profiler profiler = FlareProfiler.world();
            profiler.start();

            try
            {
                profiler.push("tick schedulers");
                //noinspection deprecation
                Scheduler.instance().tick();

                profiler.popPush("networkTick");
                OrbiterApp.getInstance().networkTicker.worldTick();

                profiler.popPush("main tick");

                // TODO: probably calculate the actual delta here?
                tick(1f / Constants.TICKS_IN_SECOND);

                profiler.popPush("renderGizmoFloor");
                renderGizmoFloor();

                profiler.popPush("snapshot world state");
                OrbiterApp.getInstance().getClient().snapshotWorldState();

                ticks++;
                profiler.pop();
            } catch (Exception exception)
            {
                LOGGER.severe("Exception thrown while ticking");
                FlareProfiler.message("Exception thrown: " + exception.getMessage());
                exception.printStackTrace();
                throw new RuntimeException(exception);
            } finally
            {
                profiler.end();
            }
        }, 0, (long) (1000 / Constants.TICKS_IN_SECOND), TimeUnit.MILLISECONDS);

        OrbiterApp.getInstance().networkTicker.tickResponsibility = NetworkTicker.TickResponsibility.WORLD;
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
        result.setGravity(Constants.GRAVITY.x(), Constants.GRAVITY.y(), Constants.GRAVITY.z());

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
        addGroundPlane();
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

    @Override
    public World getItself()
    {
        return this;
    }

    public void tick(float frameTime)
    {
        Profiler profiler = FlareProfiler.world();
        if (Keybinds.TEST_SWAP.isActive() && OrbiterApp.getInstance().isMouseGrabbed())
        {
            systems.sequential = !systems.sequential;
            CONTROL_LOGGER.info("Sequential: " + systems.sequential);
        }

        profiler.push("sound");
        tickSound();
        profiler.popPush("hold system pre physics");

        float timePerStep = 1f / Constants.TICKS_IN_SECOND; // in seconds
        int collisionSteps = 1;
        systems.holdSystem.prePhysicsTickUpdate(timePerStep);

        Client client = OrbiterApp.getInstance().getClient();
        Player player = client.player();

        profiler.popPush("player world tick");
        ((PCPlayer) player).worldTick(client.getCamera());

        profiler.popPush("physics");
        int errors = physics.update(timePerStep, collisionSteps, tempAllocator, jobSystem);
        assert errors == EPhysicsUpdateError.None : errors;

        profiler.popPush("player post sim");
        Character character = ((PCPlayer) player).character;
        character.postSimulation(0.01f);

        profiler.popPush("updateRayTrace");
        client.getRayTrace().updateLookAt(client.getCamera(), PCPlayer.REACH);

        profiler.popPush("systems");

        profiler.push("states");
        systems.updateStates();
        profiler.popPush("tick systems");
        systems.runTickSystems(frameTime);
        profiler.popPush("particle systems");
        particleSystems.runTickSystems(frameTime);
        profiler.pop();
        profiler.pop();
    }

    public WorldSnapshot createSnapshot(SnapshotPools pools, UUID clientUUID)
    {
        Profiler profiler = FlareProfiler.world();
        WorldSnapshot snapshot = new WorldSnapshot();
        profiler.push("particles");
        snapshot.particleSnapshots.createSnapshot(pools, particleEngine);
        profiler.popPush("models");
        snapshot.modelSnapshots.createSnapshot(pools, ecsEngine, this, clientUUID);
        profiler.popPush("gizmos");
        snapshot.gizmos = Gizmos.getGizmosForRender();
        profiler.pop();

        return snapshot;
    }

    private void renderGizmoFloor()
    {
        // Debug render of plane
        float density = 1f;
        int range = 64;
        float y = 0;
        float x = 0;
        for (int i = -range; i < range; i++)
        {
            Gizmos.line(new Vector3f(i * density, y, -range * density), new Vector3f(i * density, y, range * density), 0xff404040, 1f);
            Gizmos.line(new Vector3f(-range * density, y, i * density), new Vector3f(range * density, y, i * density), 0xff404040, 1f);

            if (RENDER_X_WALL)
            {
                Gizmos.line(new Vector3f(x, i * density, -range * density), new Vector3f(x, i * density, range * density), 0xffff0000, 1f);
                Gizmos.line(new Vector3f(x, -range * density, i * density), new Vector3f(x, range * density, i * density), 0xffff0000, 1f);
            }
        }

        Gizmos.line(new Vector3f(0, 0, 0), new Vector3f(1, 0, 0), 0xffff0000, 3f);
        Gizmos.line(new Vector3f(0, 0, 0), new Vector3f(0, 1, 0), 0xff00ff00, 3f);
        Gizmos.line(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1), 0xff0000ff, 3f);

//        float yOffset = (float) (Math.sin(Math.toRadians((System.currentTimeMillis() % 3600) / 10d)) * 0.4f);
//        Gizmos.filledLineCuboid(new Vector3f(0.5f, 1.5f + yOffset, 0.5f), 0.3f, 0xffa0a0a0, 0x6080cc30, 2f);
//        Gizmos.filledLineCuboid(new Vector3f(1.5f, 1.5f - yOffset, 0.5f), 0.3f, 0x60cc3080, 1f);

        // Temporary "crosshair"
        RayCastResult lookAtObject = OrbiterApp.getInstance().getClient().getRayTrace().getLookAtObject();
        if (lookAtObject != null)
        {
            Camera camera = OrbiterApp.getInstance().camera();
            Vector3f direction = MathUtil.yawPitchToVector(camera.yaw() + (float) (Math.PI * 0.5f), camera.pitch());
            Vector3f hitPosition = new Vector3f(camera.viewPosition).add(new Vector3f(direction).mul(lookAtObject.getFraction() * PCPlayer.REACH));

            Gizmos.filledLineCuboidFromSize(hitPosition, 0.02f, 0.02f, 0.02f, 0xffa0a0a0, 0x80a0a0a0, 1f);
        }
    }

    public void debugRender(float frameTime)
    {
        systems.runRenderSystems(frameTime);

        // TODO: I don't fucking know about this one lol
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

    private void addGroundPlane()
    {
        BodyInterface bi = physics.getBodyInterface();

        Vec3Arg norm = Convert.jomlToPhys(new Vector3f(0, 1f, 0));
        ConstPlane plane = new Plane(norm, (float) 0);
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
        Scheduler.clearAllTasks();
        tickExecutor.shutdown();
        try
        {
            if (tickExecutor.awaitTermination(20, TimeUnit.SECONDS))
            {
                LOGGER.finest("World exit successful");
                // TODO: enable entering world here or in catch(?)
            }
        } catch (InterruptedException e)
        {
            LOGGER.severe("World shutdown took longer than 20 seconds!");
            throw new RuntimeException(e);
        }
        physics.removeAllBodies();
        bodyMap().clear();

        particleEngine().clearPools();
        clearAllSoundSources();
    }
}
