package steve6472.orbiter.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import steve6472.core.registry.Key;
import steve6472.orbiter.util.DoubleInterval;
import steve6472.orbiter.util.SettableObject;

/**
 * Created by steve6472
 * Date: 11/24/2025
 * Project: Orbiter <br>
 */
public record PropertyDouble(SettableObject<Key> propertyKey, DoubleInterval range, double defaultValue) implements Property
{
    public static final Codec<PropertyDouble> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        steve6472.orbiter.util.DoubleInterval.CODEC.optionalFieldOf("range", steve6472.orbiter.util.DoubleInterval.UNBOUNDED).forGetter(PropertyDouble::range),
        Codec.DOUBLE.fieldOf("default").forGetter(PropertyDouble::defaultValue)
    ).apply(instance, PropertyDouble::new));

    public PropertyDouble
    {
        if (!range.fitsInInterval(defaultValue))
            throw new IllegalStateException("Default value '%s' does not fit within range %s".formatted(defaultValue, range));
    }

    public PropertyDouble(DoubleInterval range, double defaultValue)
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
        return ops.createDouble((double) value);
    }

    @Override
    public <T> Object decode(DynamicOps<T> ops, T value)
    {
        double decodedValue = ops.getNumberValue(value).getOrThrow().doubleValue();
        if (!range.fitsInInterval(decodedValue))
            throw new IllegalStateException("Decoded value '%s' does not fit within range %s".formatted(decodedValue, range));
        return decodedValue;
    }

    @Override
    public void encodeBuffer(ByteBuf buffer, Object value)
    {
        buffer.writeDouble((double) value);
    }

    @Override
    public Object decodeBuffer(ByteBuf buffer)
    {
        return buffer.readDouble();
    }

    @Override
    public PropertyType<?> getType()
    {
        return PropertyType.DOUBLE;
    }
}
