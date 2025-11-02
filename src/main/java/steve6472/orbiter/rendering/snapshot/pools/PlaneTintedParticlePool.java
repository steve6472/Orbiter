package steve6472.orbiter.rendering.snapshot.pools;

import com.badlogic.gdx.utils.Pool;
import steve6472.orbiter.rendering.snapshot.snapshots.PlaneTintedParticleSnapshot;

public class PlaneTintedParticlePool extends Pool<PlaneTintedParticleSnapshot>
{
    public PlaneTintedParticlePool(int initialCapacity, int max)
    {
        super(initialCapacity, max);
    }

    @Override
    protected PlaneTintedParticleSnapshot newObject()
    {
        return new PlaneTintedParticleSnapshot();
    }
}