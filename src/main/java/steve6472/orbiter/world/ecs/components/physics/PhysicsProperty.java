package steve6472.orbiter.world.ecs.components.physics;

import com.jme3.bullet.objects.PhysicsRigidBody;

import java.util.Set;

/**
 * Created by steve6472
 * Date: 10/11/2024
 * Project: Orbiter <br>
 */
public interface PhysicsProperty
{
    Set<Class<? extends PhysicsProperty>> PHYSICS_COMPONENTS = Set.of(
        Position.class,
        Rotation.class,
        AngularVelocity.class,
        LinearVelocity.class,
        AngularFactor.class,
        LinearFactor.class,
        AngularDamping.class,
        LinearDamping.class,
        Friction.class,
        Mass.class,
        Collision.class
    );

    ModifyState modifyComponent(PhysicsRigidBody body);

    void modifyBody(PhysicsRigidBody body);
}
