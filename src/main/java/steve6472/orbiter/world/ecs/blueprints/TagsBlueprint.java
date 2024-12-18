package steve6472.orbiter.world.ecs.blueprints;

import com.mojang.serialization.Codec;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.core.Blueprint;
import steve6472.orbiter.world.ecs.core.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public record TagsBlueprint(List<Key> tagKeys) implements Blueprint<TagsBlueprint>
{
    private static final Logger LOGGER = Log.getLogger(TagsBlueprint.class);

    public static final Key KEY = Key.defaultNamespace("tags");
    public static final Codec<TagsBlueprint> CODEC = Key.CODEC.listOf().xmap(TagsBlueprint::new, TagsBlueprint::tagKeys);

    @Override
    public List<?> createComponents()
    {
        List<Object> components = new ArrayList<>(tagKeys.size());
        for (Key tagKey : tagKeys)
        {
            Component<?> component = Registries.COMPONENT.get(tagKey);
            if (component == null)
            {
                LOGGER.severe("Tag \"" + tagKey + "\" not found!");
                return List.of();
            }

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
