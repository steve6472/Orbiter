package steve6472.orbiter.world.ecs.components;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.util.ComponentCodec;

/**
 * Created by steve6472
 * Date: 11/25/2025
 * Project: Orbiter <br>
 */
public record BlueprintReference(Key key) implements Component
{
    public static final Codec<BlueprintReference> CODEC = ComponentCodec.xmap(Constants.KEY_CODEC, key -> () -> new BlueprintReference(key), BlueprintReference::key);
}
