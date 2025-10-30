package steve6472.orbiter.rendering.snapshot;

import steve6472.orbiter.rendering.snapshot.snapshots.PlaneParticleSnapshots;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public class WorldSnapshot
{
    public PlaneParticleSnapshots planeParticleSnapshot = new PlaneParticleSnapshots();

    public void free(SnapshotPools pools)
    {
        pools.particlePool.freeAll(planeParticleSnapshot.particles);
        pools.tintedParticlePool.freeAll(planeParticleSnapshot.tintedParticles);
    }
}
