package steve6472.orbiter.world.ecs.components.emitter.lifetime;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.orlang.codec.OrNumValue;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;

public class OnceLifetime extends EmitterLifetime
{
    public static final Codec<OnceLifetime> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        OrNumValue.CODEC.fieldOf("active_time").forGetter(OnceLifetime::activeTime)
    ).apply(instance, OnceLifetime::new));

    public OrNumValue activeTime;
    private long timestamp;

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
        if (!activeTime.hadFirstEval())
        {
            activeTime.evaluate(emitter.environment);
            timestamp = System.currentTimeMillis();
        }

        return (System.currentTimeMillis() - timestamp) / 1e3d >= activeTime().get();
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
