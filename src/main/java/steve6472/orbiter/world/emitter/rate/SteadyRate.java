package steve6472.orbiter.world.emitter.rate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.world.emitter.ParticleEmitter;
import steve6472.orlang.codec.OrNumValue;

public class SteadyRate extends EmitterRate
{
    // TODO: this steady rate does not work as per spec
    public static final Codec<SteadyRate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        OrNumValue.CODEC.fieldOf("spawn_rate").forGetter(SteadyRate::spawnRate),
        OrNumValue.CODEC.fieldOf("max_count").forGetter(SteadyRate::maxCount)
    ).apply(instance, SteadyRate::new));

    public OrNumValue spawnRate;
    public OrNumValue maxCount;
    private double accumulator = 0.0;

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
        // rate is in particles/second
        double r = spawnRate.evaluateAndGet(emitter.environment);
        double perTick = r / 60.0; // emitter ticks 60 times per second

        accumulator += perTick;
        int toSpawn = (int) accumulator; // whole particles to spawn this tick
        accumulator -= toSpawn; // keep the fractional part

        return toSpawn;
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
