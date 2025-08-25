package steve6472.orbiter.world.ecs.components.emitter.lifetime;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class OnceLifetime extends EmitterLifetime
{
    public static final Codec<OnceLifetime> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("active_time").forGetter(OnceLifetime::activeTime)
    ).apply(instance, OnceLifetime::new));

    private final int activeTime;

    public OnceLifetime(int activeTime)
    {
        if (activeTime <= 0)
            throw new RuntimeException("Value out of bound, only >= 1 allowed, got " + activeTime + " (" + activeTime + ")");
        this.activeTime = activeTime;
    }

    public int activeTime()
    {
        return activeTime;
    }

    @Override
    public boolean isAlive(int ticksAlive)
    {
        return ticksAlive < activeTime();
    }

    @Override
    public boolean shouldEmit(int ticksAlive)
    {
        return true;
    }

    @Override
    protected EmitterLifetimeType<? extends EmitterLifetime> getType()
    {
        return EmitterLifetimeType.ONCE_LIFETIME;
    }
}
