package steve6472.orbiter.rendering.snapshot;

import steve6472.orbiter.rendering.snapshot.pools.ParticlePool;
import steve6472.orbiter.rendering.snapshot.pools.TintedParticlePool;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public class SnapshotPools
{
    public final ParticlePool particlePool = new ParticlePool();
    public final TintedParticlePool tintedParticlePool = new TintedParticlePool();
}