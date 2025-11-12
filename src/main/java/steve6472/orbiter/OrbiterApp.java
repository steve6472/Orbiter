package steve6472.orbiter;

import com.github.stephengold.joltjni.Jolt;
import com.github.stephengold.joltjni.JoltPhysicsObject;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import steve6472.core.registry.Key;
import steve6472.core.setting.SettingsLoader;
import steve6472.flare.Camera;
import steve6472.flare.core.Flare;
import steve6472.flare.core.FlareApp;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.input.KeybindUpdater;
import steve6472.flare.pipeline.Pipelines;
import steve6472.flare.render.*;
import steve6472.flare.tracy.FlareProfiler;
import steve6472.flare.tracy.Profiler;
import steve6472.flare.vr.VrData;
import steve6472.moondust.*;
import steve6472.moondust.builtin.BuiltinEventCalls;
import steve6472.moondust.builtin.JavaFunctions;
import steve6472.moondust.render.DebugWidgetUILines;
import steve6472.moondust.render.MoonDustUIFontRender;
import steve6472.moondust.render.MoonDustUIRender;
import steve6472.moondust.view.Command;
import steve6472.moondust.view.PanelViewEntry;
import steve6472.moondust.widget.Panel;
import steve6472.moondust.widget.Widget;
import steve6472.moondust.widget.component.ViewController;
import steve6472.orbiter.commands.Commands;
import steve6472.orbiter.network.api.NetworkMain;
import steve6472.orbiter.network.impl.dedicated.DedicatedMain;
import steve6472.orbiter.rendering.gizmo.DrawableGizmoPrimitives;
import steve6472.orbiter.rendering.gizmo.GizmoInstance;
import steve6472.orbiter.rendering.gizmo.GizmoMaterial;
import steve6472.orbiter.rendering.gizmo.Gizmos;
import steve6472.orbiter.rendering.snapshot.WorldRenderState;
import steve6472.orbiter.rendering.snapshot.system.*;
import steve6472.orbiter.rendering.*;
import steve6472.orbiter.rendering.snapshot.system.gizmo.LineGizmoRenderSystem;
import steve6472.orbiter.rendering.snapshot.system.gizmo.PointGizmoRenderSystem;
import steve6472.orbiter.rendering.snapshot.system.gizmo.TriGizmoRenderSystem;
import steve6472.orbiter.settings.Keybinds;
import steve6472.orbiter.settings.Settings;
import steve6472.orbiter.ui.MDUtil;
import steve6472.orbiter.ui.OrbiterUIRender;
import steve6472.orbiter.ui.panel.*;
import steve6472.orbiter.world.World;
import steve6472.test.DebugUILines;

import java.util.Optional;

/**
 * Created by steve6472
 * Date: 10/1/2024
 * Project: Orbiter <br>
 */
public class OrbiterApp extends FlareApp
{
    /* # TODOS:
     *
     *  [x] Unify network game/play naming to just game
     *  [x] Fix disconnecting
     *  [ ] System profiling
     *  [ ] Fix camera glitching when opening menu
     *  [ ] Better Bandwidth tracker
     *  [x] Client/Host systems
     *  [ ] Fix player collision MP bug
     *
     *  [x] Particle ECS
     *  [x] Particle components
     *  [x] Particle systems
     *  [ ] Particle tinting renderer, without normals
     *  [ ] Fix max_count for steady rate
     *  [x] Emitter anchor to a locator from animated model
     *  [x] "unify" render pipelines into opaque (default for plane/flipbook), alpha_test, blend (default for model), additive. "shaded" flag in the blueprint
     *
     *  akma asks:
     *  [ ] Mana system
     *  [ ] Ice brand arts
     *
     *  dj asks:
     *  [ ] Explosions (Megumin style probably)
     *
     *
     *
     *
     *
     */

    private static OrbiterApp instance;

    private NetworkMain networkMain;
    private Client client;
    private Commands commands;
    private boolean isMouseGrabbed = false;

