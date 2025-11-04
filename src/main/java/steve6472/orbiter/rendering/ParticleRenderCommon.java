package steve6472.orbiter.rendering;

import com.badlogic.ashley.core.Entity;
import org.joml.Vector4f;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orlang.Orlang;
import steve6472.orlang.OrlangEnvironment;
import steve6472.orlang.codec.OrCode;

/**
 * Created by steve6472
 * Date: 9/6/2025
 * Project: Orbiter <br>
 */
public final class ParticleRenderCommon
{
    public static OrlangEnvironment updateEnvironment(Entity entity)
    {
        OrlangEnv envComp = ParticleComponents.PARTICLE_ENVIRONMENT.get(entity);
        OrlangEnvironment env = envComp.env;

        // Update curves each frame
        env.curves.forEach((name, curve) -> curve.calculate(name, env));
        OrCode frame = env.expressions.get("frame");
        if (frame != null)
        {
            Orlang.interpreter.interpret(frame, env);
        }
        return env;
    }

    public static void doTint(Entity entity, OrlangEnvironment env, Vector4f tint)
    {
        var tintrgba = ParticleComponents.TINT_RGBA.get(entity);
        if (tintrgba != null)
        {
            tint.set(
                tintrgba.r.evaluateAndGet(env),
                tintrgba.g.evaluateAndGet(env),
                tintrgba.b.evaluateAndGet(env),
                tintrgba.a.evaluateAndGet(env)
            );
        } else
        {
            var tintGradient = ParticleComponents.TINT_GRADIENT.get(entity);
            if (tintGradient != null)
            {
                tintGradient.gradient.apply(env, tint);
            }
        }
    }
}
