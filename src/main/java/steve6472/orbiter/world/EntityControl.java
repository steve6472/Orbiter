package steve6472.orbiter.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.github.stephengold.joltjni.*;
import com.github.stephengold.joltjni.enumerate.EActivation;
import com.github.stephengold.joltjni.enumerate.EMotionType;
import org.joml.Vector3f;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.core.util.RandomUtil;
import steve6472.flare.assets.model.blockbench.animation.controller.AnimationController;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Convert;
import steve6472.orbiter.Registries;
import steve6472.orbiter.actions.Action;
import steve6472.orbiter.audio.Sound;
import steve6472.orbiter.audio.WorldSounds;
import steve6472.orbiter.network.api.Connections;
import steve6472.orbiter.network.api.NetworkMain;
import steve6472.orbiter.network.packets.game.clientbound.CreateCustomEntity;
import steve6472.orbiter.network.packets.game.clientbound.RemoveEntity;
import steve6472.orbiter.network.packets.game.clientbound.CreateEntity;
import steve6472.orbiter.properties.Property;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.blueprints.ParticleEmittersBlueprint;
import steve6472.orbiter.world.ecs.components.AnimatedModel;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orbiter.world.ecs.components.physics.*;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;
import steve6472.orbiter.world.emitter.ParticleEmitter;
import steve6472.orbiter.world.emitter.ParticleEmitters;
import steve6472.orlang.OrlangValue;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Created by steve6472
 * Date: 10/3/2024
 * Project: Orbiter <br>
 */
@SuppressWarnings("UnusedReturnValue")
public interface EntityControl extends WorldSounds
{
    Logger CONTROL_LOGGER = Log.getLogger(EntityControl.class);
    PhysicsSystem physics();
    Engine ecsEngine();
    JoltBodies bodyMap();
    NetworkMain network();
    World getItself();

    private Connections connections()
    {
        return network().connections();
    }


    default Entity addEntity(EntityBlueprint entityBlueprint, UUID uuid, Map<String, OrlangValue> arguments, boolean broadcast)
    {
        Vector3f randomSpawnPos = new Vector3f(RandomUtil.randomFloat(-1, 1), RandomUtil.randomFloat(0.5f, 1.5f), RandomUtil.randomFloat(-1, 1));
        return addEntity(entityBlueprint, uuid, arguments, randomSpawnPos, broadcast);
    }

    default Entity addEntity(EntityBlueprint entityBlueprint, UUID uuid, Map<String, OrlangValue> arguments, Vector3f pos, boolean broadcast)
    {
        Map<String, Property> spawnArguments = entityBlueprint.getSpawnArguments().required();
        Set<String> required = new HashSet<>(spawnArguments.keySet());
        required.removeAll(arguments.keySet());
        if (!required.isEmpty())
        {
            CONTROL_LOGGER.severe("Missing required spawn arguments: " + required);
            return null;
        }

        Entity entity = ecsEngine().createEntity();
        List<Component> components = entityBlueprint.createEntityComponents(entity, uuid);
        createEntity(entity, components);

        // Attach entity query functions
        OrlangEnv orlangEnv = Components.ENVIRONMENT.get(entity);
        if (orlangEnv != null)
        {
            orlangEnv.setQueryFunctionSet(QueryFunction.ENTITY, entity);
        }

        // Attach emitter query functions to existing emitters
        ParticleEmitters emitters = Components.PARTICLE_EMITTERS.get(entity);
        if (emitters != null)
        {
            for (ParticleEmitter emitter : emitters.emitters)
            {
                emitter.environment.queryFunctionSet = QueryFunction.EMITTER.createFunctionSet(entity);
                emitter.emitterTick();
            }
        }

        // Create callbacks for animated models
        AnimatedModel animatedModel = Components.ANIMATED_MODEL.get(entity);
        if (animatedModel != null)
        {
            AnimationController controller = animatedModel.animationController;
            controller.callbacks().onParticle = particleData ->
            {
                Key key = Key.parse(particleData.effect());
                ParticleEmittersBlueprint.EmitterEntry emitterEntry = Registries.EMITTER_BLUEPRINT.get(key);
                if (emitterEntry == null)
                    return;
                ParticleEmitter emitter = emitterEntry.toEmitter();
                if (!particleData.locator().isBlank())
                    emitter.locator = particleData.locator();
                emitter.environment.queryFunctionSet = QueryFunction.EMITTER.createFunctionSet(entity);
                emitter.emitterTick();

                ParticleEmitters particleEmitters = Components.PARTICLE_EMITTERS.get(entity);
                if (particleEmitters == null)
                {
                    particleEmitters = new ParticleEmitters(new ArrayList<>());
                    particleEmitters.emitters.add(emitter);
                    entity.add(particleEmitters);
                } else
                {
                    particleEmitters.emitters.add(emitter);
                }
            };

            controller.callbacks().onSound = soundData ->
            {
                Key parse = Key.parse(Constants.NAMESPACE, soundData.effect());
                Sound sound = Registries.SOUND.get(parse);
                if (sound == null)
                {
                    Log.warningOnce(CONTROL_LOGGER, "Sound '%s' not found".formatted(parse));
                    return;
                }

                if (!soundData.locator().isBlank())
                {
                    AnimationController.LocatorInfo locator = controller.getLocator(soundData.locator());
                    addSound(sound, locator.position().x, locator.position().y, locator.position().z, 1.0f, 0.5f);
                } else
                {
                    Position position = Components.POSITION.get(entity);
                    if (position == null)
                        return;

                    addSound(sound, position.x(), position.y(), position.z(), 1.0f, 0.5f);
                }
            };
        }

        // Special physics tag handling
        handlePhysics(entity, components, pos);

        if (orlangEnv != null)
        {
            entityBlueprint.getEvent(Constants.Events.ON_SPAWN).ifPresent(action -> Action.startAction(action, getItself(), entity, arguments, UnaryOperator.identity()));
        }

        // Broadcast new entity to peers
        if (broadcast && connections() != null && network().lobby().isHost())
            connections().broadcastPacket(new CreateEntity(uuid, entityBlueprint.key()));

        return entity;
    }

