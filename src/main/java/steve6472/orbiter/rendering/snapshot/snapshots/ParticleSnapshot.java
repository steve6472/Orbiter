package steve6472.orbiter.rendering.snapshot.snapshots;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import steve6472.flare.assets.model.blockbench.animation.controller.AnimationController;
import steve6472.orbiter.rendering.Billboard;
import steve6472.orbiter.rendering.ParticleMaterial;
import steve6472.orbiter.rendering.ParticleRenderCommon;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.AnimatedModel;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orbiter.world.particle.components.*;
import steve6472.orlang.OrlangEnvironment;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public class ParticleSnapshot implements Pool.Poolable
{
    public float x, y, z;
    public float velX, velY, velZ;
    public float scaleX, scaleY;
    public float rotation;
    public Billboard billboard = Billboard.FIXED;
    public ParticleMaterial material = ParticleMaterial.BLEND;
    public final Quaternionf parentRotation = new Quaternionf();
    public final Vector4f uv = new Vector4f(0, 0, 1, 1);

    public Entity entity;

    /// These 3 floats are used for rendering and sorting
    /// They hold the interpolated position for current frame
    public float rx, ry, rz;

    @Override
    public void reset()
    {
        entity = null;
        x = 0;
        y = 0;
        z = 0;
        velX = 0;
        velY = 0;
        velZ = 0;
        rotation = 0;
        scaleX = 1;
        scaleY = 1;
        billboard = Billboard.FIXED;
        material = ParticleMaterial.BLEND;
        parentRotation.identity();
        uv.set(0, 0, 1, 1);
        rx = 0;
        ry = 0;
        rz = 0;
    }

    public void fromEntity(Entity entity)
    {
        this.entity = entity;

        PlaneModel planeModel = ParticleComponents.PLANE_MODEL.get(entity);
        uv.set(planeModel.uv);

        OrlangEnvironment env = ParticleRenderCommon.updateEnvironment(entity);
        LocalSpace localSpace = ParticleComponents.LOCAL_SPACE.get(entity);

        Vector3f position = new Vector3f();

        if (ParticleComponents.POSITION.has(entity))
        {
            Position particlePos = ParticleComponents.POSITION.get(entity);
            if (localSpace != null && localSpace.position)
            {
                ParticleFollowerId follower = ParticleComponents.PARTICLE_FOLLOWER.get(entity);
                if (follower.locator != null)
                {
                    AnimatedModel animatedModel = Components.ANIMATED_MODEL.get(follower.entity);
                    AnimationController.LocatorInfo locator = animatedModel.animationController.getLocator(follower.locator);
                    if (locator == null)
                    {
                        var holderPosition = Components.POSITION.get(follower.entity);
                        if (holderPosition != null)
                        {
                            position.add(holderPosition.x(), holderPosition.y(), holderPosition.z());
                        }
                    } else
                    {
                        position.add(locator.position());
                    }
                } else
                {
                    var holderPosition = Components.POSITION.get(follower.entity);
                    if (holderPosition != null)
                    {
                        position.add(holderPosition.x(), holderPosition.y(), holderPosition.z());
                    }
                }
            }
            position.add(particlePos.x, particlePos.y, particlePos.z);
        }

        x = position.x;
        y = position.y;
        z = position.z;

        if (localSpace != null && localSpace.rotation)
        {
            ParticleFollowerId follower = ParticleComponents.PARTICLE_FOLLOWER.get(entity);
            Components.ROTATION.ifPresent(follower.entity, holderRotation -> parentRotation.set(holderRotation.x(), holderRotation.y(), holderRotation.z(), holderRotation.w()));
        }

        ParticleComponents.BILLBOARD.ifPresent(entity, billboard -> this.billboard = billboard.billboard);
        ParticleComponents.ROTATION.ifPresent(entity, rotation -> this.rotation = (float) Math.toRadians(rotation.rotation));
        ParticleComponents.RENDER_MATERIAL.ifPresent(entity, material -> this.material = material.value);

        ParticleComponents.SCALE.ifPresent(entity, scale -> {
            scale.scale.evaluate(env);
            scaleX = scale.scale.fx();
            scaleY = scale.scale.fy();
        });

        ParticleComponents.VELOCITY.ifPresent(entity, velocity -> {
            velX = velocity.x;
            velY = velocity.y;
            velZ = velocity.z;
        });
    }
}
