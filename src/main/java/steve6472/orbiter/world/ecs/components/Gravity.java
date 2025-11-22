package steve6472.orbiter.world.ecs.components;

import com.badlogic.ashley.core.Component;
import org.joml.Vector3f;
import steve6472.orbiter.Constants;

/**
 * Created by steve6472
 * Date: 11/22/2025
 * Project: Orbiter <br>
 */
public record Gravity(Vector3f value) implements Component
{
    public Gravity()
    {
        this(new Vector3f(Constants.GRAVITY));
    }
}
