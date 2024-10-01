package steve6472.orbiter;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import steve6472.core.setting.SettingsLoader;
import steve6472.orbiter.settings.Settings;
import steve6472.volkaniums.core.FrameInfo;
import steve6472.volkaniums.core.VolkaniumsApp;

/**
 * Created by steve6472
 * Date: 10/1/2024
 * Project: Orbiter <br>
 */
public class OrbiterApp extends VolkaniumsApp
{
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

    }

    @Override
    public void fullInit()
    {

    }

    @Override
    public void render(FrameInfo frameInfo, MemoryStack memoryStack)
    {
        frameInfo.camera().setViewTarget(new Vector3f(1f, 1.5f, -1), new Vector3f(0, 0.5f, 0));
        Vector2i mousePos = input().getMousePositionRelativeToTopLeftOfTheWindow();
        frameInfo.camera().setPerspectiveProjection(Settings.FOV.get(), aspectRatio(), 0.1f, 1024f);
        if (window().isFocused())
        {
            frameInfo.camera().center.set(0, 0, 0);
            frameInfo.camera().headOrbit(mousePos.x, mousePos.y, 0.4f, 8.5f);
        }
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
