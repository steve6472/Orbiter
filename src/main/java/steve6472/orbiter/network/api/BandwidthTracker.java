package steve6472.orbiter.network.api;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import steve6472.orbiter.settings.Settings;

/**
 * Created by steve6472
 * Date: 8/21/2025
 * Project: Orbiter <br>
 */
public class BandwidthTracker
{
    private static final int USAGE_MEMORY_SECONDS = 2;
    private static final int USAGE_MEMORY_TICKS = USAGE_MEMORY_SECONDS * 60;
    private Wrap readMemory, sendMemory;
    private boolean enabled;

    public void tick()
    {
        if (!enabled && Settings.TRACK_BANDWIDTH.get())
        {
            readMemory = new Wrap();
            sendMemory = new Wrap();
            enabled = true;
        }

        if (enabled && !Settings.TRACK_BANDWIDTH.get())
        {
            enabled = false;
            readMemory = null;
            sendMemory = null;
        }

        if (!enabled)
            return;

        readMemory.startOfTick = true;
        sendMemory.startOfTick = true;
    }

    private void addBytes(Wrap wrap, int bytes)
    {
        if (!enabled)
            return;

//        System.out.println("Add bytes " + bytes + " to " + (wrap == readMemory ? "read" : "send"));

        if (wrap.startOfTick)
        {
            if (wrap.list.size() >= USAGE_MEMORY_TICKS)
                wrap.list.removeFirst();
            wrap.list.add(bytes);
            wrap.startOfTick = false;
        } else
        {
            wrap.list.set(wrap.list.size() - 1, wrap.list.getLast() + bytes);
        }
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void addReadBytes(int bytes)
    {
        addBytes(readMemory, bytes);
        addBytes(sendMemory, 0);
    }

    public void addSendBytes(int bytes)
    {
        addBytes(readMemory, 0);
        addBytes(sendMemory, bytes);
    }

    public IntArrayList getReadMemory()
    {
        if (readMemory == null)
            return null;
        return readMemory.list;
    }

    public IntArrayList getSendMemory()
    {
        if (sendMemory == null)
            return null;
        return sendMemory.list;
    }

    private static class Wrap
    {
        IntArrayList list;
        boolean startOfTick;

        Wrap()
        {
            list = new IntArrayList(USAGE_MEMORY_TICKS);
        }
    }
}
