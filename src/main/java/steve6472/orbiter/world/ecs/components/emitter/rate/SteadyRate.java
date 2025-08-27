package steve6472.orbiter.world.ecs.components.emitter.rate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.orlang.codec.OrNumValue;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;

public class SteadyRate extends EmitterRate
{
    // TODO: this steady rate does not work as per spec
    public static final Codec<SteadyRate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        OrNumValue.CODEC.fieldOf("spawn_rate").forGetter(SteadyRate::spawnRate),
        OrNumValue.CODEC.fieldOf("max_count").forGetter(SteadyRate::maxCount)
    ).apply(instance, SteadyRate::new));

    public OrNumValue spawnRate;
    public OrNumValue maxCount;

    public SteadyRate(OrNumValue spawnRate, OrNumValue maxCount)
    {
        this.spawnRate = spawnRate;
        this.maxCount = maxCount;
    }

    private OrNumValue spawnRate()
    {
        return spawnRate;
    }

    private OrNumValue maxCount()
    {
        return maxCount;
    }

    @Override
    public int spawnCount(ParticleEmitter emitter)
    {
//        return Math.max(0, Math.min(maxCount() - emitter.trackedParticles.size(), spawnRate()));
        return (int) Math.max(0, Math.min(maxCount().evaluateAndGet(emitter.environment), spawnRate().evaluateAndGet(emitter.environment)));
    }

    @Override
    protected EmitterRateType<? extends EmitterRate> getType()
    {
        return EmitterRateType.STEADY_RATE;
    }

    @Override
    public String toString()
    {
        return "SteadyRate{" + "spawnRate=" + spawnRate + ", maxCount=" + maxCount + '}';
    }
}
