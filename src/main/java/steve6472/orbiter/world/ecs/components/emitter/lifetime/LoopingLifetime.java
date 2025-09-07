package steve6472.orbiter.world.ecs.components.emitter.lifetime;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;
import steve6472.orlang.codec.OrNumValue;

public class LoopingLifetime extends EmitterLifetime
{
    private static final int INFINITE_LOOP = -1;

    public static final Codec<LoopingLifetime> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        OrNumValue.CODEC.fieldOf("active_time").forGetter(o -> o.activeTime),
        OrNumValue.CODEC.optionalFieldOf("sleep_time", new OrNumValue(0)).forGetter(o -> o.sleepTime),
        Codec.INT.optionalFieldOf("max_loops", INFINITE_LOOP).forGetter(o -> o.maxLoops)
    ).apply(instance, LoopingLifetime::new));

    public final OrNumValue activeTime;
    public final OrNumValue sleepTime;
    public final int maxLoops;

    public State state;
    public int timesLooped;

    private long timestamp;
    private double currentTimer;

    public LoopingLifetime(OrNumValue activeTime, OrNumValue sleepTime, int maxLoops)
    {
        this.activeTime = activeTime;
        this.sleepTime = sleepTime;
        this.maxLoops = maxLoops;

        this.state = State.ACTIVE;
    }

    @Override
    public boolean isAlive(ParticleEmitter emitter)
    {
        return maxLoops == INFINITE_LOOP || timesLooped < maxLoops;
    }

    @Override
    public boolean shouldEmit(ParticleEmitter emitter)
    {
        if (state == State.ACTIVE)
        {
            if ((System.currentTimeMillis() - timestamp) / 1e3d >= currentTimer)
            {
                timestamp = System.currentTimeMillis();
                currentTimer = sleepTime.evaluateAndGet(emitter.environment);
                timesLooped++;
                state = State.ASLEEP;
            }
            return true;
        } else
        {
            if ((System.currentTimeMillis() - timestamp) / 1e3d >= currentTimer)
            {
                timestamp = System.currentTimeMillis();
                currentTimer = activeTime.evaluateAndGet(emitter.environment);
                state = State.ACTIVE;
                emitter.lastEmitterTick = timestamp;
            }
            return false;
        }
    }

    @Override
    protected EmitterLifetimeType<? extends EmitterLifetime> getType()
    {
        return EmitterLifetimeType.LOOPING_LIFETIME;
    }

    public enum State
    {
        ACTIVE, ASLEEP
    }
}
