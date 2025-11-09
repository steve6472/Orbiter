package steve6472.orbiter.rendering.snapshot;

import org.joml.Vector3f;
import steve6472.orbiter.rendering.gizmo.GizmoInstance;
import steve6472.orbiter.rendering.snapshot.snapshots.group.ModelSnapshots;
import steve6472.orbiter.rendering.snapshot.snapshots.group.ParticleSnapshots;

import java.util.List;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public class WorldSnapshot
{
    public long snapshotTimeNano, snapshotTimeMilli;

    public ParticleSnapshots particleSnapshots = new ParticleSnapshots();
    public ModelSnapshots modelSnapshots = new ModelSnapshots();
    public List<GizmoInstance> gizmos;
    public Vector3f cameraPosition = new Vector3f();

    public void free(SnapshotPools pools)
    {
        pools.planeparticlePool.freeAll(particleSnapshots.planeParticles);
        pools.planeTintedParticlePool.freeAll(particleSnapshots.planeTintedParticles);
        pools.flipbookParticlePool.freeAll(particleSnapshots.flipbookParticles);
        pools.flipbookTintedParticlePool.freeAll(particleSnapshots.flipbookTintedParticles);
        pools.staticModelPool.freeAll(modelSnapshots.staticEntities);
        pools.animatedModelPool.freeAll(modelSnapshots.animatedEntities);
    }
}
