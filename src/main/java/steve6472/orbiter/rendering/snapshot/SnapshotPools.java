package steve6472.orbiter.rendering.snapshot;

import steve6472.orbiter.rendering.snapshot.snapshots.*;
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
    private static final int MAX_ANIMATED = 512;

    public final SnapshotPool<PlaneParticleSnapshot> planeparticlePool = new SnapshotPool<>(INITIAL_CAPACITY, World.MAX_PARTICLES, PlaneParticleSnapshot::new);
    public final SnapshotPool<PlaneTintedParticleSnapshot> planeTintedParticlePool = new SnapshotPool<>(INITIAL_CAPACITY, World.MAX_PARTICLES, PlaneTintedParticleSnapshot::new);
    public final SnapshotPool<FlipbookParticleSnapshot> flipbookParticlePool = new SnapshotPool<>(INITIAL_CAPACITY, World.MAX_PARTICLES, FlipbookParticleSnapshot::new);
    public final SnapshotPool<FlipbookTintedParticleSnapshot> flipbookTintedParticlePool = new SnapshotPool<>(INITIAL_CAPACITY, World.MAX_PARTICLES, FlipbookTintedParticleSnapshot::new);

    public final SnapshotPool<StaticModelSnapshot> staticModelPool = new SnapshotPool<>(INITIAL_CAPACITY, MAX_STATIC, StaticModelSnapshot::new);
    public final SnapshotPool<AnimatedModelSnapshot> animatedModelPool = new SnapshotPool<>(INITIAL_CAPACITY, MAX_ANIMATED, AnimatedModelSnapshot::new);
}