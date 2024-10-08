package steve6472.orbiter;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.NativeLibrary;
import com.jme3.system.NativeLibraryLoader;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import steve6472.core.registry.Key;
import steve6472.core.setting.SettingsLoader;
import steve6472.orbiter.commands.Commands;
import steve6472.orbiter.debug.DebugWindow;
import steve6472.orbiter.network.test.PacketTest;
import steve6472.orbiter.steam.SteamMain;
import steve6472.orbiter.player.PCPlayer;
import steve6472.orbiter.settings.Settings;
import steve6472.orbiter.world.World;
import steve6472.volkaniums.Camera;
import steve6472.volkaniums.core.FrameInfo;
import steve6472.volkaniums.core.VolkaniumsApp;
import steve6472.volkaniums.input.KeybindUpdater;
import steve6472.volkaniums.pipeline.Pipelines;
import steve6472.volkaniums.registry.VolkaniumsRegistries;
import steve6472.volkaniums.render.StaticModelRenderSystem;
import steve6472.volkaniums.render.debug.DebugRender;
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
    private static final boolean ENABLE_STEAM = true;
    private static final boolean ENABLE_TEST_ECHO = false;

    private static OrbiterApp instance;

    private SteamMain steam;
    private Client client;
    private World world;
    private Commands commands;
    private PacketTest packetTest;

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
        if (ENABLE_STEAM)
            steam.setup();

        if (ENABLE_TEST_ECHO && !ENABLE_STEAM)
        {
            packetTest = new PacketTest();
        }
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

        client = new Client(camera());
        world.init();

        if (!VrData.VR_ON)
            world.physics().add(((PCPlayer) client.player()).character);

        commands = new Commands();
        DebugWindow.openDebugWindow(commands, client, world, steam);


        PhysicsRigidBody body = new PhysicsRigidBody(new CapsuleCollisionShape(PCPlayer.RADIUS, PCPlayer.HEIGHT / 2f));
        world.addPhysicsEntity(body, VolkaniumsRegistries.STATIC_MODEL.get(Key.defaultNamespace("blockbench/static/player_capsule")));
    }

    private float timeToNextTick = 0;

    @Override
    public void render(FrameInfo frameInfo, MemoryStack memoryStack)
    {
        if (!OrbiterMain.STEAM_TEST)
            frameInfo.camera().setPerspectiveProjection(Settings.FOV.get(), aspectRatio(), 0.1f, 1024f);

        float frameTime = frameInfo == null ? (1f / Constants.TICKS_IN_SECOND) : frameInfo.frameTime();

        if (!OrbiterMain.STEAM_TEST)
            client.handleInput(input(), vrInput(), frameTime);

        timeToNextTick -= frameTime;
        if (timeToNextTick <= 0)
        {
            tick(frameTime);
            timeToNextTick += 1f / Constants.TICKS_IN_SECOND;
        }

        if (!OrbiterMain.STEAM_TEST)
        {
            world.debugRender();
            DebugRender.addDebugObjectForFrame(DebugRender.lineCube(new Vector3f(client.player().getFeetPos()).sub(0.4f, 0, 0.4f), new Vector3f(client.player().getFeetPos()).add(0.4f, 1.8f, 0.4f), DebugRender.WHITE));
        }
    }

    private void tick(float frameTime)
    {
        steam.tick();

        world.tick();
        client.tickClient();

        if (packetTest != null)
            packetTest.tick();
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
