package steve6472.orbiter.world.emitter.lifetime;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.world.emitter.ParticleEmitter;
import steve6472.orlang.codec.OrNumValue;

public class OnceLifetime extends EmitterLifetime
{
    public static final Codec<OnceLifetime> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        OrNumValue.CODEC.fieldOf("active_time").forGetter(OnceLifetime::activeTime)
    ).apply(instance, OnceLifetime::new));

    public OrNumValue activeTime;
    private long timestamp;
    private double activeTimeN = Double.NaN;

    public OnceLifetime(OrNumValue activeTime)
    {
        this.activeTime = activeTime;
    }

    public OrNumValue activeTime()
    {
        return activeTime;
    }

    @Override
    public boolean isAlive(ParticleEmitter emitter)
    {
        if (Double.isNaN(activeTimeN))
        {
            activeTimeN = activeTime.evaluateAndGet(emitter.environment);
            timestamp = System.currentTimeMillis();
        }

        long l = System.currentTimeMillis() - timestamp;
        if (l == 0)
            return true;

        return l / 1e3d < activeTimeN;
    }

    @Override
    public boolean shouldEmit(ParticleEmitter emitter)
    {
        return true;
    }

    @Override
    protected EmitterLifetimeType<? extends EmitterLifetime> getType()
    {
        return EmitterLifetimeType.ONCE_LIFETIME;
    }
}
