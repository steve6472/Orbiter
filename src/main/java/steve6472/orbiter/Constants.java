package steve6472.orbiter;

import com.mojang.serialization.Codec;
import org.joml.Vector3f;
import org.joml.Vector3fKt;
import org.joml.Vector3fc;
import steve6472.core.registry.Key;
import steve6472.flare.FlareConstants;

import java.io.File;

/**
 * Created by steve6472
 * Date: 10/1/2024
 * Project: Orbiter <br>
 */
public final class Constants
{
    /// The main namespace of Orbiter
    public static final String NAMESPACE = "orbiter";

    public static final File SETTINGS = new File("settings/orbiter_settings.json");

    public static final float TICKS_IN_SECOND = 60f;

    public static final Vector3fc GRAVITY = new Vector3f(0,-9.81f,0);
    public static final String ORCODE_PREFIX = "$";

//    public static final int MP_PLAYER_MAGIC_CONSTANT = 69;
//    public static final int CLIENT_PLAYER_MAGIC_CONSTANT = 42;

    /// Orbiter-generated resources
    public static final File GENERATED_ORBITER = new File(FlareConstants.GENERATED_FOLDER, NAMESPACE);
    public static final File JOLT_NATIVE = new File(GENERATED_ORBITER, "joltjni.dll");

    /// Particle Atlas key reference
    public static final Key ATLAS_PARTICLE = key("particle");

    /*
     * UI stuff
     */

    public interface UI
    {
        Key MAIN_MENU = key("panel/main_menu");
        Key SETTINGS = key("panel/settings");
        Key LOBBY_MENU_DEDICATED = key("panel/lobby_dedicated/menu");
        Key LOBBY_MENU_STEAM = key("panel/lobby_steam/menu");

        Key IN_GAME_MENU = key("panel/in_game/menu");
        Key IN_GAME_CHAT = key("panel/in_game/chat");
        Key IN_GAME_ECS_PROFILER = key("panel/in_game/ecs_profiler");
    }

    /*
     * Gameplay stuff
     */

    public interface Events
    {
        Key ON_SPAWN = key("on_spawn");
        Key ON_TICK = key("on_tick");
        Key ON_INTERACTION = key("on_interaction");
    }

    /// Set in userIndex2 <br>
    /// When setting flags on a body, the ORed result has to be negated <br>
    /// Because for some dumb reason the default value of the index is -1 <br>
    /// Checking for the flags requires !BitUtil.isBitSet(...) note the **!**
    public interface PhysicsFlags
    {
        /// Override debug physics rendering to never debug render this object <br>
        /// Should be used wisely! <br>
        /// Can still be ignored by a boolean flag in code
        int NEVER_DEBUG_RENDER = 0x1;
        int MP_PLAYER = 0x2;
        int CLIENT_PLAYER = 0x4;
    }

    public interface Physics
    {
        int NUM_OBJ_LAYERS = 2;

        int OBJ_LAYER_MOVING = 0;
        int OBJ_LAYER_NON_MOVING = 1;
    }

    public static Key key(String id)
    {
        return Key.withNamespace(NAMESPACE, id);
    }

    public static final Codec<Key> KEY_CODEC = Key.withDefaultNamespace(NAMESPACE);
}
