package steve6472.orbiter.world.ecs.components.emitter.lifetime;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.StringValue;

public class LoopingLifetime extends EmitterLifetime
{
    private static final int INFINITE_LOOP = -1;

    public static final Codec<LoopingLifetime> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("ticks_active").forGetter(LoopingLifetime::ticksActive),
        Codec.INT.fieldOf("ticks_asleep").forGetter(LoopingLifetime::ticksAsleep),
        Codec.INT.optionalFieldOf("loop_count", INFINITE_LOOP).forGetter(LoopingLifetime::loopCount),
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

    public final int ticksActive;
    public final int ticksAsleep;
    public final int loopCount;

    public State state;
    public int timer;
    public int timesLooped;

    public LoopingLifetime(int ticksActive, int ticksAsleep, int loopCount)
    {
        this.ticksActive = ticksActive;
        this.ticksAsleep = ticksAsleep;
        this.loopCount = loopCount;

        this.state = State.ACTIVE;
    }

    private int ticksActive()
    {
        return ticksActive;
    }

    private int ticksAsleep()
    {
        return ticksAsleep;
    }

    private int loopCount()
    {
        return loopCount;
    }

    @Override
    public boolean isAlive(int ticksAlive)
    {
        return loopCount == INFINITE_LOOP || timesLooped < loopCount();
    }

    @Override
    public boolean shouldEmit(int ticksAlive)
    {
        if (state == State.ACTIVE)
        {
            timer++;
            if (timer >= ticksActive())
            {
                timer = 0;
                timesLooped++;
                state = State.ASLEEP;
            }
            return true;
        } else
        {
            timer++;
            if (timer >= ticksAsleep())
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
        return "LoopingLifetime{" + "ticksActive=" + ticksActive + ", ticksAsleep=" + ticksAsleep + ", loopCount=" + loopCount + ", state=" + state + ", timer=" + timer + '}';
    }
}
