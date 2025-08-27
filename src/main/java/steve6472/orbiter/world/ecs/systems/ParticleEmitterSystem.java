package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import org.joml.Vector3f;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.emitter.LocalSpaceEmitter;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitters;
import steve6472.orbiter.world.ecs.components.particle.Lifetime;
import steve6472.orbiter.world.ecs.components.particle.LocalSpace;
import steve6472.orbiter.world.ecs.components.particle.ParticleFollowerId;
import steve6472.orbiter.world.ecs.components.particle.ParticleHolderId;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;

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

        int holderId = 0;
        if (!emitter.localSpace.equals(LocalSpaceEmitter.DEFAULT))
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
        EntityBlueprint blueprint = Registries.ENTITY_BLUEPRINT.get(emitter.entity);
        if (blueprint == null)
            throw new NullPointerException("Entity " + emitter.entity + " not found!");
        List<Component> particleComponents = blueprint.createParticleComponents(particleEngine);
        for (Component particleComponent : particleComponents)
        {
            entity.add(particleComponent);
        }
        Position particlePosition = Components.POSITION.get(entity);
        if (particlePosition != null)
        {
            Vector3f position = emitter.shape.createPosition(emitter);
            emitter.offset.evaluate(emitter.environment);
            if (emitter.localSpace.position())
            {
                particlePosition.set(
                    position.x + emitter.offset.fx(),
                    position.y + emitter.offset.fy(),
                    position.z + emitter.offset.fz());
            } else
            {
                particlePosition.set(
                    emitterPosition.x() + position.x + emitter.offset.fx(),
                    emitterPosition.y() + position.y + emitter.offset.fy(),
                    emitterPosition.z() + position.z + emitter.offset.fz());
            }
        }
        Lifetime lifetime = particleEngine.createComponent(Lifetime.class);
        entity.add(lifetime);

        if (holderId != ParticleHolderId.UNASSIGNED)
        {
            ParticleFollowerId followerComponent = particleEngine.createComponent(ParticleFollowerId.class);
            followerComponent.entity = holder;
            followerComponent.followerId = holderId;
            entity.add(followerComponent);

            LocalSpace localSpace = particleEngine.createComponent(LocalSpace.class);
            localSpace.position = emitter.localSpace.position();
            localSpace.rotation = emitter.localSpace.rotation();
            localSpace.velocity = emitter.localSpace.velocity();
            entity.add(localSpace);
        }

        particleEngine.addEntity(entity);
    }
}
