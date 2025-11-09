package steve6472.orbiter.rendering.gizmo;

import steve6472.orbiter.rendering.gizmo.alpha.AlphaMultiplier;

/**
 * Created by steve6472
 * Date: 11/9/2025
 * Project: Orbiter <br>
 */
public class GizmoInstance implements GizmoRenderSettings
{
    private final Gizmo gizmo;
    private boolean alwaysOnTop;
    private boolean fadeOut;

    private long startTimeMilli;
    private long endTimeMilli;

    GizmoInstance(Gizmo gizmo)
    {
        this.gizmo = gizmo;
    }

    @Override
    public GizmoRenderSettings alwaysOnTop()
    {
        this.alwaysOnTop = true;
        return this;
    }

    @Override
    public GizmoRenderSettings fadeOut()
    {
        this.fadeOut = true;
        return this;
    }

    @Override
    public GizmoRenderSettings stayForMs(long ms)
    {
        this.startTimeMilli = System.currentTimeMillis();
        this.endTimeMilli = startTimeMilli + ms;
        return this;
    }

    public Gizmo gizmo()
    {
        return gizmo;
    }

    public boolean isAlwaysOnTop()
    {
        return alwaysOnTop;
    }

    public float getFadeOutAlpha(long now)
    {
        if (!fadeOut)
            return 1f;
        return 1f - Math.clamp((float) (now - startTimeMilli) / (float) (endTimeMilli - startTimeMilli), 0f, 1f);
    }

    public AlphaMultiplier alphaMultiplier()
    {
        if (fadeOut)
            return AlphaMultiplier.interpolatedAlpha(startTimeMilli, endTimeMilli);
        else
            return AlphaMultiplier.staticAlpha();
    }

    public boolean hasExpired(long now)
    {
        return endTimeMilli < now;
    }

    @Override
    public String toString()
    {
        return "GizmoInstance{" + "gizmo=" + gizmo + ", alwaysOnTop=" + alwaysOnTop + ", fadeOut=" + fadeOut + ", startTimeMilli=" + startTimeMilli + ", endTimeMilli=" + endTimeMilli + '}';
    }
}
