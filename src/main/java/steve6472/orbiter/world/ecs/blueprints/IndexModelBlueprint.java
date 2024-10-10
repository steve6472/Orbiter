package steve6472.orbiter.world.ecs.blueprints;

import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.ecs.core.Blueprint;
import steve6472.volkaniums.registry.VolkaniumsRegistries;

import java.util.Collection;
import java.util.List;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public record IndexModelBlueprint(Key modelKey) implements Blueprint<IndexModelBlueprint>
{
    public static final Key KEY = Key.defaultNamespace("model");
    public static final Codec<IndexModelBlueprint> CODEC = Key.CODEC.xmap(IndexModelBlueprint::new, IndexModelBlueprint::modelKey);

    @Override
    public Collection<?> createComponents()
    {
        return List.of(new IndexModel(VolkaniumsRegistries.STATIC_MODEL.get(modelKey)));
    }

    @Override
    public Codec<IndexModelBlueprint> codec()
    {
        return CODEC;
    }

    @Override
    public Key key()
    {
        return KEY;
    }
}
