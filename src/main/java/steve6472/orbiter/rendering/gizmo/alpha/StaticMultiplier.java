package steve6472.orbiter.rendering.gizmo.alpha;

/**
 * Created by steve6472
 * Date: 11/9/2025
 * Project: Orbiter <br>
 */
record StaticMultiplier() implements AlphaMultiplier
{
    static final StaticMultiplier INSTANCE = new StaticMultiplier();

    @Override
    public float get(long nowMilli)
    {
        return 1f;
    }
}