package steve6472.orbiter.world.ecs.blueprints;

import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.core.Blueprint;
import steve6472.orbiter.world.ecs.core.Component;
import steve6472.volkaniums.registry.VolkaniumsRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public record TagsBlueprint(List<Key> tagKeys) implements Blueprint<TagsBlueprint>
{
    public static final Key KEY = Key.defaultNamespace("tags");
    public static final Codec<TagsBlueprint> CODEC = Key.CODEC.listOf().xmap(TagsBlueprint::new, TagsBlueprint::tagKeys);

    @Override
    public Collection<?> createComponents()
    {
        List<Object> components = new ArrayList<>(tagKeys.size());
        for (Key tagKey : tagKeys)
        {
            Component<?> component = Registries.COMPONENT.get(tagKey);
            components.add(Tag.getTagInstance(component.componentClass()));
        }
        return components;
    }

    @Override
    public Codec<TagsBlueprint> codec()
    {
        return CODEC;
    }

    @Override
    public Key key()
    {
        return KEY;
    }
}
