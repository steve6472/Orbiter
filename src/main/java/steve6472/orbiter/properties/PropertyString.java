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
public record PropertyString(SettableObject<Key> propertyKey, String defaultValue) implements Property
{
    public static final Codec<PropertyString> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("default").forGetter(PropertyString::defaultValue)
    ).apply(instance, PropertyString::new));

    public PropertyString(String defaultValue)
    {
        this(SettableObject.create(), defaultValue);
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
        return ops.getStringValue(value).getOrThrow();
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
        return PropertyType.STRING;
    }
}
