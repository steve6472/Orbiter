package steve6472.orbiter.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodecs;
import steve6472.core.registry.Key;
import steve6472.orbiter.util.SettableObject;

import java.util.List;
import java.util.Set;

/**
 * Created by steve6472
 * Date: 11/24/2025
 * Project: Orbiter <br>
 */
public record PropertyEnum(SettableObject<Key> propertyKey, Set<String> range, String defaultValue) implements Property
{
    public static final Codec<PropertyEnum> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.listOf().fieldOf("range").xmap(Set::copyOf, List::copyOf).forGetter(PropertyEnum::range),
        Codec.STRING.fieldOf("default").forGetter(PropertyEnum::defaultValue)
    ).apply(instance, PropertyEnum::new));

    public PropertyEnum
    {
        if (!range.contains(defaultValue))
            throw new IllegalStateException("Default value '%s' does not fit within range %s".formatted(defaultValue, range));
    }

    public PropertyEnum(Set<String> range, String defaultValue)
    {
        this(SettableObject.create(), range, defaultValue);
    }

    @Override
    public Object getDefaultValue()
    {
        return defaultValue;
    }

    @Override
    public <T> T encodeDynamic(DynamicOps<T> ops, Object value)
    {
        return ops.createString((String) value);
    }

    @Override
    public <T> Object decode(DynamicOps<T> ops, T value)
    {
        String decodedValue = ops.getStringValue(value).getOrThrow();
        if (!range.contains(decodedValue))
            throw new IllegalStateException("Decoded value '%s' does not fit within range %s".formatted(decodedValue, range));
        return decodedValue;
    }

    @Override
    public void encodeBuffer(ByteBuf buffer, Object value)
    {
        BufferCodecs.STRING.encode(buffer, (String) value);
    }

    @Override
    public Object decodeBuffer(ByteBuf buffer)
    {
        return BufferCodecs.STRING.decode(buffer);
    }

    @Override
    public PropertyType<?> getType()
    {
        return PropertyType.ENUM;
    }
}
