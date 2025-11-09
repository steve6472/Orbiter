package steve6472.orbiter.rendering.gizmo.alpha;

/**
 * Created by steve6472
 * Date: 11/9/2025
 * Project: Orbiter <br>
 */
@FunctionalInterface
public interface AlphaMultiplier
{
    float get(long nowMilli);

    static AlphaMultiplier staticAlpha()
    {
        return StaticMultiplier.INSTANCE;
    }

    static AlphaMultiplier interpolatedAlpha(long startMillis, long endMillis)
    {
        return new InterpolatedMultiplier(startMillis, endMillis);
    }

    static boolean isBlend(float currentAlpha, AlphaMultiplier multiplier)
    {
        return currentAlpha < 1.0f || multiplier instanceof InterpolatedMultiplier;
    }
}
