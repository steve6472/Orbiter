package steve6472.orbiter.rendering.snapshot.snapshots.group;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import steve6472.orbiter.rendering.snapshot.SnapshotPools;
import steve6472.orbiter.rendering.snapshot.snapshots.FlipbookParticleSnapshot;
import steve6472.orbiter.rendering.snapshot.snapshots.PlaneParticleSnapshot;
import steve6472.orbiter.rendering.snapshot.snapshots.PlaneTintedParticleSnapshot;
import steve6472.orbiter.world.particle.components.*;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public class ParticleSnapshots
{
    public static final Family FAMILY_PLANE = Family.all(Position.class, PlaneModel.class).exclude(TintGradient.class, TintRGBA.class).get();
    public static final Family FAMILY_PLANE_TINTED = Family.all(Position.class, PlaneModel.class).one(TintGradient.class, TintRGBA.class).get();
    public static final Family FAMILY_FLIPBOOK = Family.all(Position.class, FlipbookModel.class).get();

    public final Array<PlaneParticleSnapshot> planeParticles = new Array<>(false, 16);
    public final Array<PlaneTintedParticleSnapshot> planeTintedParticles = new Array<>(false, 16);
    public final Array<FlipbookParticleSnapshot> flipbookParticles = new Array<>(false, 16);

    public void createSnapshot(SnapshotPools pools, PooledEngine particleEngine)
    {
        ImmutableArray<Entity> particle = particleEngine.getEntitiesFor(FAMILY_PLANE);
        for (Entity entity : particle)
        {
            PlaneParticleSnapshot snapshot = pools.planeparticlePool.obtain();
            snapshot.fromEntity(entity);
            planeParticles.add(snapshot);
        }

        ImmutableArray<Entity> tintedParticle = particleEngine.getEntitiesFor(FAMILY_PLANE_TINTED);
        for (Entity entity : tintedParticle)
        {
            PlaneTintedParticleSnapshot snapshot = pools.planeTintedParticlePool.obtain();
            snapshot.fromEntity(entity);
            planeTintedParticles.add(snapshot);
        }

        long now = System.currentTimeMillis();

        ImmutableArray<Entity> flipbookParticle = particleEngine.getEntitiesFor(FAMILY_FLIPBOOK);
        for (Entity entity : flipbookParticle)
        {
            FlipbookParticleSnapshot snapshot = pools.flipbookParticlePool.obtain();
            snapshot.fromEntity(entity, now);
            flipbookParticles.add(snapshot);
        }
    }
}
