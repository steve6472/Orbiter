package steve6472.orbiter.world.ecs.components.emitter.rate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;

public class InstantRate extends EmitterRate
{
    public static final Codec<InstantRate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("count").forGetter(InstantRate::count)
    ).apply(instance, InstantRate::new));

    private final int count;

    private InstantRate(int count)
    {
        this.count = count;
    }

    private int count()
    {
        return count;
    }

    @Override
    public int spawnCount(ParticleEmitter emitter)
    {
        return count();
    }

    @Override
    protected EmitterRateType<? extends EmitterRate> getType()
    {
        return EmitterRateType.INSTANT_RATE;
    }

    @Override
    public String toString()
    {
        return "InstantRate{" + "count=" + count + '}';
    }
}
