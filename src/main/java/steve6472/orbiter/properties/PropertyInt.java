package steve6472.orbiter.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import steve6472.core.registry.Key;
import steve6472.orbiter.util.IntInterval;
import steve6472.orbiter.util.SettableObject;

/**
 * Created by steve6472
 * Date: 11/24/2025
 * Project: Orbiter <br>
 */
public record PropertyInt(SettableObject<Key> propertyKey, IntInterval range, int defaultValue) implements Property
{
    public static final Codec<PropertyInt> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        IntInterval.CODEC.optionalFieldOf("range", IntInterval.UNBOUNDED).forGetter(PropertyInt::range),
        Codec.INT.fieldOf("default").forGetter(PropertyInt::defaultValue)
    ).apply(instance, PropertyInt::new));

    public PropertyInt
    {
        if (!range.fitsInInterval(defaultValue))
            throw new IllegalStateException("Default value '%s' does not fit within range %s".formatted(defaultValue, range));
    }

    public PropertyInt(IntInterval range, int defaultValue)
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
        return ops.createInt((int) value);
    }

    @Override
    public <T> Object decode(DynamicOps<T> ops, T value)
    {
        int decodedValue = ops.getNumberValue(value).getOrThrow().intValue();
        if (!range.fitsInInterval(decodedValue))
            throw new IllegalStateException("Decoded value '%s' does not fit within range %s".formatted(decodedValue, range));
        return decodedValue;
    }

    @Override
    public void encodeBuffer(ByteBuf buffer, Object value)
    {
        buffer.writeInt((int) value);
    }

    @Override
    public Object decodeBuffer(ByteBuf buffer)
    {
        return buffer.readInt();
    }

    @Override
    public PropertyType<?> getType()
    {
        return PropertyType.INT;
    }
}
