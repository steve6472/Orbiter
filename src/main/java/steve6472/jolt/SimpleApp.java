package steve6472.jolt;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import steve6472.core.registry.Registry;
import steve6472.flare.Camera;
import steve6472.flare.FlareConstants;
import steve6472.flare.core.FlareApp;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.input.Keybind;
import steve6472.flare.input.KeybindType;
import steve6472.flare.input.KeybindUpdater;
import steve6472.flare.registry.RegistryCreators;
import steve6472.flare.settings.VisualSettings;

import static steve6472.flare.render.debug.DebugRender.*;

/**
 * Created by steve6472
 * Date: 10/8/2025
 * Project: Orbiter <br>
 */
public abstract class SimpleApp extends FlareApp
{
    protected float X = 0;
    protected float Z = 0;
    protected float cameraDistance = 0.8f;
    protected boolean canControlCamera = true;

    @Override
    protected void preInit()
    {
    }

    @Override
    protected Camera setupCamera()
    {
        return new Camera();
    }

    @Override
    protected void initRegistries()
    {
        initRegistry(Regs.KEYBIND);
    }

    @Override
    public void loadSettings()
    {
        VisualSettings.TITLE_FPS.set(false);
        VisualSettings.LINE_WIDTH.set(1f);
    }

    @Override
    protected void createRenderSystems()
    {
    }

    @Override
    public void postInit()
    {
        KeybindUpdater.updateKeybinds(Regs.KEYBIND, input());
    }

    @Override
    public void render(FrameInfo frameInfo, MemoryStack memoryStack)
    {
        frameInfo.camera().setPerspectiveProjection(80f, aspectRatio(), 0.1f, 2048.0f);

        addDebugObjectForFrame(line(new Vector3f(0, 0, 0), new Vector3f(1, 0, 0), RED));
        addDebugObjectForFrame(line(new Vector3f(0, 0, 0), new Vector3f(0, 1, 0), GREEN));
        addDebugObjectForFrame(line(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1), BLUE));
    }

    protected void tickCamera(Camera camera)
    {
        Vector2i mousePos = input().getMousePositionRelativeToTopLeftOfTheWindow();

        camera.center.set(X, 0, Z);

        if (canControlCamera)
        {
            camera.headOrbit(mousePos.x, mousePos.y, 0.4f, cameraDistance);
        } else
        {
            camera.oldx = mousePos.x;
            camera.oldy = mousePos.y;
        }

        float cameraSpeed = 0.06f;

        if (Keys.SPRINT.isActive())
            cameraSpeed *= 10f;

        if (Keys.SLOW.isActive())
            cameraSpeed /= 4f;

        if (Keys.CAMERA_FAR.isActive())
            cameraDistance += cameraSpeed;
        if (Keys.CAMERA_CLOSE.isActive())
            cameraDistance -= cameraSpeed;
        cameraDistance = Math.clamp(cameraDistance, 0, 1024);

        if (Keys.TOGGLE_CAMERA_CONTROL.isActive())
        {
            canControlCamera = !canControlCamera;
        }

        float speed = 0.05f;

        if (Keys.SPRINT.isActive())
            speed *= 5f;

        if (Keys.SLOW.isActive())
            speed /= 5f;

        if (Keys.FORWARD.isActive())
        {
            X += (float) (Math.sin(camera.yaw()) * -speed);
            Z += (float) (Math.cos(camera.yaw()) * -speed);
        }

        if (Keys.BACK.isActive())
        {
            X += (float) (Math.sin(camera.yaw()) * speed);
            Z += (float) (Math.cos(camera.yaw()) * speed);
        }

        if (Keys.LEFT.isActive())
        {
            X += (float) (Math.sin(camera.yaw() + Math.PI / 2.0) * -speed);
            Z += (float) (Math.cos(camera.yaw() + Math.PI / 2.0) * -speed);
        }

        if (Keys.RIGHT.isActive())
        {
            X += (float) (Math.sin(camera.yaw() + Math.PI / 2.0) * speed);
            Z += (float) (Math.cos(camera.yaw() + Math.PI / 2.0) * speed);
        }
    }

    @Override
    public void saveSettings()
    {
    }

    @Override
    public void cleanup()
    {
    }

    @Override
    public String windowTitle()
    {
        return "Simple App";
    }

    @Override
    public String defaultNamespace()
    {
        return "simple_app";
    }

    static class Regs extends RegistryCreators
    {
        static {
            NAMESPACE = "simple_app";
        }

        public static final Registry<Keybind> KEYBIND = createRegistry("keybind", () -> Keys.FORWARD);
    }

    public static class Keys
    {
        public static final Keybind FORWARD = register(Keybind.key(FlareConstants.key("forward"), KeybindType.REPEAT, GLFW.GLFW_KEY_W));
        public static final Keybind LEFT = register(Keybind.key(FlareConstants.key("left"), KeybindType.REPEAT, GLFW.GLFW_KEY_A));
        public static final Keybind RIGHT = register(Keybind.key(FlareConstants.key("right"), KeybindType.REPEAT, GLFW.GLFW_KEY_D));
        public static final Keybind BACK = register(Keybind.key(FlareConstants.key("back"), KeybindType.REPEAT, GLFW.GLFW_KEY_S));
        public static final Keybind SPRINT = register(Keybind.key(FlareConstants.key("sprint"), KeybindType.REPEAT, GLFW.GLFW_KEY_LEFT_SHIFT));
        public static final Keybind SLOW = register(Keybind.key(FlareConstants.key("slow"), KeybindType.REPEAT, GLFW.GLFW_KEY_LEFT_CONTROL));

        public static final Keybind CAMERA_FAR = register(Keybind.key(FlareConstants.key("camera_far"), KeybindType.REPEAT, GLFW.GLFW_KEY_Q));
        public static final Keybind CAMERA_CLOSE = register(Keybind.key(FlareConstants.key("camera_close"), KeybindType.REPEAT, GLFW.GLFW_KEY_E));
        public static final Keybind TOGGLE_CAMERA_CONTROL = register(Keybind.key(FlareConstants.key("toggle_camera_control"), KeybindType.ONCE, GLFW.GLFW_KEY_ESCAPE));

        public static final Keybind G = register(Keybind.key(FlareConstants.key("special_action"), KeybindType.ONCE, GLFW.GLFW_KEY_G));
        public static final Keybind F = register(Keybind.key(FlareConstants.key("special_action_2"), KeybindType.ONCE, GLFW.GLFW_KEY_F));
        public static final Keybind L = register(Keybind.key(FlareConstants.key("dump_samplers"), KeybindType.ONCE, GLFW.GLFW_KEY_L));

        public static final Keybind TO_UP = register(Keybind.key(FlareConstants.key("to_up"), KeybindType.ONCE, GLFW.GLFW_KEY_UP));
        public static final Keybind TO_LEFT = register(Keybind.key(FlareConstants.key("to_left"), KeybindType.ONCE, GLFW.GLFW_KEY_LEFT));
        public static final Keybind TO_RIGHT = register(Keybind.key(FlareConstants.key("to_right"), KeybindType.ONCE, GLFW.GLFW_KEY_RIGHT));
        public static final Keybind TO_DOWN = register(Keybind.key(FlareConstants.key("to_down"), KeybindType.ONCE, GLFW.GLFW_KEY_DOWN));

        private static Keybind register(Keybind keybind)
        {
            Regs.KEYBIND.register(keybind);
            return keybind;
        }
    }


}