    public PhysicsDebugRenderer physicsDebugRenderer;
    public DrawableGizmoPrimitives drawableGizmoPrimitives = new DrawableGizmoPrimitives();
    public DrawableGizmoPrimitives drawableGizmoPrimitivesAlwaysOnTop = new DrawableGizmoPrimitives();

    OrbiterApp()
    {
        if (instance != null)
            throw new RuntimeException("OrbiterApp already started");
        instance = this;
    }

    @Override
    protected void preInit()
    {
        // Load Jolt native library
        System.load(Constants.JOLT_NATIVE.getAbsolutePath());
        //Jolt.setTraceAllocations(true); // to log Jolt-JNI heap allocations
        JoltPhysicsObject.startCleaner(); // to reclaim native memory
        Jolt.registerDefaultAllocator(); // tell Jolt Physics to use malloc/free
        Jolt.installDefaultAssertCallback();
        Jolt.installDefaultTraceCallback();
        boolean success = Jolt.newFactory();
        assert success;
        Jolt.registerTypes();

        client = new Client();
    }

    @Override
    protected Camera setupCamera()
    {
        return new Camera();
    }

    @Override
    protected void initRegistries()
    {
        MoonDustRegs.VIEW_ENTRIES.add(new PanelViewEntry(Constants.key("main_menu"), MainMenu::new));
        MoonDustRegs.VIEW_ENTRIES.add(new PanelViewEntry(Constants.key("in_game_menu"), InGameMenu::new));
        MoonDustRegs.VIEW_ENTRIES.add(new PanelViewEntry(Constants.key("chat"), InGameChat::new));
        MoonDustRegs.VIEW_ENTRIES.add(new PanelViewEntry(Constants.key("settings"), SettingsMenu::new));
        MoonDustRegs.VIEW_ENTRIES.add(new PanelViewEntry(Constants.key("lobby_dedicated"), LobbyMenuDedicated::new));
        MoonDustRegs.VIEW_ENTRIES.add(new PanelViewEntry(Constants.key("ecs_profiler"), InGameECSProfiler::new));

        initRegistry(MoonDustRegistries.POSITION_BLUEPRINT_TYPE);
        JavaFunctions.init(this);
        BuiltinEventCalls.init();
        initRegistry(Registries.SETTINGS);
    }

    @Override
    public void loadSettings()
    {
        SettingsLoader.loadFromJsonFile(Registries.SETTINGS, Constants.SETTINGS);
        SettingsLoader.loadFromJsonFile(MoonDustRegistries.SETTINGS, MoonDustConstants.SETTINGS_FILE);

        MoonDust.getInstance().setPixelScale(Settings.UI_SCALE.get());
//        VisualSettings.USERNAME.set(RandomNameGenerator.generateFullName());
    }

