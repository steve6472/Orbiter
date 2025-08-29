package steve6472.orbiter.world.particle.core;

import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.core.registry.Serializable;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public record PCBlueprintEntry<T extends PCBlueprint<?>>(Key key, Codec<T> codec) implements Keyable, Serializable<T>
{
}
