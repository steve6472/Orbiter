package steve6472.orbiter.world.ecs.blueprints;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.flare.registry.FlareRegistries;
import steve6472.orbiter.Constants;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.ecs.core.Blueprint;

import java.util.List;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public record IndexModelBlueprint(Key modelKey) implements Blueprint<IndexModelBlueprint>
{
    public static final Key KEY = Constants.key("model");
    public static final Codec<IndexModelBlueprint> CODEC = Constants.KEY_CODEC.xmap(IndexModelBlueprint::new, IndexModelBlueprint::modelKey);

    @Override
    public List<Component> createComponents()
    {
        return List.of(new IndexModel(FlareRegistries.STATIC_MODEL.get(modelKey)));
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