    @Override
    protected void createRenderSystems()
    {
        Configuration.DEBUG_MEMORY_ALLOCATOR.set(true);
        physicsDebugRenderer = new PhysicsDebugRenderer(masterRenderer());

        addRenderSystem(new DebugLineRenderSystem(masterRenderer(), Pipelines.DEBUG_LINE));
        addRenderSystem(new UIRenderSystem(masterRenderer(), new MoonDustUIRender(this), 256f));
        addRenderSystem(new UIFontRender(masterRenderer(), new MoonDustUIFontRender()));
        addRenderSystem(new UIRenderSystem(masterRenderer(), new OrbiterUIRender(this), 256f));
        // Debug
        addRenderSystem(new UILineRender(masterRenderer(), new DebugWidgetUILines()));
        addRenderSystem(new UILineRender(masterRenderer(), new DebugUILines()));

        addRenderSystem(new StaticModelRenderSystem(masterRenderer(), new StaticWorldRenderSystem(client), Pipelines.BLOCKBENCH_STATIC));
        addRenderSystem(new AnimatedModelRenderSystem(masterRenderer(), Pipelines.SKIN, client));

        addRenderSystem(new PhysicsOutlineRenderSystem(masterRenderer(), false, client));
        addRenderSystem(new PhysicsOutlineRenderSystem(masterRenderer(), true, client));

        // Non blend
        addRenderSystem(new PlaneParticleRenderSystem(masterRenderer(), ParticleMaterial.OPAQUE, client));
        addRenderSystem(new PlaneTintedParticleRenderSystem(masterRenderer(), ParticleMaterial.OPAQUE_TINT, client));
        addRenderSystem(new PlaneParticleRenderSystem(masterRenderer(), ParticleMaterial.ALPHA_TEST, client));
        addRenderSystem(new FlipbookRenderSystem(masterRenderer(), ParticleMaterial.ALPHA_TEST, client));
        addRenderSystem(new PlaneTintedParticleRenderSystem(masterRenderer(), ParticleMaterial.ALPHA_TEST_TINT, client));
        addRenderSystem(new FlipbookTintedRenderSystem(masterRenderer(), ParticleMaterial.ALPHA_TEST_TINT, client));

        addRenderSystem(new PointGizmoRenderSystem(masterRenderer(), GizmoMaterial.OPAQUE));
        addRenderSystem(new PointGizmoRenderSystem(masterRenderer(), GizmoMaterial.OPAQUE_ON_TOP));
        addRenderSystem(new LineGizmoRenderSystem(masterRenderer(), GizmoMaterial.OPAQUE));
        addRenderSystem(new LineGizmoRenderSystem(masterRenderer(), GizmoMaterial.OPAQUE_ON_TOP));
        addRenderSystem(new TriGizmoRenderSystem(masterRenderer(), GizmoMaterial.OPAQUE));
        addRenderSystem(new TriGizmoRenderSystem(masterRenderer(), GizmoMaterial.OPAQUE_ON_TOP));

        // Blend
        addRenderSystem(new PlaneTintedParticleRenderSystem(masterRenderer(), ParticleMaterial.BLEND, client));

        // Additive
        addRenderSystem(new PlaneTintedParticleRenderSystem(masterRenderer(), ParticleMaterial.ADDITIVE, client));

        addRenderSystem(new PointGizmoRenderSystem(masterRenderer(), GizmoMaterial.BLEND));
        addRenderSystem(new PointGizmoRenderSystem(masterRenderer(), GizmoMaterial.BLEND_ON_TOP));
        addRenderSystem(new LineGizmoRenderSystem(masterRenderer(), GizmoMaterial.BLEND));
        addRenderSystem(new LineGizmoRenderSystem(masterRenderer(), GizmoMaterial.BLEND_ON_TOP));
        addRenderSystem(new TriGizmoRenderSystem(masterRenderer(), GizmoMaterial.BLEND));
        addRenderSystem(new TriGizmoRenderSystem(masterRenderer(), GizmoMaterial.BLEND_ON_TOP));

        new MoonDustCallbacks().init(window().callbacks(), input());
    }

    @Override
    public void postInit()
    {
        MoonDust.getInstance().setWindow(window());

        KeybindUpdater.updateKeybinds(Registries.KEYBINDS, input());
        KeybindUpdater.updateKeybinds(MoonDustRegistries.KEYBIND, input());

        Panel testPanel = Panel.create(Key.withNamespace(Constants.NAMESPACE, "panel/main_menu"));
        testPanel.clearFocus();
        MoonDust.getInstance().addPanel(testPanel);

        client.setCamera(camera());

        commands = new Commands();

        swapNetworkBackend(Settings.MULTIPLAYER_BACKEND.get());

        Flare.getModuleManager().clearPartsCache();
    }

    private float timeToNextTick = 0;
    public float partialTicks = 0;
    public WorldRenderState currentRenderState;

