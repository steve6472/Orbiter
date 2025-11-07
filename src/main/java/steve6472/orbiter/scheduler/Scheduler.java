package steve6472.orbiter.scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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
    private final List<DelayTask> pendingTasks = new ArrayList<>(32); // tasks added during tick

    private boolean ticking = false; // flag to prevent direct modification during iteration

    public static void clearAllTasks()
    {
        synchronized (instance.tasks)
        {
            instance.tasks.clear();
            instance.pendingTasks.clear();
        }
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
        synchronized (tasks)
        {
            ticking = true;
            try
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

                        // Run the actual task (may create new tasks)
                        task.task.runnable.run();
                    }
                }
            }
            finally
            {
                ticking = false;

                // Merge new tasks safely
                if (!pendingTasks.isEmpty())
                {
                    tasks.addAll(pendingTasks);
                    pendingTasks.clear();
                }
            }
        }
    }

    /*
     * Runner methods
     */
    private static Task addTaskWithDelay(ScheduledTask task, int runDelay)
    {
        DelayTask delayTask = new DelayTask(task, runDelay);

        synchronized (instance.tasks)
        {
            if (instance.ticking)
                instance.pendingTasks.add(delayTask);
            else
                instance.tasks.add(delayTask);
        }

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
