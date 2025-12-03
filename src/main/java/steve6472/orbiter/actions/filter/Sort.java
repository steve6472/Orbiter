package steve6472.orbiter.actions.filter;

import com.mojang.serialization.Codec;
import steve6472.core.registry.StringValue;

import java.util.Locale;

/**
 * Created by steve6472
 * Date: 11/28/2025
 * Project: Orbiter <br>
 */
public enum Sort implements StringValue
{
    ARBITRARY,
    NEAREST,
    FURTHEST,
    RANDOM;

    public static final Codec<Sort> CODEC = StringValue.fromValues(Sort::values);

    @Override
    public String stringValue()
    {
        return name().toLowerCase(Locale.ROOT);
    }
}
