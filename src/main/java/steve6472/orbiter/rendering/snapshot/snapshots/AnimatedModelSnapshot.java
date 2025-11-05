package steve6472.orbiter.rendering.snapshot.snapshots;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import steve6472.flare.assets.model.Model;
import steve6472.flare.assets.model.blockbench.animation.controller.AnimationController;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.AnimatedModel;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.components.physics.Rotation;
import steve6472.orlang.OrlangEnvironment;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 11/4/2025
 * Project: Orbiter <br>
 */
public class AnimatedModelSnapshot implements Pool.Poolable, UUIDSnapshot
{
    private static final Quaternionf ROTATION = new Quaternionf();
    private static final Matrix4f TRANSFORM = new Matrix4f();

    public Matrix4f[] transformations;
    public Model model;
    public UUID uuid;

    @Override
    public void reset()
    {
        model = null;
        uuid = null;
        transformations = null;
    }

    public void fromEntity(Entity entity, UUID uuid)
    {
        this.uuid = uuid;

        AnimatedModel animatedModel = Components.ANIMATED_MODEL.get(entity);
        Position position = Components.POSITION.get(entity);
        Rotation rotation = Components.ROTATION.get(entity);
        OrlangEnv orlangEnv = Components.ENVIRONMENT.get(entity);
        OrlangEnvironment env = orlangEnv == null ? null : orlangEnv.env;

        model = animatedModel.model;
        AnimationController animationController = animatedModel.animationController;

        TRANSFORM.identity();
        if (position != null)
        {
            TRANSFORM.translate(position.x(), position.y(), position.z());
        }
        if (rotation != null)
        {
            ROTATION.set(rotation.x(), rotation.y(), rotation.z(), rotation.w());
            TRANSFORM.rotate(ROTATION);
        }

        animationController.tick(TRANSFORM, env);

        transformations = animationController.getTransformations();
    }

    @Override
    public UUID uuid()
    {
        return uuid;
    }
}
