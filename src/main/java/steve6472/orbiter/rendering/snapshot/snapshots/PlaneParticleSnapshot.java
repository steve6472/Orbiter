package steve6472.orbiter.rendering.snapshot.snapshots;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import org.joml.Vector4f;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orbiter.world.particle.components.PlaneModel;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public class PlaneParticleSnapshot extends ParticleSnapshot implements Pool.Poolable
{
    public final Vector4f uv = new Vector4f(0, 0, 1, 1);

    @Override
    public void reset()
    {
        super.reset();
        uv.set(0, 0, 1, 1);
    }

    public void fromEntity(Entity entity)
    {
        super.fromEntity(entity);

        PlaneModel planeModel = ParticleComponents.PLANE_MODEL.get(entity);
        uv.set(planeModel.uv);
    }
}