    @Override
    public void render(FrameInfo frameInfo, MemoryStack memoryStack)
    {
        Profiler profiler = FlareProfiler.frame();
        profiler.push("orbiter render");
        profiler.push("set perspective");
        frameInfo.camera().setPerspectiveProjection(Settings.FOV.get(), aspectRatio(), 0.1f, 1024f);

        profiler.popPush("clearPerFrameGizmos");
        drawableGizmoPrimitives.clearAll();
        drawableGizmoPrimitivesAlwaysOnTop.clearAll();
        // Gizmos can be added from here on out

        float frameTime = frameInfo.frameTime();
        float tickDuration = 1f / Constants.TICKS_IN_SECOND;
        profiler.popPush("handle input");
        if ((isMouseGrabbed) || VrData.VR_ON)
            client.handleInput(input(), vrInput(), frameTime);

        profiler.popPush("tick");
        timeToNextTick -= frameTime;
        while (timeToNextTick < 0)
        {
            frameTick();
            timeToNextTick += tickDuration;
        }

        profiler.popPush("render state");
        // get should only ever be called once
        // before the end of this render method as this runs before the render systems
        currentRenderState = client.worldRenderState.get();
        if (currentRenderState != null)
        {
            partialTicks = computePartialTicks(currentRenderState);
            profiler.push("create render pairs");
            currentRenderState.createRenderPairs();
            profiler.popPush("prepare");
            currentRenderState.prepare(frameInfo.camera().viewPosition, partialTicks);
            profiler.pop();
            camera().viewPosition.set(currentRenderState.lastSnapshot.cameraPosition.lerp(currentRenderState.currentSnapshot.cameraPosition, partialTicks, new Vector3f()));
        }

        profiler.popPush("prepareRenderGizmos");
        prepareRenderGizmos(frameInfo.camera().viewPosition);

//        client.render(frameInfo, memoryStack);
        profiler.pop();
        profiler.pop();
    }

    private void prepareRenderGizmos(Vector3f viewPosition)
    {
        for (GizmoInstance gizmo : Gizmos.getGizmosForRender())
        {
            if (gizmo.isAlwaysOnTop())
            {
                drawableGizmoPrimitivesAlwaysOnTop.createPrimitives(gizmo);
            } else
            {
                drawableGizmoPrimitives.createPrimitives(gizmo);
            }
        }

        drawableGizmoPrimitivesAlwaysOnTop.sortPrimitives(viewPosition, partialTicks);
        drawableGizmoPrimitives.sortPrimitives(viewPosition, partialTicks);
    }

    public enum PartialTicksType
    {
        PREDICT_NEXT_TICK, LERP_BETWEEN_SNAPSHOTS
    }

    public static PartialTicksType partialTicksType = PartialTicksType.LERP_BETWEEN_SNAPSHOTS;

    private float computePartialTicks(WorldRenderState state)
    {
        float alpha;
        if (partialTicksType == PartialTicksType.PREDICT_NEXT_TICK)
        {
            long now = System.nanoTime();
            long tickTime = state.lastSnapshotTimeNano;

            float elapsed = (now - tickTime);
            alpha = elapsed / (1e9f / Constants.TICKS_IN_SECOND);
        } else
        {
            long previousTickTime = state.lastSnapshot.snapshotTimeNano;
            long currentTickTime  = state.currentSnapshot.snapshotTimeNano;
            long now = System.nanoTime();
            alpha = (now - currentTickTime) / (float) (currentTickTime - previousTickTime);
        }

        return Math.clamp(alpha, 0f, 1f);
    }

    private void frameTick()
    {
        Profiler profiler = FlareProfiler.frame();
        profiler.push("network tick");
        // TODO: move to game thread 100%
        if (networkMain != null)
            networkMain.tick();
        profiler.popPush("keybinds processing");

        if (Keybinds.ESCAPE.isActive()) processEscape();
        if (Keybinds.CHAT.isActive() && !(MDUtil.isPanelOpen(Constants.UI.IN_GAME_MENU) && MDUtil.isPanelOpen(Constants.UI.SETTINGS))) processChat();

        if (Keybinds.ENTER.isActive() || Keybinds.ENTER_KP.isActive() && MDUtil.isPanelOpen(Constants.UI.IN_GAME_CHAT))
        {
            MDUtil.getPanel(Constants.UI.IN_GAME_CHAT).ifPresent(chat -> {
                chat.getComponent(ViewController.class).ifPresent(controller -> {
                    controller.panelView().sendCommand(new Command(Constants.key("execute_command")));
                });
            });
        }
        profiler.pop();
    }

