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
    public static final Keybind FORWARD = registerRepeatKey("forward", GLFW.GLFW_KEY_W);
    public static final Keybind BACKWARD = registerRepeatKey("backward", GLFW.GLFW_KEY_S);
    public static final Keybind LEFT = registerRepeatKey("left", GLFW.GLFW_KEY_A);
    public static final Keybind RIGHT = registerRepeatKey("right", GLFW.GLFW_KEY_D);
    public static final Keybind JUMP = registerRepeatKey("jump", GLFW.GLFW_KEY_SPACE);
    public static final Keybind SPRINT = registerRepeatKey("sprint", GLFW.GLFW_KEY_LEFT_SHIFT);

    public static final Keybind HOLD_OBJECT = registerRepeatMouse("hold_object", GLFW.GLFW_MOUSE_BUTTON_LEFT);
    public static final Keybind INTERACT_OBJECT = registerOnceMouse("interact_object", GLFW.GLFW_MOUSE_BUTTON_RIGHT);
    public static final Keybind TEST_ATTRACT = registerRepeatKey("test_attract", GLFW.GLFW_KEY_G);

    public static final Keybind ESCAPE = registerOnceKey("escape", GLFW.GLFW_KEY_ESCAPE);
    public static final Keybind CHAT = registerOnceKey("chat", GLFW.GLFW_KEY_GRAVE_ACCENT);
    public static final Keybind KEEP_CHAT_OPEN = registerRepeatKey("keep_chat_open", GLFW.GLFW_KEY_LEFT_SHIFT);
    public static final Keybind ENTER = registerOnceKey("enter", GLFW.GLFW_KEY_ENTER);
    public static final Keybind ENTER_KP = registerOnceKey("enter_kp", GLFW.GLFW_KEY_KP_ENTER);

    private static Keybind registerRepeatKey(String id, int key)
    {
        return register(Keybind.key(Key.withNamespace(Constants.NAMESPACE, id), KeybindType.REPEAT, key));
    }

    private static Keybind registerOnceKey(String id, int key)
    {
        return register(Keybind.key(Key.withNamespace(Constants.NAMESPACE, id), KeybindType.ONCE, key));
    }

    private static Keybind registerRepeatMouse(String id, int key)
    {
        return register(Keybind.mouse(Key.withNamespace(Constants.NAMESPACE, id), KeybindType.REPEAT, key));
    }

    private static Keybind registerOnceMouse(String id, int key)
    {
        return register(Keybind.mouse(Key.withNamespace(Constants.NAMESPACE, id), KeybindType.ONCE, key));
    }

    private static Keybind register(Keybind keybind)
    {
        return Registries.KEYBINDS.register(keybind);
    }
}
