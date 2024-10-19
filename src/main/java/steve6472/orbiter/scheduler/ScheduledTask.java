package steve6472.orbiter.scheduler;

/**
 * Created by steve6472
 * Date: 10/16/2024
 * Project: Orbiter <br>
 */
public class ScheduledTask implements Task
{
    private final boolean repeating;
    private final int repeatInterval;

    final Runnable runnable;

    private boolean cancelled;

    ScheduledTask(Runnable runnable, boolean repeating, int repeatInterval)
    {
        this.runnable = runnable;
        this.repeating = repeating;
        this.repeatInterval = repeatInterval;
    }

    @Override
    public void cancel()
    {
        cancelled = true;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public boolean isRepeating()
    {
        return repeating;
    }

    @Override
    public int repeatInterval()
    {
        return repeatInterval;
    }
}
