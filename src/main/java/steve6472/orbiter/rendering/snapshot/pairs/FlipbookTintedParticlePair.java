package steve6472.orbiter.rendering.snapshot.pairs;

import steve6472.orbiter.rendering.snapshot.snapshots.FlipbookTintedParticleSnapshot;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public record FlipbookTintedParticlePair(FlipbookTintedParticleSnapshot previous, FlipbookTintedParticleSnapshot current) implements RenderPair<FlipbookTintedParticleSnapshot>
{
}
