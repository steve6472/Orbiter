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
public class IntInterval
{
    private static final Codec<IntInterval> CODEC_LIST = Codec.INT.listOf(2, 2).xmap(list -> new IntInterval(list.getFirst(), list.get(1)), interval -> List.of(interval.min, interval.max));

    private static final Codec<IntInterval> CODEC_FULL = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("min").forGetter(IntInterval::min),
        Codec.INT.fieldOf("max").forGetter(IntInterval::max)
    ).apply(instance, IntInterval::new));

    public static final Codec<IntInterval> CODEC = Codec.withAlternative(Codec.withAlternative(CODEC_LIST, CODEC_FULL), Codec.INT, IntInterval::new);

    public static final BufferCodec<ByteBuf, IntInterval> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.INT, IntInterval::min,
        BufferCodecs.INT, IntInterval::max,
        IntInterval::new
    );

    public static final IntInterval UNBOUNDED = new IntInterval(Integer.MIN_VALUE, Integer.MAX_VALUE);

    public int min, max;

    public IntInterval(int min, int max)
    {
        this.min = min;
        this.max = max;
    }

    public IntInterval(int interval)
    {
        this.min = max = interval;
    }

    public boolean fitsInInterval(int number)
    {
        return number >= min && number <= max;
    }

    public int min()
    {
        return min;
    }

    public int max()
    {
        return max;
    }

    public int getRandom()
    {
        return RandomUtil.randomInt(min, max);
    }

    @Override
    public String toString()
    {
        return "IntInterval[" + min + ", " + max + ']';
    }
}
