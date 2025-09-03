package steve6472.orbiter;

import com.mojang.serialization.Codec;
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

    public static final int MP_PLAYER_MAGIC_CONSTANT = 69;
    public static final int CLIENT_PLAYER_MAGIC_CONSTANT = 42;

    /// Orbiter-generated resources
    public static final File GENERATED_ORBITER = new File(FlareConstants.GENERATED_FOLDER, NAMESPACE);
    public static final File BULLET_NATIVE = new File(GENERATED_ORBITER, "bulletjme.dll");

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

    public static Key key(String id)
    {
        return Key.withNamespace(NAMESPACE, id);
    }

    public static final Codec<Key> KEY_CODEC = Key.withDefaultNamespace(NAMESPACE);
}
