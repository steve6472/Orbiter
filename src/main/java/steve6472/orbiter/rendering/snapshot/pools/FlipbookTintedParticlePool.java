package steve6472.orbiter.rendering.snapshot.pools;

import com.badlogic.gdx.utils.Pool;
import steve6472.orbiter.rendering.snapshot.snapshots.FlipbookParticleSnapshot;
import steve6472.orbiter.rendering.snapshot.snapshots.FlipbookTintedParticleSnapshot;

public class FlipbookTintedParticlePool extends Pool<FlipbookTintedParticleSnapshot>
{
    public FlipbookTintedParticlePool(int initialCapacity, int max)
    {
        super(initialCapacity, max);
    }

    @Override
    protected FlipbookTintedParticleSnapshot newObject()
    {
        return new FlipbookTintedParticleSnapshot();
    }
}