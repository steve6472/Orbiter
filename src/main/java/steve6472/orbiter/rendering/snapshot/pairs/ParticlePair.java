package steve6472.orbiter.rendering.snapshot.pairs;

import steve6472.orbiter.rendering.snapshot.snapshots.ParticleSnapshot;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public record ParticlePair(ParticleSnapshot previous, ParticleSnapshot current, boolean tinted)
{
}
