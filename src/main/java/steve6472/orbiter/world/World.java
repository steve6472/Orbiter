package steve6472.orbiter.world;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.objects.PhysicsBody;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Plane;
import dev.dominion.ecs.api.Dominion;
import org.joml.Vector3f;
import steve6472.core.util.RandomUtil;
import steve6472.flare.MasterRenderer;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Convert;
import steve6472.orbiter.Registries;
import steve6472.orbiter.network.api.Connections;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.core.ComponentRenderSystem;
import steve6472.orbiter.world.ecs.core.ComponentSystem;
import steve6472.orbiter.world.ecs.core.ComponentSystems;
import steve6472.orbiter.world.ecs.systems.NetworkSync;
import steve6472.orbiter.world.ecs.systems.RenderNametag;
import steve6472.orbiter.world.ecs.systems.UpdateECS;
import steve6472.orbiter.world.ecs.systems.UpdatePhysics;

import java.util.*;

import static steve6472.flare.render.debug.DebugRender.*;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class World implements EntityControl
{
    PhysicsSpace physics;
    Dominion ecs;
    // TODO: split to client & host ?
    ComponentSystems<ComponentSystem> systems;
    ComponentSystems<ComponentRenderSystem> renderSystems;
    private final Map<UUID, PhysicsRigidBody> bodyMap = new HashMap<>();
    private final Map<UUID, PhysicsGhostObject> ghostMap = new HashMap<>();

    private static final boolean RENDER_X_WALL = false;

    public World()
    {
        physics = new PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT);
        physics.useDeterministicDispatch(true);
        ecs = Dominion.create("space");
        systems = new ComponentSystems<>(system -> system.tick(ecs, this));
    }

    public void init(MasterRenderer renderer)
    {
        renderSystems = new ComponentSystems<>(system -> system.tick(renderer, ecs, this));
        addPlane(new Vector3f(0, 1f, 0), 0);
        initSystems();
    }

    private void initSystems()
    {
        // First
        systems.registerSystem(new UpdateECS(), "Update ECS Positions", "Updates ECS Positions with data from last tick of Physics Simulation");
        systems.registerSystem(new ComponentSystem()
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
        }, "Firefly AI", "Test firefly entity");

        // Last
//        systems.registerSystem(new NetworkSync(steam), "Network Sync", "");
        systems.registerSystem(new UpdatePhysics(), "Update Physics Positions", "Updates Physics Positions with data from last tick ECS Systems");


        /*
         * Render
         */
        renderSystems.registerSystem(new RenderNametag(), "Render Nametag");
    }

    @Override
    public PhysicsSpace physics()
    {
        return physics;
    }

    @Override
    public Dominion ecs()
    {
        return ecs;
    }

    @Override
    public Map<UUID, PhysicsRigidBody> bodyMap()
    {
        return bodyMap;
    }

    @Override
    public Connections connections()
    {
        return null;
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
        systems.run();
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

        renderSystems.run();

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
