package steve6472.orbiter;

import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.core.registry.ObjectRegistry;
import steve6472.core.registry.PacketRegistry;
import steve6472.core.registry.Registry;
import steve6472.core.setting.Setting;
import steve6472.flare.input.Keybind;
import steve6472.flare.registry.RegistryCreators;
import steve6472.orbiter.network.Packets;
import steve6472.orbiter.settings.Keybinds;
import steve6472.orbiter.settings.Settings;
import steve6472.orbiter.world.collision.OrbiterCollisionShape;
import steve6472.orbiter.world.ecs.Blueprints;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.emitter.lifetime.EmitterLifetimeType;
import steve6472.orbiter.world.ecs.components.emitter.rate.EmitterRateType;
import steve6472.orbiter.world.ecs.components.emitter.shapes.EmitterShapeType;
import steve6472.orbiter.world.ecs.core.BlueprintEntry;
import steve6472.orbiter.world.ecs.core.ComponentEntry;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;
import steve6472.orbiter.world.particle.ParticleComponentBlueprints;
import steve6472.orbiter.world.particle.core.PCBlueprintEntry;
import steve6472.orbiter.world.particle.core.ParticleBlueprint;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/1/2024
 * Project: Orbiter <br>
 */
public class Registries extends RegistryCreators
{
    private static final Logger LOGGER = Log.getLogger(Registries.class);

    /*
     * Typed
     */
    // Components of emitters
    public static final Registry<EmitterShapeType<?>> EMITTER_SHAPE = createRegistry("emitter_shape", () -> EmitterShapeType.POINT_SHAPE);
    public static final Registry<EmitterRateType<?>> EMITTER_RATE = createRegistry("emitter_rate", () -> EmitterRateType.INSTANT_RATE);
    public static final Registry<EmitterLifetimeType<?>> EMITTER_LIFETIME = createRegistry("emitter_lifetime", () -> EmitterLifetimeType.ONCE_LIFETIME);

    public static final ObjectRegistry<Setting<?, ?>> SETTINGS = createNamespacedObjectRegistry(Constants.NAMESPACE, "setting", () -> Settings.FOV);
    public static final Registry<Keybind> KEYBINDS = createNamespacedRegistry(Constants.NAMESPACE, "keybinds", () -> Keybinds.FORWARD);
    public static final Registry<Rarity> RARITY = createRegistry("rarity", () -> Rarities.COMMON);
    public static final PacketRegistry PACKET = createPacketRegistry("packet", Packets::init);

    public static final Registry<ComponentEntry<?>> PARTICLE_COMPONENT = createNamespacedRegistry(Constants.NAMESPACE, "particle_component", () -> Components.POSITION);
    public static final Registry<PCBlueprintEntry<?>> PARTICLE_COMPONENT_BLUEPRINT = createNamespacedRegistry(Constants.NAMESPACE, "particle_component_blueprint", () -> ParticleComponentBlueprints.SCALE);
    public static final ObjectRegistry<ParticleBlueprint> PARTICLE_BLUEPRINT = createObjectRegistry("particle_blueprint", ParticleBlueprint::load);

    public static final Registry<ComponentEntry<?>> COMPONENT = createNamespacedRegistry(Constants.NAMESPACE, "component", () -> Components.POSITION);
    public static final Registry<BlueprintEntry<?>> COMPONENT_BLUEPRINT = createRegistry("component_blueprint", () -> Blueprints.POSITION);
    public static final ObjectRegistry<EntityBlueprint> ENTITY_BLUEPRINT = createObjectRegistry("entity_blueprint", EntityBlueprint::load);
    public static final ObjectRegistry<OrbiterCollisionShape> COLLISION = createObjectRegistry("collision", OrbiterCollisionShape::load);

    // TODO: add to flare
    private static PacketRegistry createPacketRegistry(String id, Runnable bootstrap)
    {
        Key key = Key.withNamespace(Constants.NAMESPACE, id);
        LOGGER.finest("Creating Registry " + key);
        LOADERS.put(key, bootstrap);
        return new PacketRegistry(key);
    }
}
