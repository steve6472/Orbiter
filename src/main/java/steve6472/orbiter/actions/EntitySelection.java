package steve6472.orbiter.actions;

import com.mojang.serialization.Codec;
import steve6472.core.registry.StringValue;

import java.util.Locale;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public enum EntitySelection implements StringValue
{
    UNSELECTED, INITIATOR, ITERATED;

    public static final Codec<EntitySelection> CODEC = StringValue.fromValues(EntitySelection::values);

    public static EntitySelection fromString(String s)
    {
        for (EntitySelection value : values())
        {
            if (value.stringValue().equals(s))
                return value;
        }
        throw new IllegalArgumentException("Unknown selection '%s'".formatted(s));
    }

    @Override
    public String stringValue()
    {
        return name().toLowerCase(Locale.ROOT);
    }
}
