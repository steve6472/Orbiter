package steve6472.orbiter.world.ecs.blueprints;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.world.ecs.components.physics.Gravity;
import steve6472.orbiter.world.ecs.components.physics.Mass;
import steve6472.orbiter.world.ecs.core.Blueprint;

import java.util.List;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public record MassBlueprint(float mass) implements Blueprint<MassBlueprint>
{
    public static final Key KEY = Constants.key("mass");
    public static final Codec<MassBlueprint> CODEC = Codec.FLOAT.xmap(MassBlueprint::new, MassBlueprint::mass);

    @Override
    public List<Component> createComponents()
    {
        return List.of(new Mass(mass));
    }

    @Override
    public Codec<MassBlueprint> codec()
    {
        return CODEC;
    }

    @Override
    public Key key()
    {
        return KEY;
    }
}