    default Entity addCustomEntity(UUID uuid, Collection<Component> components, boolean broadcast)
    {
        components.add(new UUIDComp(uuid));
        Entity entity = createEntity(ecsEngine().createEntity(), components);

        Vector3f pos = new Vector3f();
        Position position = Components.POSITION.get(entity);
        if (position != null)
            pos.set(position.x(), position.y(), position.z());

        // Special physics tag handling
        handlePhysics(entity, components, pos);

        // Broadcast new entity to peers
        if (broadcast && connections() != null && network().lobby().isHost())
        {
            CreateCustomEntity packet = new CreateCustomEntity(entity);
            connections().broadcastPacket(packet);
        }

        return entity;
    }

    private void handlePhysics(Entity entity, Collection<Component> components, Vector3f pos)
    {
        if (!Components.TAG_PHYSICS.has(entity))
            return;

        UUID uuid = Components.UUID.get(entity).uuid();
        Objects.requireNonNull(uuid);

        Collision collision_ = Components.COLLISION.get(entity);
        if (collision_ == null)
            throw new RuntimeException("Entity blueprint needs Collision if Physics tag is specified!");

        Position position = Components.POSITION.get(entity);
        position.set(pos.x, pos.y, pos.z);
        Rotation rotation = Components.ROTATION.get(entity);

        BodyCreationSettings bcs = new BodyCreationSettings(
            collision_.shape(),
            Convert.jomlToPhys(position.toVec3f()).toRVec3(),
            Convert.jomlToPhys(rotation.toQuat()),
            EMotionType.Dynamic,
            Constants.Physics.OBJ_LAYER_MOVING);

        BodyInterface bi = physics().getBodyInterface();
        Body body = bi.createBody(bcs);
        int bodyId = body.getId();

        bodyMap().addBody(uuid, body);
        bi.addBody(body, EActivation.Activate);

        for (Object component : components)
        {
            if (component instanceof PhysicsProperty pp)
            {
                pp.modifyBody(bi, bodyId);
            }
        }
    }

    /// Sends the RemoveEntity packet
    default void removeEntity(UUID uuid, boolean broadcast)
    {
        BodyInterface bodyInterface = physics().getBodyInterface();
        int id = bodyMap().getIdByUUID(uuid);
        if (bodyInterface.isAdded(id))
        {
            bodyInterface.removeBody(id);
            bodyInterface.destroyBody(id);
            bodyMap().removeBody(id);
        }

        for (Entity entity : ecsEngine().getEntitiesFor(Family.all(UUIDComp.class).get()))
        {
            if (Components.UUID.get(entity).uuid().equals(uuid))
            {
                ecsEngine().removeEntity(entity);
            }
        }

        if (broadcast && connections() != null && network().lobby().isHost())
            connections().broadcastPacket(new RemoveEntity(uuid));
    }

    default Optional<Entity> getEntityByUUID(UUID uuid)
    {
        return Stream.of(ecsEngine().getEntitiesFor(Family.all(UUIDComp.class).get()).toArray(Entity.class)).filter(e -> Components.UUID.get(e).uuid().equals(uuid)).findAny();
    }

    private Entity createEntity(Entity entity, Collection<Component> components)
    {
        for (Component component : components)
        {
            entity.add(component);
        }
        ecsEngine().addEntity(entity);
        return entity;
    }
}
