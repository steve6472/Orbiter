package steve6472.orbiter.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.objects.PhysicsBody;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Plane;
import org.joml.Vector3f;
import steve6472.flare.MasterRenderer;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Convert;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.Registries;
import steve6472.orbiter.network.api.NetworkMain;
import steve6472.orbiter.world.ecs.RenderECSSystem;
import steve6472.orbiter.world.ecs.systems.*;

import java.util.*;

import static steve6472.flare.render.debug.DebugRender.*;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class World implements EntityControl, EntityModify
{
    PhysicsSpace physics;
    Engine ecsEngine;
    // TODO: split to client & host ?
    private final Map<UUID, PhysicsRigidBody> bodyMap = new HashMap<>();
    private final Map<UUID, PhysicsGhostObject> ghostMap = new HashMap<>();

    public UpdateClientData updateClientData;

    private static final boolean RENDER_X_WALL = false;

    public World()
    {
        physics = new PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT);
        physics.useDeterministicDispatch(true);
        ecsEngine = new Engine();
    }

    public void init(MasterRenderer renderer)
    {
        addPlane(new Vector3f(0, 1f, 0), 0);
        initSystems();

        ecsEngine.addSystem(new RenderNametag(renderer)); // "Render Nametag"
        ecsEngine.addSystem(new RenderNetworkData(renderer));
    }

    private void initSystems()
    {
        // First
        ecsEngine.addSystem(new UpdateECS(this)); // "Update ECS Positions", "Updates ECS Positions with data from last tick of Physics Simulation"
        /*systems.registerSystem(new ComponentSystem()
        {
            @Override
            public void tick(Dominion dominion, World world)
            {
//                if (!steam.isHost())
//                    return;

                dominion.findEntitiesWith(Tag.FireflyAI.class, Position.class).forEach(e ->
                {
                    Position position = e.comp2();
                    modifyComponent(e.entity(), position, p -> p.add(RandomUtil.randomFloat(-0.01f, 0.01f), RandomUtil.randomFloat(-0.01f, 0.01f), RandomUtil.randomFloat(-0.01f, 0.01f)));
                });
            }
        }, "Firefly AI", "Test firefly entity");*/
        // Needs to be before network sync and physics update
        ecsEngine.addSystem(updateClientData = new UpdateClientData());

        ecsEngine.addSystem(new BroadcastClientPosition());

        // Last
        ecsEngine.addSystem(new NetworkSync(network())); //"Network Sync", ""
        ecsEngine.addSystem(new UpdatePhysics(this)); // "Update Physics Positions", "Updates Physics Positions with data from last tick ECS Systems"
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

    public void tick()
    {
        Set<UUID> accessed = new HashSet<>();

        for (PhysicsRigidBody body : physics.getRigidBodyList())
        {
            if (body.userIndex() == Constants.PLAYER_MAGIC_CONSTANT)
            {
                body.activate(true);
                UUID uuid = (UUID) body.getUserObject();
                PhysicsGhostObject physicsGhostObject = ghostMap.computeIfAbsent(uuid, _ ->
                {
                    CollisionShape shape = Registries.COLLISION
                        .get(Constants.key("blockbench/static/player_capsule"))
                        .collisionShape();
                    shape.setScale(1.5f);
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

        physics.update(1f / Constants.TICKS_IN_SECOND, 8);

        // Disable rendering systems, enable tick systems
        for (EntitySystem system : ecsEngine.getSystems())
        {
            system.setProcessing(!(system instanceof RenderECSSystem));
        }

        ecsEngine.update(0);

        // Enable rendering systems, disable tick systems
        for (EntitySystem system : ecsEngine.getSystems())
        {
            system.setProcessing(system instanceof RenderECSSystem);
        }
    }

    public void debugRender()
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

//        renderSystems.run();
        ecsEngine.update(0);

        PhysicsRenderer.render(physics());
    }

    private void addPlane(Vector3f normal, float constant)
    {
        Plane plane = new Plane(Convert.jomlToPhys(normal), constant);
        CollisionShape planeShape = new PlaneCollisionShape(plane);
        float mass = PhysicsBody.massForStatic;
        PhysicsRigidBody floor = new PhysicsRigidBody(planeShape, mass);
        physics.addCollisionObject(floor);
    }
}
