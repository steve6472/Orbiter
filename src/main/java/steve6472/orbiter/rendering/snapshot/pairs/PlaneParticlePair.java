package steve6472.orbiter.rendering.snapshot.pairs;

import steve6472.orbiter.rendering.snapshot.snapshots.PlaneParticleSnapshot;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public record PlaneParticlePair(PlaneParticleSnapshot previous, PlaneParticleSnapshot current) implements RenderPair<PlaneParticleSnapshot>
{
}
