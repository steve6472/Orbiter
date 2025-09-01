package steve6472.orbiter.rendering;

import com.mojang.serialization.Codec;
import steve6472.core.registry.StringValue;

import java.util.Locale;

/**
 * Created by steve6472
 * Date: 9/1/2025
 * Project: Orbiter <br>
 */
public enum Billboard implements StringValue
{
    FIXED,
    ROTATE_XYZ,
    ROTATE_Y,
    LOOKAT_XYZ,
    LOOKAT_Y,
    DIRECTION_X,
    DIRECTION_Y,
    DIRECTION_Z,
    DERIVE_FROM_VELOCITY,
    CUSTOM_DIRECTION;

    public static final Codec<Billboard> CODEC = StringValue.fromValues(Billboard::values);

    @Override
    public String stringValue()
    {
        return name().toLowerCase(Locale.ROOT);
    }
}
