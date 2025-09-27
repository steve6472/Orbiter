package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import org.joml.Vector3f;
import steve6472.core.log.Log;
import steve6472.flare.assets.model.blockbench.animation.controller.AnimationController;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.AnimatedModel;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.ecs.components.ParticleHolderId;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orbiter.world.emitter.ParticleEmitter;
import steve6472.orbiter.world.emitter.ParticleEmitters;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;
import steve6472.orbiter.world.particle.blueprints.ParticleDirectionBlueprint;
import steve6472.orbiter.world.particle.components.FlipbookModel;
import steve6472.orbiter.world.particle.components.Velocity;
import steve6472.orbiter.world.particle.components.LocalSpace;
import steve6472.orbiter.world.particle.components.ParticleFollowerId;
import steve6472.orbiter.world.particle.core.ParticleBlueprint;
import steve6472.orbiter.world.particle.core.ParticleComponent;
import steve6472.orlang.OrlangEnvironment;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class ParticleEmitterSystem extends IteratingProfiledSystem
{
    private static final Logger LOGGER = Log.getLogger(ParticleEmitterSystem.class);
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

        //noinspection Java8CollectionRemoveIf
        for (Iterator<ParticleEmitter> iterator = emitters.emitters.iterator(); iterator.hasNext(); )
        {
            ParticleEmitter emitter = iterator.next();
            if (processEmitter(emitter, entity))
            {
                iterator.remove();
            }
        }

        if (emitters.emitters.isEmpty())
        {
            entity.remove(ParticleEmitters.class);
            entity.remove(ParticleHolderId.class);
        }
    }

    /// @return true if emitter should be removed
    private boolean processEmitter(ParticleEmitter emitter, Entity entity)
    {
        if (!emitter.lifetime.isAlive(emitter))
        {
            return true;
        }

        if (!emitter.lifetime.shouldEmit(emitter))
            return false;

        int spawnCount = emitter.rate.spawnCount(emitter);

        if (spawnCount <= 0)
            return false;

        // Sets environment variables
        emitter.emitterTick();

        int holderId = ParticleHolderId.UNASSIGNED;
        if (emitter.particleData.get().containsLocalSpace)
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
            createParticle(entity, holderId, emitter, position);
        }

        return false;
    }

    private void createParticle(Entity holder, int holderId, ParticleEmitter emitter, Position emitterPosition)
    {
        emitter.particleTick();

        Entity entity = particleEngine.createEntity();
        ParticleBlueprint blueprint = emitter.particleData.get();
        OrlangEnv envComp = (OrlangEnv) blueprint.environmentBlueprint.create(particleEngine, emitter.environment);
        entity.add(envComp);
        OrlangEnvironment env = envComp.env;

        List<ParticleComponent> particleComponents = blueprint.createComponents(particleEngine, env);
        for (Component particleComponent : particleComponents)
        {
            entity.add(particleComponent);
        }

        LocalSpace localSpace = ParticleComponents.LOCAL_SPACE.get(entity);

        if (blueprint.containsFlipbook)
        {
            FlipbookModel flipbookModel = ParticleComponents.FLIPBOOK_MODEL.get(entity);
            flipbookModel.finishSetup(ParticleComponents.MAX_AGE.get(entity).maxAge);
        }

        /*
         * Position
         */
        var particlePosition = ParticleComponents.POSITION.create(particleEngine);
        emitter.offset.evaluate(emitter.environment);
        particlePosition.x = emitter.offset.fx();
        particlePosition.y = emitter.offset.fy();
        particlePosition.z = emitter.offset.fz();

        Vector3f position = emitter.shape.createPosition(emitter);
        setPosition(holder, emitter, emitterPosition, localSpace, particlePosition, position);
        entity.add(particlePosition);

        /*
         * Velocity
         */
        Velocity velocity = particleEngine.createComponent(Velocity.class);
        entity.add(velocity);

        ParticleDirectionBlueprint particleDirectionBlueprint = blueprint.direction;
        if (particleDirectionBlueprint != null)
        {
            var initialSpeedBlueprint = blueprint.initialSpeed;
            float initialSpeed = 0;
            if (initialSpeedBlueprint != null)
            {
                initialSpeed = (float) initialSpeedBlueprint.initialSpeed().evaluateAndGet(env);
            }
            particleDirectionBlueprint.direction(velocity, initialSpeed, position, env);
        }

        if (holderId != ParticleHolderId.UNASSIGNED)
        {
            ParticleFollowerId followerComponent = particleEngine.createComponent(ParticleFollowerId.class);
            followerComponent.entity = holder;
            followerComponent.followerId = holderId;
            followerComponent.locator = emitter.locator;
            entity.add(followerComponent);
        }

        particleEngine.addEntity(entity);
    }

    private static void setPosition(
        Entity holder,
        ParticleEmitter emitter,
        Position emitterPosition,
        LocalSpace localSpace,
        steve6472.orbiter.world.particle.components.Position particlePosition,
        Vector3f position)
    {
        if (emitter.locator != null)
        {
            AnimatedModel animatedModel = Components.ANIMATED_MODEL.get(holder);
            AnimationController.LocatorInfo locator = animatedModel.animationController.getLocator(emitter.locator);
            if (locator == null)
            {
                Log.warningOnce(LOGGER, "Locator '" + emitter.locator + "' not found");
                if (localSpace != null && localSpace.position)
                {
                    particlePosition.x += position.x;
                    particlePosition.y += position.y;
                    particlePosition.z += position.z;
                } else
                {
                    particlePosition.x += emitterPosition.x() + position.x;
                    particlePosition.y += emitterPosition.y() + position.y;
                    particlePosition.z += emitterPosition.z() + position.z;
                }
            } else
            {
                if (localSpace != null && localSpace.position)
                {
                    particlePosition.x += position.x;
                    particlePosition.y += position.y;
                    particlePosition.z += position.z;
                } else
                {
                    particlePosition.x += locator.position().x + position.x;
                    particlePosition.y += locator.position().y + position.y;
                    particlePosition.z += locator.position().z + position.z;
                }
            }
        }
        else
        {
            if (localSpace != null && localSpace.position)
            {
                particlePosition.x += position.x;
                particlePosition.y += position.y;
                particlePosition.z += position.z;
            } else
            {
                particlePosition.x += emitterPosition.x() + position.x;
                particlePosition.y += emitterPosition.y() + position.y;
                particlePosition.z += emitterPosition.z() + position.z;
            }
        }
    }
}
