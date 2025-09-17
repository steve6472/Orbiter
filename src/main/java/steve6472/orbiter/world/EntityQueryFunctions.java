package steve6472.orbiter.world;

import com.badlogic.ashley.core.Entity;
import org.joml.Vector3f;
import steve6472.flare.assets.model.blockbench.animation.controller.AnimationQuery;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.physics.LinearVelocity;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.components.physics.Rotation;
import steve6472.orlang.OrlangValue;
import steve6472.orlang.QueryFunctionSet;

/**
 * Created by steve6472
 * Date: 9/8/2025
 * Project: Orbiter <br>
 */
public class EntityQueryFunctions extends QueryFunctionSet implements AnimationQuery
{
    private double animTime;
    private boolean anyAnimFinished, allAnimsFinished;

    public EntityQueryFunctions(Entity entity)
    {
        Class<Double> D = Double.TYPE;
        Class<Boolean> B = Boolean.TYPE;

        functions.put("to_north", OrlangValue.func(() ->
        {
            Rotation rotation = Components.ROTATION.get(entity);
            if (rotation == null)
                return 0;
            Vector3f rotate = new Vector3f(0, 0, -1).rotate(rotation.toQuat());

            double _2PI = 2 * Math.PI;
            double x = rotate.x();
            double z = rotate.z();
            double theta = Math.atan2(-x, z);
            return (float) -Math.toDegrees((theta) % _2PI + Math.PI);
        }));

        functions.put("position", OrlangValue.func(D, (axis) ->
        {
            Position position = Components.POSITION.get(entity);
            if (position == null)
                return 0;
            if (axis == 0.0)
                return position.x();
            else if (axis == 1.0)
                return position.y();
            else if (axis == 2.0)
                return position.z();
            return 0;
        }));

        functions.put("total_linear_velocity", OrlangValue.func(() ->
        {
            LinearVelocity linearVelocity = Components.LINEAR_VELOCITY.get(entity);
            if (linearVelocity == null)
                return 0;
            return (double) Vector3f.length(linearVelocity.x(), linearVelocity.y(), linearVelocity.z());
        }));

        functions.put("anim_time", OrlangValue.func(() -> animTime));
        functions.put("any_animation_finished", OrlangValue.func(() -> anyAnimFinished));
        functions.put("all_animations_finished", OrlangValue.func(() -> allAnimsFinished));
    }

    @Override
    public void setAnimTime(double animTime)
    {
        this.animTime = animTime;
    }

    @Override
    public void setAnyAnimationFinished(boolean flag)
    {
        anyAnimFinished = flag;
    }

    @Override
    public void setAllAnimationsFinished(boolean flag)
    {
        allAnimsFinished = flag;
    }
}
