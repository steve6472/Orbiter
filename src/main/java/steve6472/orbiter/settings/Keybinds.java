package steve6472.orbiter.settings;

import org.lwjgl.glfw.GLFW;
import steve6472.core.registry.Key;
import steve6472.flare.input.Keybind;
import steve6472.flare.input.KeybindType;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Registries;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class Keybinds
{
    public static final Keybind FORWARD = registerRepeat("forward", GLFW.GLFW_KEY_W);
    public static final Keybind BACKWARD = registerRepeat("backward", GLFW.GLFW_KEY_S);
    public static final Keybind LEFT = registerRepeat("left", GLFW.GLFW_KEY_A);
    public static final Keybind RIGHT = registerRepeat("right", GLFW.GLFW_KEY_D);
    public static final Keybind JUMP = registerRepeat("jump", GLFW.GLFW_KEY_SPACE);
    public static final Keybind SPRINT = registerRepeat("sprint", GLFW.GLFW_KEY_LEFT_SHIFT);

    public static final Keybind ESCAPE = registerOnce("escape", GLFW.GLFW_KEY_ESCAPE);
    public static final Keybind CHAT = registerOnce("chat", GLFW.GLFW_KEY_GRAVE_ACCENT);
    public static final Keybind KEEP_CHAT_OPEN = registerRepeat("keep_chat_open", GLFW.GLFW_KEY_LEFT_SHIFT);
    public static final Keybind ENTER = registerOnce("enter", GLFW.GLFW_KEY_ENTER);
    public static final Keybind ENTER_KP = registerOnce("enter_kp", GLFW.GLFW_KEY_KP_ENTER);

    private static Keybind registerRepeat(String id, int key)
    {
        return register(new Keybind(Key.withNamespace(Constants.NAMESPACE, id), KeybindType.REPEAT, key));
    }

    private static Keybind registerOnce(String id, int key)
    {
        return register(new Keybind(Key.withNamespace(Constants.NAMESPACE, id), KeybindType.ONCE, key));
    }

    private static Keybind register(Keybind keybind)
    {
        return Registries.KEYBINDS.register(keybind);
    }
}
