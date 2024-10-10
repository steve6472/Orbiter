package steve6472.orbiter.world.ecs;

import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.ecs.blueprints.TagsBlueprint;
import steve6472.orbiter.world.ecs.core.Blueprint;
import steve6472.orbiter.world.ecs.core.BlueprintEntry;
import steve6472.orbiter.world.ecs.blueprints.IndexModelBlueprint;
import steve6472.orbiter.world.ecs.blueprints.PositionBlueprint;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public class Blueprints
{
    public static final BlueprintEntry<PositionBlueprint> POSITION = register(PositionBlueprint.KEY, PositionBlueprint.CODEC);
    public static final BlueprintEntry<IndexModelBlueprint> MODEL = register(IndexModelBlueprint.KEY, IndexModelBlueprint.CODEC);
    public static final BlueprintEntry<TagsBlueprint> TAGS = register(TagsBlueprint.KEY, TagsBlueprint.CODEC);

    private static <T extends Blueprint<?>> BlueprintEntry<T> register(Key key, Codec<T> codec)
    {
        if (Registries.BLUEPRINT.get(key) != null)
            throw new RuntimeException("Blueprint with key " + key + " already exists!");

        BlueprintEntry<T> obj = new BlueprintEntry<>(key, codec);
        Registries.BLUEPRINT.register(key, obj);
        return obj;
    }
}
