package steve6472.orbiter.rendering.snapshot.pairs;

import steve6472.orbiter.rendering.snapshot.snapshots.FlipbookParticleSnapshot;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public record FlipbookParticlePair(FlipbookParticleSnapshot previous, FlipbookParticleSnapshot current) implements RenderPair<FlipbookParticleSnapshot>
{
}
