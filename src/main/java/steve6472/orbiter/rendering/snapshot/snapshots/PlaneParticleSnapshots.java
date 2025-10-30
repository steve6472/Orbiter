package steve6472.orbiter.rendering.snapshot.snapshots;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import steve6472.orbiter.rendering.snapshot.SnapshotPools;
import steve6472.orbiter.world.particle.components.PlaneModel;
import steve6472.orbiter.world.particle.components.Position;
import steve6472.orbiter.world.particle.components.TintGradient;
import steve6472.orbiter.world.particle.components.TintRGBA;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public class PlaneParticleSnapshots
{
    public static final Family FAMILY_UNTINTED = Family.all(Position.class, PlaneModel.class).exclude(TintGradient.class, TintRGBA.class).get();
    public static final Family FAMILY_TINTED = Family.all(Position.class, PlaneModel.class).one(TintGradient.class, TintRGBA.class).get();

    public final Array<ParticleSnapshot> particles = new Array<>(false, 16);
    public final Array<TintedParticleSnapshot> tintedParticles = new Array<>(false, 16);

    public void createSnapshot(SnapshotPools pools, PooledEngine particleEngine)
    {
        ImmutableArray<Entity> particle = particleEngine.getEntitiesFor(FAMILY_UNTINTED);

        if (particle.size() != 0)
        {
            for (Entity entity : particle)
            {
                ParticleSnapshot snapshot = pools.particlePool.obtain();
                snapshot.fromEntity(entity);
                particles.add(snapshot);
            }
        }

        ImmutableArray<Entity> tintedParticle = particleEngine.getEntitiesFor(FAMILY_TINTED);

        if (tintedParticle.size() != 0)
        {
            for (Entity entity : tintedParticle)
            {
                TintedParticleSnapshot snapshot = pools.tintedParticlePool.obtain();
                snapshot.fromEntity(entity);
                tintedParticles.add(snapshot);
            }
        }
    }
}
