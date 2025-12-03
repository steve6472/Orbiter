package steve6472.orbiter.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.util.RandomUtil;

import java.util.List;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public class DoubleInterval
{
    private static final Codec<DoubleInterval> CODEC_LIST = Codec.DOUBLE.listOf(2, 2).xmap(list -> new DoubleInterval(list.getFirst(), list.get(1)), interval -> List.of(interval.min, interval.max));

    private static final Codec<DoubleInterval> CODEC_FULL = RecordCodecBuilder.create(instance -> instance.group(
        Codec.DOUBLE.fieldOf("min").forGetter(DoubleInterval::min),
        Codec.DOUBLE.fieldOf("max").forGetter(DoubleInterval::max)
    ).apply(instance, DoubleInterval::new));

    public static final Codec<DoubleInterval> CODEC = Codec.withAlternative(Codec.withAlternative(CODEC_LIST, CODEC_FULL), Codec.INT, DoubleInterval::new);

    public static final BufferCodec<ByteBuf, DoubleInterval> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.DOUBLE, DoubleInterval::min,
        BufferCodecs.DOUBLE, DoubleInterval::max,
        DoubleInterval::new
    );

    public static final DoubleInterval UNBOUNDED = new DoubleInterval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

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

    public boolean fitsInInterval(double number)
    {
        return number >= min && number <= max;
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
