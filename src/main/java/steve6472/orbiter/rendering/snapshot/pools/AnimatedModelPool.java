package steve6472.orbiter.rendering.snapshot.pools;

import com.badlogic.gdx.utils.Pool;
import steve6472.orbiter.rendering.snapshot.snapshots.AnimatedModelSnapshot;
import steve6472.orbiter.rendering.snapshot.snapshots.StaticModelSnapshot;

public class AnimatedModelPool extends Pool<AnimatedModelSnapshot>
{
    public AnimatedModelPool(int initialCapacity, int max)
    {
        super(initialCapacity, max);
    }

    @Override
    protected AnimatedModelSnapshot newObject()
    {
        return new AnimatedModelSnapshot();
    }
}