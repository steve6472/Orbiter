package steve6472.orbiter.rendering;

import com.badlogic.ashley.core.Entity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import steve6472.flare.Camera;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orbiter.world.particle.components.*;
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

    public static void doTransform(Entity entity, OrlangEnvironment env, Matrix4f transform, Camera camera)
    {
        LocalSpace localSpace = ParticleComponents.LOCAL_SPACE.get(entity);

        Vector3f position = new Vector3f();

        if (ParticleComponents.POSITION.has(entity))
        {
            Position particlePos = ParticleComponents.POSITION.get(entity);
            if (localSpace != null && localSpace.position)
            {
                ParticleFollowerId follower = ParticleComponents.PARTICLE_FOLLOWER.get(entity);
                var holderPosition = Components.POSITION.get(follower.entity);
                if (holderPosition != null)
                {
                    position.add(holderPosition.x(), holderPosition.y(), holderPosition.z());
                }
            }
            position.add(particlePos.x, particlePos.y, particlePos.z);
        }

        ParticleBillboard particleBillboard = ParticleComponents.BILLBOARD.get(entity);
        if (particleBillboard != null)
        {
            Matrix4f matrix4f = BillboardUtil.makeBillboard(position, entity, camera, particleBillboard);
            transform.mul(matrix4f);
        } else
        {
            transform.translate(position.x, position.y, position.z);
        }

        if (localSpace != null && localSpace.rotation)
        {
            ParticleFollowerId follower = ParticleComponents.PARTICLE_FOLLOWER.get(entity);
            steve6472.orbiter.world.ecs.components.physics.Rotation holderRotation = Components.ROTATION.get(follower.entity);
            if (holderRotation != null)
            {
                transform.rotate(new Quaternionf(holderRotation.x(), holderRotation.y(), holderRotation.z(), holderRotation.w()));
            }
        }

        var rotation = ParticleComponents.ROTATION.get(entity);
        if (rotation != null)
        {
            BillboardUtil.applySpin(transform, (float) Math.toRadians(rotation.rotation));
        }

        if (ParticleComponents.SCALE.has(entity))
        {
            Scale scale = ParticleComponents.SCALE.get(entity);
            scale.scale.evaluate(env);
            transform.scale(scale.scale.fx(), scale.scale.fy(), scale.scale.fz());
        }
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
