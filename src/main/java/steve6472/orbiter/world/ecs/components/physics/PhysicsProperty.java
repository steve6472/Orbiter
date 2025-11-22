package steve6472.orbiter.world.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import com.github.stephengold.joltjni.BodyInterface;
import com.mojang.datafixers.util.Pair;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.core.ComponentEntry;

import java.util.Set;

/**
 * Created by steve6472
 * Date: 10/11/2024
 * Project: Orbiter <br>
 */
public interface PhysicsProperty extends Component
{
    Set<Pair<Class<? extends PhysicsProperty>, ComponentEntry<?>>> PHYSICS_COMPONENTS = Set.of(
        Pair.of(Position.class, Components.POSITION),
        Pair.of(Rotation.class, Components.ROTATION),
        Pair.of(AngularVelocity.class, Components.ANGULAR_VELOCITY),
        Pair.of(LinearVelocity.class, Components.LINEAR_VELOCITY),
        Pair.of(Friction.class, Components.FRICTION)
    );

    ModifyState modifyComponent(BodyInterface bi, int body);

    void modifyBody(BodyInterface bi, int body);

    void setEcsModifyFlag();
    void resetEcsModifyFlag();
    boolean wasEcsModified();
}
