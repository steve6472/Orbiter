package steve6472.orbiter.tracy;

import io.github.benjaminamos.tracy.Tracy;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 11/5/2025
 * Project: Orbiter <br>
 */
public class OrbiterProfiler
{
    private final static Map<String, IProfiler> TRACY_PROFILER = new HashMap<>();

    public static IProfiler get()
    {
        return get("main");
    }

    public static IProfiler frame()
    {
        return get("Frame");
    }

    public static IProfiler world()
    {
        return get("World");
    }

    public static IProfiler get(String name)
    {
        return TRACY_PROFILER.computeIfAbsent(name, TracyProfiler::new);
    }

    public static void endFrame()
    {
        Tracy.markFrame();
    }
}