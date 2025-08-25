package steve6472.orbiter.world.ecs.components.emitter.rate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;

public class SteadyRate extends EmitterRate
{
    public static final Codec<SteadyRate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("spawn_rate").forGetter(SteadyRate::spawnRate),
        Codec.INT.fieldOf("max_count").forGetter(SteadyRate::maxCount)
    ).apply(instance, SteadyRate::new));

    private final int spawnRate;
    private final int maxCount;

    public SteadyRate(int spawnRate, int maxCount)
    {
        this.spawnRate = spawnRate;
        this.maxCount = maxCount;
    }

    private int spawnRate()
    {
        return spawnRate;
    }

    private int maxCount()
    {
        return maxCount;
    }

    @Override
    public int spawnCount(ParticleEmitter emitter)
    {
//        return Math.max(0, Math.min(maxCount() - emitter.trackedParticles.size(), spawnRate()));
        return Math.max(0, Math.min(maxCount(), spawnRate()));
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
