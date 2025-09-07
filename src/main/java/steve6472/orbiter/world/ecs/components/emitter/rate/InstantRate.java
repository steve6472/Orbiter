package steve6472.orbiter.world.ecs.components.emitter.rate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;
import steve6472.orlang.codec.OrNumValue;

public class InstantRate extends EmitterRate
{
    public static final Codec<InstantRate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        OrNumValue.CODEC.fieldOf("count").forGetter(InstantRate::count)
    ).apply(instance, InstantRate::new));

    public final OrNumValue count;

    private InstantRate(OrNumValue count)
    {
        this.count = count;
    }

    private OrNumValue count()
    {
        return count;
    }

    @Override
    public int spawnCount(ParticleEmitter emitter)
    {
        return (int) count.evaluateAndGet(emitter.environment);
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
