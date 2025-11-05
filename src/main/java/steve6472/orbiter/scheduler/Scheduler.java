package steve6472.orbiter.scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by steve6472
 * Date: 10/16/2024
 * Project: Orbiter <br>
 */
public class Scheduler
{
    /*
     * Singleton stuff
     */
    private static Scheduler instance;

    private Scheduler() {}

    /// deprecated - internal use only
    @Deprecated
    public static Scheduler instance()
    {
        if (instance == null)
            instance = new Scheduler();
        return instance;
    }

    /*
     * Instance stuff
     */

    private final List<DelayTask> tasks = new ArrayList<>(64);

    public static void clearAllTasks()
    {
        instance.tasks.clear();
    }

    private static class DelayTask
    {
        private final ScheduledTask task;
        public int delayTicks;

        private DelayTask(ScheduledTask task, int delayTicks)
        {
            this.task = task;
            this.delayTicks = delayTicks;
        }
    }

    public void tick()
    {
        for (Iterator<DelayTask> iterator = tasks.iterator(); iterator.hasNext(); )
        {
            DelayTask task = iterator.next();
            task.delayTicks--;

            if (task.task.isCancelled())
            {
                iterator.remove();
                continue;
            }

            if (task.delayTicks <= 0)
            {
                if (task.task.isRepeating())
                    task.delayTicks = task.task.repeatInterval();
                else
                    iterator.remove();

                task.task.runnable.run();
            }
        }
    }

    /*
     * Runner methods
     */

    private static Task addTaskWithDelay(ScheduledTask task, int runDelay)
    {
        instance().tasks.add(new DelayTask(task, runDelay));
        return task;
    }

    private static Task addTask(ScheduledTask task)
    {
        return addTaskWithDelay(task, 0);
    }

    public static Task runTaskLater(Runnable runnable)
    {
        return addTask(new ScheduledTask(runnable, false, 0));
    }

    public static Task runTaskLater(Runnable runnable, int runDelay)
    {
        return addTaskWithDelay(new ScheduledTask(runnable, false, 0), runDelay);
    }
}