    private void processEscape()
    {
        if (client.getWorld() == null)
        {
            setMouseGrab(false);
            return;
        }

        if (MDUtil.isPanelOpen(Constants.UI.IN_GAME_ECS_PROFILER))
        {
            setMouseGrab(!isMouseGrabbed);
        } else if (MDUtil.isPanelOpen(Constants.UI.IN_GAME_MENU))
        {
            // If open, close
            MDUtil.removePanel(Constants.UI.IN_GAME_MENU);
            setMouseGrab(true);
        } else if (MDUtil.isPanelOpen(Constants.UI.IN_GAME_CHAT))
        {
            MDUtil.removePanel(Constants.UI.IN_GAME_CHAT);
            setMouseGrab(true);
        } else if (MDUtil.isPanelOpen(Constants.UI.LOBBY_MENU_DEDICATED))
        {
            MDUtil.removePanel(Constants.UI.LOBBY_MENU_DEDICATED);
            MDUtil.addPanel(Constants.UI.IN_GAME_MENU);
        } else
        {
            if (MDUtil.isPanelOpen(Constants.UI.SETTINGS))
            {
                MDUtil.removePanel(Constants.UI.SETTINGS);
            }

            MDUtil.addPanel(Constants.UI.IN_GAME_MENU);
            setMouseGrab(false);
        }
    }

    private void processChat()
    {
        if (!MDUtil.isPanelOpen(Constants.UI.IN_GAME_CHAT))
        {
            Panel panel = MDUtil.addPanel(Constants.UI.IN_GAME_CHAT);
            Optional<Widget> child = panel.getChild("chat_field");
            child.ifPresent(w -> MoonDust.getInstance().focus(w));
            setMouseGrab(false);
        }
    }

    public void swapNetworkBackend(Settings.MultiplayerBackend backend)
    {
        if (backend == Settings.MultiplayerBackend.DEDICATED)
            networkMain = new DedicatedMain();
        else
            throw new IllegalStateException("Steam backend not implemented yet!");

        networkMain.setup();
    }

    public void clearWorld()
    {
        if (client.getWorld() != null)
        {
            client.getWorld().cleanup();
            client.setWorld(null);
        }
        setMouseGrab(false);
    }

    public void setCurrentWorld(World world)
    {
        if (world == null)
        {
            throw new IllegalStateException("Tried to navigate to null world, use clearWorld if you wish to clear the world instead.");
        }

        world.init(masterRenderer());
        this.client.setWorld(world);
        setMouseGrab(true);
    }

    public boolean isMouseGrabbed()
    {
        return isMouseGrabbed;
    }

    public void setMouseGrab(boolean isMouseGrabbed)
    {
        GLFW.glfwSetInputMode(window().window(), GLFW.GLFW_CURSOR, isMouseGrabbed ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
        if (!isMouseGrabbed && this.isMouseGrabbed)
        {
            GLFW.glfwSetCursorPos(window().window(), window().getWidth() / 2d, window().getHeight() / 2d);
        } else
        {
            Vector2i mousePos = input().getMousePositionRelativeToTopLeftOfTheWindow();
            camera().oldx = mousePos.x;
            camera().oldy = mousePos.y;
        }
        this.isMouseGrabbed = isMouseGrabbed;
    }

    public Client getClient()
    {
        return client;
    }

    public Commands getCommands()
    {
        return commands;
    }

    public NetworkMain getNetwork()
    {
        return networkMain;
    }

    public static OrbiterApp getInstance()
    {
        return instance;
    }

    @Override
    public void saveSettings()
    {
        SettingsLoader.saveToJsonFile(Registries.SETTINGS, Constants.SETTINGS);
        SettingsLoader.saveToJsonFile(MoonDustRegistries.SETTINGS, MoonDustConstants.SETTINGS_FILE);
    }

    @Override
    public void cleanup()
    {
        if (networkMain != null)
            networkMain.shutdown();
        clearWorld();
        client.getSoundMaster().cleanup();
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
