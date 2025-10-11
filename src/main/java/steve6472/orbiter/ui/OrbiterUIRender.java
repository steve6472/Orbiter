package steve6472.orbiter.ui;

import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.core.util.Profiler;
import steve6472.flare.render.impl.UIRenderImpl;
import steve6472.moondust.MoonDust;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.network.api.BandwidthTracker;
import steve6472.orbiter.settings.Settings;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 8/22/2025
 * Project: Orbiter <br>
 */
public class OrbiterUIRender extends UIRenderImpl
{
    private static final Logger LOGGER = Log.getLogger(OrbiterUIRender.class);
    private final OrbiterApp orbiter;

    private static final Key READ_SPRITE = Constants.key("bandwidth_tracker/read");
    private static final Key SEND_SPRITE = Constants.key("bandwidth_tracker/send");

    public OrbiterUIRender(OrbiterApp orbiter)
    {
        this.orbiter = orbiter;
    }

    @Override
    public void render()
    {
        trackBandwidth();
        profileTick();
        profileFps();
    }

    private void profileTick()
    {
        if (!Settings.PROFILE_TICK.get())
            return;

        int pixelScale = (int) MoonDust.getInstance().getPixelScale();
        int windowHeight = orbiter.window().getHeight() / pixelScale;

        Profiler profiler = OrbiterApp.getInstance().tickProfiler;
        sprite(0, windowHeight - 16, 0, profiler.getTotalMeasurements() + 4, 1, READ_SPRITE);
        for (int i = 0; i < profiler.getTotalMeasurements(); i++)
        {
            int height = (int) (profiler.getMeasurementAt(i) / 1e6);
            sprite(i, windowHeight - height, 0, 1, height, SEND_SPRITE);
        }
    }

    private void profileFps()
    {
        if (!Settings.PROFILE_FPS.get())
            return;

        int pixelScale = (int) MoonDust.getInstance().getPixelScale();
        int windowHeight = orbiter.window().getHeight() / pixelScale;

        Profiler profiler = OrbiterApp.getInstance().fpsProfiler;
        sprite(0, windowHeight - 30, 0, profiler.getTotalMeasurements() + 4, 1, READ_SPRITE);
        for (int i = 0; i < profiler.getTotalMeasurements(); i++)
        {
            int sampleHeight = (int)Math.round(profiler.getMeasurementAt(i) / 1e6 * 60.0 / 33.333333333333336);
            sprite(i, windowHeight - sampleHeight, 0, 1, sampleHeight, SEND_SPRITE);
        }
    }

    private void trackBandwidth()
    {
        if (!Settings.TRACK_BANDWIDTH.get())
            return;

        BandwidthTracker tracker = orbiter.getNetwork().connections().bandwidthTracker();

        if (tracker == null || !tracker.isEnabled())
            return;

        if (tracker.getReadMemory().size() != tracker.getSendMemory().size())
        {
            Log.warningOnce(LOGGER, "Read & Send memory size mismatch");
        }

        int pixelScale = (int) MoonDust.getInstance().getPixelScale();
        int windowHeight = orbiter.window().getHeight() / pixelScale;

        int size = tracker.getReadMemory().size();

        for (int i = 0; i < size; i++)
        {
            int readHeight = calcHeight(tracker.getReadMemory().getInt(i));
            int sendHeight = calcHeight(tracker.getSendMemory().getInt(i));
            sprite(i, windowHeight - sendHeight, 0, 1, sendHeight, SEND_SPRITE);
            sprite(i, windowHeight - readHeight - sendHeight, 0, 1, readHeight, READ_SPRITE);
        }

        //        if (!tracker.getSendMemory().isEmpty())
        //            LOGGER.info("Read: " + tracker.getReadMemory().getLast() + "B, Send: " + tracker.getSendMemory().getLast());
    }

    private static final double log2 = Math.log(2);
    private int calcHeight(int bytes)
    {
        if (bytes == 0)
            return 0;
        return (int) Math.max(1, (Math.log(bytes) / log2));
    }

    protected final void sprite(int x, int y, float zIndex, int width, int height, Key textureKey)
    {
        float pixelScale = MoonDust.getInstance().getPixelScale();
        createSprite((int) (x * pixelScale), (int) (y * pixelScale), zIndex, (int) (width * pixelScale), (int) (height * pixelScale), width, height, NO_TINT, getTextureEntry(textureKey));
    }
}
