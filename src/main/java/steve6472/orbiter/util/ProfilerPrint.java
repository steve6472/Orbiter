package steve6472.orbiter.util;

import steve6472.core.util.Profiler;

/**
 * Created by steve6472
 * Date: 10/11/2025
 * Project: Orbiter <br>
 */
public class ProfilerPrint
{
    public static void sout(Profiler profiler)
    {
        String t = "Last: %.4fms (%.4fms left) Avg: %.4fms Max: %.4fms".formatted(
            profiler.lastMilli(),
            ((1f / 60f) * 1e3) - profiler.lastMilli(),
            profiler.averageMilli(),
            profiler.maxEverMilli()
        );
        System.out.println(t);
    }

    public static void sout(Profiler profiler, String prefix, int count)
    {
        String t = "%s: %s, Last: %.4fms (%.4fms left) Avg: %.4fms Max: %.4fms".formatted(
            prefix,
            count,
            profiler.lastMilli(),
            ((1f / 60f) * 1e3) - profiler.lastMilli(),
            profiler.averageMilli(),
            profiler.maxEverMilli()
        );
        System.out.println(t);
    }
}
