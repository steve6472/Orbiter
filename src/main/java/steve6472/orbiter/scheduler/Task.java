package steve6472.orbiter.scheduler;

/**
 * Created by steve6472
 * Date: 10/16/2024
 * Project: Orbiter <br>
 */
public interface Task
{
    void cancel();
    boolean isCancelled();

    boolean isRepeating();
    int repeatInterval();
}
