package steve6472.orbiter.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.objects.PhysicsBody;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Plane;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import steve6472.flare.MasterRenderer;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Convert;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.Registries;
import steve6472.orbiter.audio.Source;
import steve6472.orbiter.audio.WorldSounds;
import steve6472.orbiter.network.api.NetworkMain;
import steve6472.orbiter.settings.Settings;

import java.util.*;
import java.util.function.Consumer;

import static steve6472.flare.render.debug.DebugRender.*;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class World implements EntityControl, EntityModify, WorldSounds
{
    private static final int MAX_PARTICLES = 32767;

    // TODO: split to client & host ?
    private final Map<UUID, PhysicsRigidBody> bodyMap = new HashMap<>();
    private final Map<UUID, PhysicsGhostObject> ghostMap = new HashMap<>();

    private final PhysicsSpace physics;
    private final Engine ecsEngine;
    private final PooledEngine particleEngine;
    private final WorldSystems systems;
    private final ParticleSystems particleSystems;
    private final List<Source> soundSources;

    private static final boolean RENDER_X_WALL = false;

    public World()
    {
        physics = new PhysicsSpace(Convert.phys(-1000, -1000, -1000), Convert.phys(1000, 1000, 1000), PhysicsSpace.BroadphaseType.DBVT);
        physics.useDeterministicDispatch(true);
        ecsEngine = new Engine();
        particleEngine = new PooledEngine(MAX_PARTICLES >> 4, MAX_PARTICLES, MAX_PARTICLES >> 4, MAX_PARTICLES);
        systems = new WorldSystems(this, ecsEngine);
        particleSystems = new ParticleSystems(this, particleEngine);
        soundSources = new ArrayList<>(256);
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
    public PhysicsSpace physics()
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
    public Map<UUID, PhysicsRigidBody> bodyMap()
    {
        return bodyMap;
    }

    @Override
    public NetworkMain network()
    {
        return OrbiterApp.getInstance().getNetwork();
    }

    public void tick(float frameTime)
    {
        tickSoundClean();
        shittyGhostPhysicsThing();
        physics.update(1f / Constants.TICKS_IN_SECOND, 8);

        systems.updateStates();
        systems.runTickSystems(frameTime);
        particleSystems.runTickSystems(frameTime);
    }

    private void shittyGhostPhysicsThing()
    {
        Set<UUID> accessed = new HashSet<>();

        for (PhysicsRigidBody body : physics.getRigidBodyList())
        {
            if (body.userIndex() == Constants.MP_PLAYER_MAGIC_CONSTANT)
            {
                body.activate(true);
                UUID uuid = (UUID) body.getUserObject();
                PhysicsGhostObject physicsGhostObject = ghostMap.computeIfAbsent(uuid, _ ->
                {
                    CollisionShape shape = Registries.COLLISION
                        .get(Constants.key("blockbench/static/player_capsule_ghost"))
                        .collisionShape();
                    PhysicsGhostObject pgo = new PhysicsGhostObject(shape);
                    physics.add(pgo);
                    return pgo;
                });

                physicsGhostObject.setPhysicsLocation(body.getPhysicsLocation(new com.jme3.math.Vector3f()));
                physicsGhostObject.activate(true);
                accessed.add(uuid);
            }
        }

        ghostMap.keySet().removeIf(uuid -> !accessed.contains(uuid));
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

        systems.runRenderSystems(frameTime);

        PhysicsRenderer.render(physics());
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
        }
    }

    private void addPlane(Vector3f normal, float constant)
    {
        Plane plane = new Plane(Convert.jomlToPhys(normal), constant);
        CollisionShape planeShape = new PlaneCollisionShape(plane);
        float mass = PhysicsBody.massForStatic;
        PhysicsRigidBody floor = new PhysicsRigidBody(planeShape, mass);
        floor.setUserIndex2(~Constants.PhysicsFlags.NEVER_DEBUG_RENDER);
        physics.addCollisionObject(floor);
    }

    @Override
    public List<Source> getSoundSources()
    {
        return soundSources;
    }

    public void cleanup()
    {
        particleEngine().clearPools();
        clearAllSoundSources();
    }
}
