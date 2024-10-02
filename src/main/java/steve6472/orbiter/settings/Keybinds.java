package steve6472.orbiter.settings;

import org.lwjgl.glfw.GLFW;
import steve6472.core.registry.Key;
import steve6472.orbiter.Registries;
import steve6472.volkaniums.input.Keybind;
import steve6472.volkaniums.input.KeybindType;

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

    public static final Keybind TEST = registerOnce("test", GLFW.GLFW_KEY_G);

    private static Keybind registerRepeat(String id, int key)
    {
        return register(new Keybind(Key.defaultNamespace(id), KeybindType.REPEAT, key));
    }

    private static Keybind registerOnce(String id, int key)
    {
        return register(new Keybind(Key.defaultNamespace(id), KeybindType.ONCE, key));
    }

    private static Keybind register(Keybind keybind)
    {
        return Registries.KEYBINDS.register(keybind);
    }
}
