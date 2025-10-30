package steve6472.orbiter.rendering.snapshot.pools;

import com.badlogic.gdx.utils.Pool;
import steve6472.orbiter.rendering.snapshot.snapshots.ParticleSnapshot;
import steve6472.orbiter.rendering.snapshot.snapshots.TintedParticleSnapshot;

public class TintedParticlePool extends Pool<TintedParticleSnapshot>
{
    @Override
    protected TintedParticleSnapshot newObject()
    {
        return new TintedParticleSnapshot();
    }
}