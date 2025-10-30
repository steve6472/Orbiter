package steve6472.orbiter.rendering.snapshot.pools;

import com.badlogic.gdx.utils.Pool;
import steve6472.orbiter.rendering.snapshot.snapshots.ParticleSnapshot;

public class ParticlePool extends Pool<ParticleSnapshot>
{
    @Override
    protected ParticleSnapshot newObject()
    {
        return new ParticleSnapshot();
    }
}