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
import steve6472.orbiter.world.ecs.core.BlueprintEntry;
import steve6472.orbiter.world.ecs.core.Component;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/1/2024
 * Project: Orbiter <br>
 */
public class Registries extends RegistryCreators
{
    private static final Logger LOGGER = Log.getLogger(Registries.class);

    public static final ObjectRegistry<Setting<?, ?>> SETTINGS = createNamespacedObjectRegistry(Constants.NAMESPACE, "setting", () -> Settings.FOV);
    public static final Registry<Keybind> KEYBINDS = createNamespacedRegistry(Constants.NAMESPACE, "keybinds", () -> Keybinds.FORWARD);
    public static final Registry<Rarity> RARITY = createRegistry("rarity", () -> Rarities.COMMON);
    public static final PacketRegistry PACKET = createPacketRegistry("packet", Packets::init);
    public static final Registry<Component<?>> COMPONENT = createNamespacedRegistry(Constants.NAMESPACE, "component", () -> Components.POSITION);
    public static final Registry<BlueprintEntry<?>> BLUEPRINT = createRegistry("blueprint", () -> Blueprints.POSITION);
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
