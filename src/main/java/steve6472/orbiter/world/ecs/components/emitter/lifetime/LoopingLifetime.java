package steve6472.orbiter.world.ecs.components.emitter.lifetime;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.StringValue;
import steve6472.orbiter.orlang.codec.OrNumValue;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;

public class LoopingLifetime extends EmitterLifetime
{
    private static final int INFINITE_LOOP = -1;

    public static final Codec<LoopingLifetime> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        OrNumValue.CODEC.fieldOf("ticks_active").forGetter(LoopingLifetime::ticksActive),
        OrNumValue.CODEC.fieldOf("ticks_asleep").forGetter(LoopingLifetime::ticksAsleep),
        Codec.INT.optionalFieldOf("max_loop_count", INFINITE_LOOP).forGetter(LoopingLifetime::maxLoopCount),
        State.CODEC.optionalFieldOf("state", State.ACTIVE).forGetter(o -> o.state),
        Codec.INT.optionalFieldOf("timer", 0).forGetter(o -> o.timer),
        Codec.INT.optionalFieldOf("times_looped", 0).forGetter(o -> o.timesLooped)
    ).apply(instance, (ticksActive, ticksAsleep, loopCount, state, timer, timesLooped) -> {
        LoopingLifetime lifetime = new LoopingLifetime(ticksActive, ticksAsleep, loopCount);
        lifetime.state = state;
        lifetime.timer = timer;
        lifetime.timesLooped = timesLooped;
        return lifetime;
    }));

    public final OrNumValue ticksActive;
    public final OrNumValue ticksAsleep;
    public final int maxLoopCount;

    public State state;
    public int timer;
    public int timesLooped;

    public LoopingLifetime(OrNumValue ticksActive, OrNumValue ticksAsleep, int maxLoopCount)
    {
        this.ticksActive = ticksActive;
        this.ticksAsleep = ticksAsleep;
        this.maxLoopCount = maxLoopCount;

        this.state = State.ACTIVE;
    }

    private OrNumValue ticksActive()
    {
        return ticksActive;
    }

    private OrNumValue ticksAsleep()
    {
        return ticksAsleep;
    }

    private int maxLoopCount()
    {
        return maxLoopCount;
    }

    @Override
    public boolean isAlive(ParticleEmitter emitter, int ticksAlive)
    {
        return maxLoopCount == INFINITE_LOOP || timesLooped < maxLoopCount();
    }

    @Override
    public boolean shouldEmit(ParticleEmitter emitter, int ticksAlive)
    {
        if (state == State.ACTIVE)
        {
            if (timer == 0)
                ticksActive.evaluate(emitter.environment);

            timer++;
            if (timer >= ticksActive.get())
            {
                timer = 0;
                timesLooped++;
                state = State.ASLEEP;
            }
            return true;
        } else
        {
            if (timer == 0)
                ticksActive.evaluate(emitter.environment);

            timer++;
            if (timer >= ticksAsleep.get())
            {
                timer = 0;
                state = State.ACTIVE;
            }
            return false;
        }
    }

    @Override
    protected EmitterLifetimeType<? extends EmitterLifetime> getType()
    {
        return EmitterLifetimeType.LOOPING_LIFETIME;
    }

    public enum State implements StringValue
    {
        ACTIVE("active"), ASLEEP("asleep");

        private static final Codec<State> CODEC = StringValue.fromValues(State::values);

        private final String id;

        State(String id)
        {
            this.id = id;
        }

        @Override
        public String stringValue()
        {
            return id;
        }
    }

    @Override
    public String toString()
    {
        return "LoopingLifetime{" + "ticksActive=" + ticksActive + ", ticksAsleep=" + ticksAsleep + ", loopCount=" + maxLoopCount + ", state=" + state + ", timer=" + timer + '}';
    }
}
