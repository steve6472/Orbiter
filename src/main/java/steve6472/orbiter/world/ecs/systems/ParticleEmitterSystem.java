package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import org.joml.Vector3f;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.ParticleHolderId;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitters;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;
import steve6472.orbiter.world.particle.components.LocalSpace;
import steve6472.orbiter.world.particle.components.ParticleFollowerId;

import java.util.List;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class ParticleEmitterSystem extends IteratingProfiledSystem
{
    private final PooledEngine particleEngine;

    public ParticleEmitterSystem(World world)
    {
        super(Family.all(Position.class, ParticleEmitters.class).get());
        this.particleEngine = world.particleEngine();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        ParticleEmitters emitters = Components.PARTICLE_EMITTERS.get(entity);

        for (ParticleEmitter emitter : emitters.emitters)
        {
            processEmitter(emitters, emitter, entity);
        }

        if (emitters.emitters.isEmpty())
        {
            entity.remove(ParticleEmitters.class);
            entity.remove(ParticleHolderId.class);
        }
    }

    private void processEmitter(ParticleEmitters emitters, ParticleEmitter emitter, Entity entity)
    {
        if (!emitter.lifetime.isAlive(emitter, emitter.emitterAge))
        {
            emitters.emitters.remove(emitter);
            return;
        }

        if (!emitter.lifetime.shouldEmit(emitter, emitter.emitterAge))
        {
            emitter.emitterAge++;
            return;
        }

        int spawnCount = emitter.rate.spawnCount(emitter);

        if (spawnCount <= 0)
        {
            emitter.emitterAge++;
            return;
        }

        emitter.updateEnvironment();

        int holderId = ParticleHolderId.UNASSIGNED;
        if (emitter.particleData.localSpace().isPresent())
        {
            ParticleHolderId holderId_ = Components.PARTICLE_HOLDER.get(entity);
            if (holderId_ == null)
            {
                holderId_ = new ParticleHolderId();
                holderId_.followerId = ParticleHolderId.generateRandomId();
                entity.add(holderId_);
            }
            holderId = holderId_.followerId;
        }

        Position position = Components.POSITION.get(entity);

        for (int i = 0; i < spawnCount; i++)
        {
            createEntity(entity, holderId, emitter, position);
        }
    }

    private void createEntity(Entity holder, int holderId, ParticleEmitter emitter, Position emitterPosition)
    {
        Entity entity = particleEngine.createEntity();
        OrlangEnvironment env = particleEngine.createComponent(OrlangEnvironment.class);
        entity.add(env);

        List<Component> particleComponents = emitter.particleData.createComponents(particleEngine, env);
        for (Component particleComponent : particleComponents)
        {
            entity.add(particleComponent);
        }

        LocalSpace localSpace = ParticleComponents.LOCAL_SPACE.get(entity);

        var particlePosition = ParticleComponents.POSITION.create(particleEngine);
        Vector3f position = emitter.shape.createPosition(emitter);
        emitter.offset.evaluate(emitter.environment);
        if (localSpace != null && localSpace.position)
        {
            particlePosition.x = position.x + emitter.offset.fx();
            particlePosition.y = position.y + emitter.offset.fy();
            particlePosition.z = position.z + emitter.offset.fz();
        } else
        {
            particlePosition.x = emitterPosition.x() + position.x + emitter.offset.fx();
            particlePosition.y = emitterPosition.y() + position.y + emitter.offset.fy();
            particlePosition.z = emitterPosition.z() + position.z + emitter.offset.fz();
        }
        entity.add(particlePosition);

        if (holderId != ParticleHolderId.UNASSIGNED)
        {
            ParticleFollowerId followerComponent = particleEngine.createComponent(ParticleFollowerId.class);
            followerComponent.entity = holder;
            followerComponent.followerId = holderId;
            entity.add(followerComponent);
        }

        particleEngine.addEntity(entity);
    }
}
