package steve6472.orbiter.rendering.snapshot.snapshots;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import org.joml.Vector4f;
import steve6472.orbiter.rendering.ParticleRenderCommon;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orlang.OrlangEnvironment;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public class TintedParticleSnapshot extends ParticleSnapshot implements Pool.Poolable
{
    public float r, g, b, a;

    @Override
    public void reset()
    {
        super.reset();
        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }

    public void fromEntity(Entity entity)
    {
        super.fromEntity(entity);

        // TODO: currently we do this twice, once here and once in super
        OrlangEnvironment env = ParticleRenderCommon.updateEnvironment(entity);

        // TODO: separate properly
        var tintrgba = ParticleComponents.TINT_RGBA.get(entity);
        var tintGradient = ParticleComponents.TINT_GRADIENT.get(entity);
        if (tintrgba != null || tintGradient != null)
        {
            Vector4f color = new Vector4f();
            ParticleRenderCommon.doTint(entity, env, color);
            this.r = color.x;
            this.g = color.y;
            this.b = color.z;
            this.a = color.w;
        }
    }
}
