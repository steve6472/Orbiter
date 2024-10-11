package steve6472.orbiter;

import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.core.registry.ObjectRegistry;
import steve6472.core.registry.PacketRegistry;
import steve6472.core.registry.Registry;
import steve6472.core.setting.Setting;
import steve6472.orbiter.network.Packets;
import steve6472.orbiter.settings.Keybinds;
import steve6472.orbiter.settings.Settings;
import steve6472.orbiter.world.OrbiterCollisionShape;
import steve6472.orbiter.world.ecs.Blueprints;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.core.BlueprintEntry;
import steve6472.orbiter.world.ecs.core.Component;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;
import steve6472.volkaniums.input.Keybind;
import steve6472.volkaniums.registry.RegistryCreators;

import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/1/2024
 * Project: Orbiter <br>
 */
public class Registries extends RegistryCreators
{
    private static final Logger LOGGER = Log.getLogger(Registries.class);

    public static final ObjectRegistry<Setting<?, ?>> SETTINGS = createObjectRegistry("setting", () -> Settings.FOV);
    public static final Registry<Keybind> KEYBINDS = createRegistry("keybinds", () -> Keybinds.FORWARD);
    public static final Registry<Rarity> RARITY = createRegistry("rarity", () -> Rarities.COMMON);
    public static final PacketRegistry PACKET = createPacketRegistry("packet", Packets::init);
    public static final Registry<Component<?>> COMPONENT = createRegistry("component", () -> Components.POSITION);
    public static final Registry<BlueprintEntry<?>> BLUEPRINT = createRegistry("blueprint", () -> Blueprints.POSITION);
    public static final ObjectRegistry<EntityBlueprint> ENTITY_BLUEPRINT = createObjectRegistry("entity_blueprint", EntityBlueprint::load);
    public static final ObjectRegistry<OrbiterCollisionShape> COLLISION = createObjectRegistry("collision", OrbiterCollisionShape::load);

    // TODO: add to volkaniums
    private static PacketRegistry createPacketRegistry(String id, Supplier<?> bootstrap)
    {
        Key key = Key.defaultNamespace(id);
        LOGGER.finest("Creating Registry " + key);
        LOADERS.put(key, bootstrap);
        return new PacketRegistry(key);
    }
}
