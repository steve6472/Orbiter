package steve6472.orbiter.rendering.snapshot.snapshots;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import org.joml.Vector2f;
import org.joml.Vector4f;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orbiter.world.particle.components.FlipbookModel;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public class FlipbookParticleSnapshot extends ParticleSnapshot implements Pool.Poolable
{
    public final Vector4f uv = new Vector4f(0, 0, 1, 1);
    public int flags;
    public Vector2f singleSize = new Vector2f(1, 1);
    public Vector2f pixelSize = new Vector2f(1, 1);
    public int[] framesIndex;
    public long[] framesTime;
    public int frameIndex;
    public int nextFrameIndex;
    // float progress

    @Override
    public void reset()
    {
        super.reset();
    }

    public void fromEntity(Entity entity, long now)
    {
        super.fromEntity(entity);

        FlipbookModel flipbook = ParticleComponents.FLIPBOOK_MODEL.get(entity);
        flipbook.tick(now);
//        uv.set(flipbook.uv)
    }
}
