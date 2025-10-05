package steve6472.orbiter.world.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import com.github.stephengold.joltjni.BodyInterface;

import java.util.Set;

/**
 * Created by steve6472
 * Date: 10/11/2024
 * Project: Orbiter <br>
 */
public interface PhysicsProperty extends Component
{
    Set<Class<? extends PhysicsProperty>> PHYSICS_COMPONENTS = Set.of(
        Position.class,
        Rotation.class,
        AngularVelocity.class,
        LinearVelocity.class,
        Friction.class
    );

    ModifyState modifyComponent(BodyInterface bi, int body);

    void modifyBody(BodyInterface bi, int body);
}
