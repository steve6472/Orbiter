package steve6472.orbiter.rendering.snapshot.snapshots.group;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Array;
import steve6472.orbiter.rendering.snapshot.SnapshotPools;
import steve6472.orbiter.rendering.snapshot.snapshots.FlipbookParticleSnapshot;
import steve6472.orbiter.rendering.snapshot.snapshots.FlipbookTintedParticleSnapshot;
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
    public static final Family FAMILY_FLIPBOOK = Family.all(Position.class, FlipbookModel.class).exclude(TintGradient.class, TintRGBA.class).get();
    public static final Family FAMILY_FLIPBOOK_TINTED = Family.all(Position.class, FlipbookModel.class).one(TintGradient.class, TintRGBA.class).get();

    public final Array<PlaneParticleSnapshot> planeParticles = new Array<>(false, 16);
    public final Array<PlaneTintedParticleSnapshot> planeTintedParticles = new Array<>(false, 16);
    public final Array<FlipbookParticleSnapshot> flipbookParticles = new Array<>(false, 16);
    public final Array<FlipbookTintedParticleSnapshot> flipbookTintedParticles = new Array<>(false, 16);

    public void createSnapshot(SnapshotPools pools, PooledEngine particleEngine)
    {
        for (Entity entity : particleEngine.getEntitiesFor(FAMILY_PLANE))
        {
            PlaneParticleSnapshot snapshot = pools.planeparticlePool.obtain();
            snapshot.fromEntity(entity);
            planeParticles.add(snapshot);
        }

        for (Entity entity : particleEngine.getEntitiesFor(FAMILY_PLANE_TINTED))
        {
            PlaneTintedParticleSnapshot snapshot = pools.planeTintedParticlePool.obtain();
            snapshot.fromEntity(entity);
            planeTintedParticles.add(snapshot);
        }

        for (Entity entity : particleEngine.getEntitiesFor(FAMILY_FLIPBOOK))
        {
            FlipbookParticleSnapshot snapshot = pools.flipbookParticlePool.obtain();
            snapshot.fromEntity(entity);
            flipbookParticles.add(snapshot);
        }

        for (Entity entity : particleEngine.getEntitiesFor(FAMILY_FLIPBOOK_TINTED))
        {
            FlipbookTintedParticleSnapshot snapshot = pools.flipbookTintedParticlePool.obtain();
            snapshot.fromEntity(entity);
            flipbookTintedParticles.add(snapshot);
        }
    }
}
