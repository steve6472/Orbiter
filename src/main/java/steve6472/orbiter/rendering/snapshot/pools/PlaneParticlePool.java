package steve6472.orbiter.rendering.snapshot.pools;

import com.badlogic.gdx.utils.Pool;
import steve6472.orbiter.rendering.snapshot.snapshots.PlaneParticleSnapshot;

public class PlaneParticlePool extends Pool<PlaneParticleSnapshot>
{
    public PlaneParticlePool(int initialCapacity, int max)
    {
        super(initialCapacity, max);
    }

    @Override
    protected PlaneParticleSnapshot newObject()
    {
        return new PlaneParticleSnapshot();
    }
}