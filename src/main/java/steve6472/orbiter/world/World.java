package steve6472.orbiter.world;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.objects.PhysicsBody;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class World
{
    PhysicsSpace physics;

    public World()
    {
        physics = new PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT);
        addPlane(new Vector3f(0, 1, 0), -1);
    }

    public PhysicsSpace physics()
    {
        return physics;
    }

    public void tick(float frameTime)
    {
        physics.update(frameTime, 8);
    }

    private void addPlane(Vector3f normal, float constant)
    {
        Plane plane = new Plane(normal, constant);
        CollisionShape planeShape = new PlaneCollisionShape(plane);
        float mass = PhysicsBody.massForStatic;
        PhysicsRigidBody floor = new PhysicsRigidBody(planeShape, mass);
        physics.addCollisionObject(floor);
    }
}
