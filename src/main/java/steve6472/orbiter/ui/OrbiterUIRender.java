package steve6472.orbiter.ui;

import steve6472.core.log.Log;
import steve6472.core.registry.Key;
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
