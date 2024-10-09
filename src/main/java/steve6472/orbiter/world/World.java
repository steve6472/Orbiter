package steve6472.orbiter.world;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.objects.PhysicsBody;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Plane;
import dev.dominion.ecs.api.Dominion;
import org.joml.Vector3f;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Convert;
import steve6472.orbiter.world.ecs.core.ECSystem;
import steve6472.orbiter.world.ecs.core.Systems;
import steve6472.orbiter.world.ecs.systems.UpdateECSPositions;
import steve6472.orbiter.world.ecs.systems.UpdatePhysicsPositions;

import java.util.*;

import static steve6472.volkaniums.render.debug.DebugRender.*;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class World implements EntityControl
{
    PhysicsSpace physics;
    Dominion ecs;
    Systems<ECSystem> systems;
    public final Map<UUID, PhysicsRigidBody> bodyMap = new HashMap<>();

    public World()
    {
        physics = new PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT);
        ecs = Dominion.create("space");
        systems = new Systems<>(system -> system.tick(ecs, this));
    }

    public void init()
    {
        addPlane(new Vector3f(0, 1, 0), -1);
        initSystems();
    }

    private void initSystems()
    {
        // First
        systems.registerSystem(new UpdateECSPositions(), "Update ECS Positions", "Updates ECS Positions with data from last tick of Physics Simulation");

        // Last
        systems.registerSystem(new UpdatePhysicsPositions(), "Update Physics Positions", "Updates Physics Positions with data from last tick ECS Systems");
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

    public void tick()
    {
        physics.update(1f / Constants.TICKS_IN_SECOND, 8);
        systems.run();
    }

    public void debugRender()
    {
        // Debug render of plane
        float density = 1f;
        int range = 64;
        float y = -1;
        for (int i = -range; i < range; i++)
        {
            addDebugObjectForFrame(line(new Vector3f(i * density, y, -range * density), new Vector3f(i * density, y, range * density), DARK_GRAY));
            addDebugObjectForFrame(line(new Vector3f(-range * density, y, i * density), new Vector3f(range * density, y, i * density), DARK_GRAY));
        }
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
