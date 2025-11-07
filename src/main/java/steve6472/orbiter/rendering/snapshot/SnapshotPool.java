package steve6472.orbiter.rendering.snapshot;

import com.badlogic.gdx.utils.Pool;

import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 11/7/2025
 * Project: Orbiter <br>
 */
public class SnapshotPool<T extends Pool.Poolable> extends Pool<T>
{
    private final Supplier<T> constructor;

    public SnapshotPool(int initialCapacity, int max, Supplier<T> constructor)
    {
        super(initialCapacity, max);
        this.constructor = constructor;
    }

    @Override
    protected T newObject()
    {
        return constructor.get();
    }
}
