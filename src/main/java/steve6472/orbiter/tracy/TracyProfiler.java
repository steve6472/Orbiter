package steve6472.orbiter.tracy;

import io.github.benjaminamos.tracy.Tracy;
import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Created by steve6472
 * Date: 11/5/2025
 * Project: Orbiter <br>
 */
public class TracyProfiler implements IProfiler
{
    private final String name;
    Stack<Tracy.ZoneContext> zones = new ObjectArrayList<>(8);

    public TracyProfiler(String name)
    {
        this.name = name;
    }

    @Override
    public void start()
    {
        // noop I guess ?
        push(name, 0x559e81);
    }

    @Override
    public void end()
    {
        pop();
    }

    @Override
    public void push(String name, int color)
    {
        long handle = Tracy.allocSourceLocation(0, "source", "function", name, color);
        Tracy.ZoneContext zoneContext = Tracy.zoneBegin(handle, 1);
        zones.push(zoneContext);
    }

    @Override
    public void pop()
    {
        Tracy.ZoneContext pop = zones.pop();
        Tracy.zoneEnd(pop);
    }

    @Override
    public void push(String name)
    {
        push(name, 0);
    }

    @Override
    public void popPush(String name)
    {
        popPush(name, 0);
    }

    @Override
    public void popPush(String name, int color)
    {
        pop();
        push(name, color);
    }
}
