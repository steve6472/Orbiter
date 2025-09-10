package steve6472.orbiter.world.ecs.blueprints;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.world.ecs.components.physics.AngularVelocity;
import steve6472.orbiter.world.ecs.components.physics.LinearVelocity;
import steve6472.orbiter.world.ecs.core.Blueprint;

import java.util.List;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public record LinearVelocityBlueprint(float x, float y, float z) implements Blueprint<LinearVelocityBlueprint>
{
    public static final Key KEY = Constants.key("linear_velocity");
    public static final Codec<LinearVelocityBlueprint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.optionalFieldOf("x", 0f).forGetter(LinearVelocityBlueprint::x),
        Codec.FLOAT.optionalFieldOf("y", 0f).forGetter(LinearVelocityBlueprint::y),
        Codec.FLOAT.optionalFieldOf("z", 0f).forGetter(LinearVelocityBlueprint::z)
    ).apply(instance, LinearVelocityBlueprint::new));

    @Override
    public List<Component> createComponents()
    {
        return List.of(new LinearVelocity(x, y, z));
    }

    @Override
    public Codec<LinearVelocityBlueprint> codec()
    {
        return CODEC;
    }

    @Override
    public Key key()
    {
        return KEY;
    }
}
