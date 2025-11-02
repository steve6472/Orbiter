package steve6472.orbiter.rendering.snapshot.pairs;

/**
 * Created by steve6472
 * Date: 10/30/2025
 * Project: Orbiter <br>
 */
public interface RenderPair<T>
{
    T previous();
    T current();
}
