package steve6472.orbiter.rendering.gizmo.alpha;

/**
 * Created by steve6472
 * Date: 11/9/2025
 * Project: Orbiter <br>
 */
record InterpolatedMultiplier(long startMilli, long endMilli) implements AlphaMultiplier
{
    @Override
    public float get(long nowMilli)
    {
        return 1f - Math.clamp((float) (nowMilli - startMilli) / (float) (endMilli - startMilli), 0f, 1f);
    }
}
