package steve6472.orbiter.rendering.snapshot.pools;

import com.badlogic.gdx.utils.Pool;
import steve6472.orbiter.rendering.snapshot.snapshots.FlipbookParticleSnapshot;

public class FlipbookParticlePool extends Pool<FlipbookParticleSnapshot>
{
    public FlipbookParticlePool(int initialCapacity, int max)
    {
        super(initialCapacity, max);
    }

    @Override
    protected FlipbookParticleSnapshot newObject()
    {
        return new FlipbookParticleSnapshot();
    }
}