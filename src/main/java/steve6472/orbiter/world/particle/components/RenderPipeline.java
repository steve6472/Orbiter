package steve6472.orbiter.world.particle.components;

import com.mojang.serialization.Codec;
import steve6472.core.registry.StringValue;
import steve6472.orbiter.world.particle.core.ParticleComponent;

import java.util.Locale;

/**
 * Created by steve6472
 * Date: 8/29/2025
 * Project: Orbiter <br>
 */
public class RenderPipeline implements ParticleComponent
{
    public Enum value;

    @Override
    public void reset()
    {
        value = Enum.MODEL;
    }

    public enum Enum implements StringValue
    {
        MODEL,
        MODEL_UNSHADED,
        MODEL_UNSHADED_TINTED,
        MODEL_UNSHADED_TINTED_ADDITIVE,
        MODEL_ADDITIVE,
        MODEL_UNSHADED_ADDITIVE;

        public static final Codec<Enum> CODEC = StringValue.fromValues(Enum::values);

        @Override
        public String stringValue()
        {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
