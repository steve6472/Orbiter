package steve6472.orbiter.world.ecs.systems.debug;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.github.stephengold.joltjni.AaBox;
import com.github.stephengold.joltjni.Mat44;
import com.github.stephengold.joltjni.Vec3;
import org.joml.Matrix4f;
import steve6472.moondust.widget.component.IBounds;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Convert;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.Registries;
import steve6472.orbiter.actions.Action;
import steve6472.orbiter.rendering.gizmo.Gizmos;
import steve6472.orbiter.util.AABB;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.BlueprintReference;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.ecs.components.physics.Collision;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.components.physics.Rotation;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;

import java.util.Optional;

/**
 * Created by steve6472
 * Date: 11/22/2025
 * Project: Orbiter <br>
 */
public class DebugShowPhysicsBounds extends IteratingProfiledSystem
{
    public DebugShowPhysicsBounds()
    {
        super(Family.all(Collision.class, Position.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        if (OrbiterApp.getInstance().getClient().player().ecsEntity() == entity)
            return;

        Position position = Components.POSITION.get(entity);
        Rotation rotation = Components.ROTATION.get(entity);
        Collision collision = Components.COLLISION.get(entity);
        AaBox localBounds = collision.shape().getLocalBounds();
        Matrix4f mat = new Matrix4f().translate(position.toVec3f()).rotate(rotation.toQuat());
        localBounds = localBounds.transformed(new Mat44(mat.get(new float[16])));
        AABB aabb = AABB.fromMinMax(Convert.physToJoml(localBounds.getMin()), Convert.physToJoml(localBounds.getMax()));
        Gizmos.filledLineCuboid(aabb, 0x403030cc);
    }
}
