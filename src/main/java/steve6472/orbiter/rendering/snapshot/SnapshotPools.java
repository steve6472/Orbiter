package steve6472.orbiter.rendering.snapshot;

import steve6472.orbiter.rendering.snapshot.pools.*;
import steve6472.orbiter.world.World;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public class SnapshotPools
{
    private static final int INITIAL_CAPACITY = 64;
    private static final int MAX_STATIC = 1024;

    public final PlaneParticlePool planeparticlePool = new PlaneParticlePool(INITIAL_CAPACITY, World.MAX_PARTICLES);
    public final PlaneTintedParticlePool planeTintedParticlePool = new PlaneTintedParticlePool(INITIAL_CAPACITY, World.MAX_PARTICLES);
    public final FlipbookParticlePool flipbookParticlePool = new FlipbookParticlePool(INITIAL_CAPACITY, World.MAX_PARTICLES);
    public final FlipbookTintedParticlePool flipbookTintedParticlePool = new FlipbookTintedParticlePool(INITIAL_CAPACITY, World.MAX_PARTICLES);

    public final StaticModelPool staticModelPool = new StaticModelPool(INITIAL_CAPACITY, MAX_STATIC);
}