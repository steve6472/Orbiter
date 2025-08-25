package steve6472.orbiter.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.util.RandomUtil;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public class DoubleInterval
{
    private static final Codec<DoubleInterval> CODEC_FULL = RecordCodecBuilder.create(instance -> instance.group(
        Codec.DOUBLE.fieldOf("min").forGetter(DoubleInterval::min),
        Codec.DOUBLE.fieldOf("max").forGetter(DoubleInterval::max)
    ).apply(instance, DoubleInterval::new));

    public static final Codec<DoubleInterval> CODEC = Codec.withAlternative(CODEC_FULL, Codec.DOUBLE, DoubleInterval::new);

    public static final BufferCodec<ByteBuf, DoubleInterval> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.DOUBLE, DoubleInterval::min,
        BufferCodecs.DOUBLE, DoubleInterval::max,
        DoubleInterval::new
    );

    public double min, max;

    public DoubleInterval(double min, double max)
    {
        this.min = min;
        this.max = max;
    }

    public DoubleInterval(double interval)
    {
        this.min = max = interval;
    }

    public double min()
    {
        return min;
    }

    public double max()
    {
        return max;
    }

    public double getRandom()
    {
        return RandomUtil.randomDouble(min, max);
    }

    @Override
    public String toString()
    {
        return "DoubleInterval[" + min + ", " + max + ']';
    }
}
