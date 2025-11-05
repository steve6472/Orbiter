package steve6472.orbiter.rendering.snapshot.pairs;

import steve6472.orbiter.rendering.snapshot.snapshots.AnimatedModelSnapshot;

/**
 * Created by steve6472
 * Date: 11/4/2025
 * Project: Orbiter <br>
 */
public record AnimatedModelPair(AnimatedModelSnapshot previous, AnimatedModelSnapshot current) implements RenderPair<AnimatedModelSnapshot>
{
}
