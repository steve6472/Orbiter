package steve6472.orbiter.world.ecs.blueprints;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.world.ecs.components.physics.AngularFactor;
import steve6472.orbiter.world.ecs.core.Blueprint;

import java.util.List;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public record AngularFactorBlueprint(float x, float y, float z) implements Blueprint<AngularFactorBlueprint>
{
    public static final Key KEY = Constants.key("angular_factor");
    public static final Codec<AngularFactorBlueprint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.optionalFieldOf("x", 1f).forGetter(AngularFactorBlueprint::x),
        Codec.FLOAT.optionalFieldOf("y", 1f).forGetter(AngularFactorBlueprint::y),
        Codec.FLOAT.optionalFieldOf("z", 1f).forGetter(AngularFactorBlueprint::z)
    ).apply(instance, AngularFactorBlueprint::new));

    @Override
    public List<Component> createComponents()
    {
        return List.of(new AngularFactor(x, y, z));
    }

    @Override
    public Codec<AngularFactorBlueprint> codec()
    {
        return CODEC;
    }

    @Override
    public Key key()
    {
        return KEY;
    }
}
