package steve6472.orbiter;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.NativeLibrary;
import com.jme3.system.NativeLibraryLoader;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import steve6472.core.setting.SettingsLoader;
import steve6472.orbiter.player.PCPlayer;
import steve6472.orbiter.settings.Keybinds;
import steve6472.orbiter.settings.Settings;
import steve6472.orbiter.world.World;
import steve6472.test.TestRegistries;
import steve6472.volkaniums.core.FrameInfo;
import steve6472.volkaniums.core.VolkaniumsApp;
import steve6472.volkaniums.input.KeybindUpdater;
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
    private Client client;
    private World world;

    @Override
    protected void initRegistries()
    {
        PhysicsSpace.logger.setLevel(Level.WARNING);
        PhysicsRigidBody.logger2.setLevel(Level.WARNING);
        NativeLibraryLoader.logger.setLevel(Level.WARNING);
        NativeLibraryLoader.loadLibbulletjme(true, new File("dep"), "Debug", "Sp");
        NativeLibrary.setStartupMessageEnabled(false);

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

    }

    @Override
    public void fullInit()
    {
        KeybindUpdater.updateKeybinds(Registries.KEYBINDS, input());

        client = new Client(camera());
        world = new World();

        if (!VrData.VR_ON)
            world.physics().add(((PCPlayer) client.player()).character);
    }

    @Override
    public void render(FrameInfo frameInfo, MemoryStack memoryStack)
    {
        frameInfo.camera().setPerspectiveProjection(Settings.FOV.get(), aspectRatio(), 0.1f, 1024f);
        client.handleInput(input(), vrInput(), frameInfo.frameTime());
        world.tick(frameInfo.frameTime());
        client.tickClient(frameInfo.frameTime());

//        frameInfo.camera().setViewTarget(new Vector3f(1f, 1.5f, -1), new Vector3f(0, 0.5f, 0));
//        Vector2i mousePos = input().getMousePositionRelativeToTopLeftOfTheWindow();
//        if (window().isFocused())
//        {
//            frameInfo.camera().center.set(0, 0, 0);
//            frameInfo.camera().headOrbit(mousePos.x, mousePos.y, 0.4f, 8.5f);
//        }
    }

    @Override
    public void saveSettings()
    {
        SettingsLoader.saveToJsonFile(Registries.SETTINGS, Constants.SETTINGS);
    }

    @Override
    public void cleanup()
    {

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
