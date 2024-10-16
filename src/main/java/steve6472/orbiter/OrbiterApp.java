package steve6472.orbiter;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.NativeLibrary;
import com.jme3.system.NativeLibraryLoader;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import steve6472.core.registry.Key;
import steve6472.core.setting.SettingsLoader;
import steve6472.orbiter.commands.Commands;
import steve6472.orbiter.debug.DebugWindow;
import steve6472.orbiter.network.packets.game.AcceptedPeerConnection;
import steve6472.orbiter.scheduler.Scheduler;
import steve6472.orbiter.settings.Keybinds;
import steve6472.orbiter.steam.SteamMain;
import steve6472.orbiter.player.PCPlayer;
import steve6472.orbiter.settings.Settings;
import steve6472.orbiter.world.World;
import steve6472.volkaniums.Camera;
import steve6472.volkaniums.core.FrameInfo;
import steve6472.volkaniums.core.VolkaniumsApp;
import steve6472.volkaniums.input.KeybindUpdater;
import steve6472.volkaniums.pipeline.Pipelines;
import steve6472.volkaniums.render.StaticModelRenderSystem;
import steve6472.volkaniums.render.debug.DebugRender;
import steve6472.volkaniums.settings.VisualSettings;
import steve6472.volkaniums.vr.VrData;

import java.io.File;
import java.util.logging.Level;

/**
 * Created by steve6472
 * Date: 10/1/2024
 * Project: Orbiter <br>
 */
public class OrbiterApp extends VolkaniumsApp
{
    private static OrbiterApp instance;

    private SteamMain steam;
    private Client client;
    private World world;
    private Commands commands;
    private boolean isMouseGrabbed = false;

    OrbiterApp()
    {
        if (instance != null)
            throw new RuntimeException("OrbiterApp already started");
        instance = this;
    }

    @Override
    protected void preInit()
    {
        PhysicsSpace.logger.setLevel(Level.WARNING);
        PhysicsRigidBody.logger2.setLevel(Level.WARNING);
        NativeLibraryLoader.logger.setLevel(Level.WARNING);
        NativeLibraryLoader.loadLibbulletjme(true, new File("dep"), "Debug", "Sp");
        NativeLibrary.setStartupMessageEnabled(false);

        world = new World();

        steam = new SteamMain(this);
        if (OrbiterMain.ENABLE_STEAM || OrbiterMain.FAKE_P2P)
            steam.setup();
        world.steam = steam;
    }

    @Override
    protected Camera setupCamera()
    {
        return new Camera();
    }

    @Override
    protected void initRegistries()
    {
        initRegistry(Registries.SETTINGS);
    }

    @Override
    public void loadSettings()
    {
        SettingsLoader.loadFromJsonFile(Registries.SETTINGS, Constants.SETTINGS);

        if (OrbiterMain.FAKE_P2P)
        {
            if (OrbiterMain.FAKE_PEER)
                VisualSettings.VR.set(true);
            else
                VisualSettings.VR.set(false);
        }
    }

    @Override
    protected void createRenderSystems()
    {
        addRenderSystem(new StaticModelRenderSystem(masterRenderer(), new StaticWorldRender(world), Pipelines.BLOCKBENCH_STATIC));
    }

    @Override
    public void postInit()
    {
        KeybindUpdater.updateKeybinds(Registries.KEYBINDS, input());

        world.init();
        client = new Client(camera(), world);

        if (!VrData.VR_ON)
            world.physics().add(((PCPlayer) client.player()).character);

        commands = new Commands();
        DebugWindow.openDebugWindow(commands, client, world, steam);

        if (OrbiterMain.FAKE_P2P)
        {
            steam.connections.broadcastMessage(new AcceptedPeerConnection(VrData.VR_ON));
        }
    }

    private float timeToNextTick = 0;

    @Override
    public void render(FrameInfo frameInfo, MemoryStack memoryStack)
    {
        if (!OrbiterMain.STEAM_TEST)
            frameInfo.camera().setPerspectiveProjection(Settings.FOV.get(), aspectRatio(), 0.1f, 1024f);

        float frameTime = frameInfo == null ? (1f / Constants.TICKS_IN_SECOND) : frameInfo.frameTime();

        if ((!OrbiterMain.STEAM_TEST && isMouseGrabbed) || VrData.VR_ON)
            client.handleInput(input(), vrInput(), frameTime);

        timeToNextTick -= frameTime;
        if (timeToNextTick <= 0)
        {
            tick(frameTime);
            timeToNextTick += 1f / Constants.TICKS_IN_SECOND;
        }

        world.debugRender();
    }

    private void tick(float frameTime)
    {
        //noinspection deprecation
        Scheduler.instance().tick();
        steam.tick();

        client.tickClient();
        world.tick();

        if (Keybinds.TOGGLE_GRAB_MOUSE.isActive() || Keybinds.DISABLE_GRAB_MOUSE.isActive())
        {
            isMouseGrabbed = !isMouseGrabbed;
            GLFW.glfwSetInputMode(window().window(), GLFW.GLFW_CURSOR, isMouseGrabbed ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
        }
    }

    public boolean isMouseGrabbed()
    {
        return isMouseGrabbed;
    }

    public World getWorld()
    {
        return world;
    }

    public Client getClient()
    {
        return client;
    }

    public SteamMain getSteam()
    {
        return steam;
    }

    public static OrbiterApp getInstance()
    {
        return instance;
    }

    @Override
    public void saveSettings()
    {
        SettingsLoader.saveToJsonFile(Registries.SETTINGS, Constants.SETTINGS);
    }

    @Override
    public void cleanup()
    {
        DebugWindow.closeDebugWindow();
        steam.shutdown();
    }

    @Override
    public String windowTitle()
    {
        return "Orbiter";
    }

    @Override
    public String defaultNamespace()
    {
        return "orbiter";
    }
}
