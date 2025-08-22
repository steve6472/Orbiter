package steve6472.orbiter.world.ecs.blueprints;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.orbiter.world.ecs.components.physics.Gravity;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.core.Blueprint;

import java.util.Collection;
import java.util.List;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public record GravityBlueprint(float x, float y, float z) implements Blueprint<GravityBlueprint>
{
    public static final Key KEY = Key.defaultNamespace("gravity");
    public static final Codec<GravityBlueprint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("x").forGetter(GravityBlueprint::x),
        Codec.FLOAT.fieldOf("y").forGetter(GravityBlueprint::y),
        Codec.FLOAT.fieldOf("z").forGetter(GravityBlueprint::z)
    ).apply(instance, GravityBlueprint::new));

    @Override
    public List<Component> createComponents()
    {
        return List.of(new Gravity(x, y, z));
    }

    @Override
    public Codec<GravityBlueprint> codec()
    {
        return CODEC;
    }

    @Override
    public Key key()
    {
        return KEY;
    }
}
