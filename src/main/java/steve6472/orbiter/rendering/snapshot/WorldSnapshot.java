package steve6472.orbiter.rendering.snapshot;

import steve6472.orbiter.rendering.snapshot.snapshots.group.ParticleSnapshots;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public class WorldSnapshot
{
    public ParticleSnapshots particleSnapshots = new ParticleSnapshots();

    public void free(SnapshotPools pools)
    {
        pools.planeparticlePool.freeAll(particleSnapshots.planeParticles);
        pools.planeTintedParticlePool.freeAll(particleSnapshots.planeTintedParticles);
        pools.flipbookParticlePool.freeAll(particleSnapshots.flipbookParticles);
    }
}
