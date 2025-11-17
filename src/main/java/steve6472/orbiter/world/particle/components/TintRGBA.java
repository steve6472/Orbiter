package steve6472.orbiter.world.particle.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.world.particle.core.ParticleComponent;
import steve6472.orlang.codec.OrNumValue;

import java.util.List;

/**
 * Created by steve6472
 * Date: 8/29/2025
 * Project: Orbiter <br>
 */
public class TintRGBA implements ParticleComponent
{
    public OrNumValue r, g, b, a;

    private static final Codec<TintRGBA> CODEC_STRUCT = RecordCodecBuilder.create(instance -> instance.group(
        OrNumValue.CODEC.fieldOf("r").forGetter(o -> o.r),
        OrNumValue.CODEC.fieldOf("g").forGetter(o -> o.g),
        OrNumValue.CODEC.fieldOf("b").forGetter(o -> o.b),
        OrNumValue.CODEC.fieldOf("a").forGetter(o -> o.a)
    ).apply(instance, TintRGBA::new));

    private static final Codec<TintRGBA> CODEC_LIST = OrNumValue.CODEC.listOf(4, 4).flatXmap(list -> {
        if (list.size() != 4)
            return DataResult.error(() -> "List size incorrect, has to be 4");
        return DataResult.success(new TintRGBA(list.get(0), list.get(1), list.get(2), list.get(3)));
    }, o -> DataResult.success(List.of(o.r, o.g, o.b, o.a)));

    public static final Codec<TintRGBA> CODEC = Codec.withAlternative(CODEC_LIST, CODEC_STRUCT);

    public TintRGBA()
    {
    }

    private TintRGBA(OrNumValue r, OrNumValue g, OrNumValue b, OrNumValue a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @Override
    public void reset()
    {
        r = null;
        g = null;
        b = null;
        a = null;
    }

    public void setFrom(TintRGBA tintRGBA)
    {
        this.r = tintRGBA.r;
        this.g = tintRGBA.g;
        this.b = tintRGBA.b;
        this.a = tintRGBA.a;
    }
}
