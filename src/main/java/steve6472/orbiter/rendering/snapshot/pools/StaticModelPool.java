package steve6472.orbiter.rendering.snapshot.pools;

import com.badlogic.gdx.utils.Pool;
import steve6472.orbiter.rendering.snapshot.snapshots.FlipbookParticleSnapshot;
import steve6472.orbiter.rendering.snapshot.snapshots.StaticModelSnapshot;

public class StaticModelPool extends Pool<StaticModelSnapshot>
{
    public StaticModelPool(int initialCapacity, int max)
    {
        super(initialCapacity, max);
    }

    @Override
    protected StaticModelSnapshot newObject()
    {
        return new StaticModelSnapshot();
    }
}