package steve6472.jolt;

import com.github.stephengold.joltjni.*;
import com.github.stephengold.joltjni.enumerate.EActivation;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import steve6472.flare.core.Flare;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.pipeline.Pipelines;
import steve6472.flare.render.*;
import steve6472.flare.util.PackerUtil;
import steve6472.moondust.*;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Convert;
import steve6472.orbiter.rendering.*;
import steve6472.orbiter.scheduler.Scheduler;
import steve6472.orbiter.world.JoltBodies;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 10/1/2024
 * Project: Orbiter <br>
 */
public class JoltApp extends SimpleApp
{
    private static JoltApp instance;

    public PhysicsDebugRenderer physicsDebugRenderer;
    BodyManagerDrawSettings settings;
    Phys phys;

    JoltApp()
    {
        if (instance != null)
            throw new RuntimeException("JoltApp already started");
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
    }

    @Override
    protected void createRenderSystems()
    {
        physicsDebugRenderer = new PhysicsDebugRenderer(masterRenderer());
        settings = new BodyManagerDrawSettings();
        addRenderSystem(new DebugLineRenderSystem(masterRenderer(), Pipelines.DEBUG_LINE));

//        addRenderSystem(new StaticModelRenderSystem(masterRenderer(), new StaticWorldRender(client), Pipelines.BLOCKBENCH_STATIC));

        new MoonDustCallbacks().init(window().callbacks(), input());
    }

    @Override
    public void postInit()
    {
        super.postInit();
        Flare.getModuleManager().clearPartsCache();
        phys = new Phys();
    }

    private float timeToNextTick = 0;

    boolean titleDraw = false;
    boolean doDebugDraw = true;

    @Override
    public void render(FrameInfo frameInfo, MemoryStack memoryStack)
    {
        super.render(frameInfo, memoryStack);

        float frameTime = frameInfo.frameTime();

        timeToNextTick -= frameTime;
        if (timeToNextTick <= 0)
        {
            //noinspection deprecation
            Scheduler.instance().tick();
            JoltBodies joltBodies = phys.joltBodies;
            BodyInterface bodyInterface = phys.physics.getBodyInterface();

            tickCamera(frameInfo.camera());

            // Tick
            phys.tick();

            if (SimpleApp.Keys.G.isActive())
            {
                for (int i = 0; i < 100; i++)
                {
                    phys.testBoxPos(frameInfo.camera().center);
                }
            }

            if (SimpleApp.Keys.F.isActive())
            {
                for (int i = 0; i < 20; i++)
                {
                    phys.testBox(frameInfo.camera().center);
                }
            }

            if (SimpleApp.Keys.L.isActive())
            {
                for (int i = 0; i < 100; i++)
                {
                    Scheduler.runTaskLater(() ->
                    {
                        for (int j = 0; j < 5; j++)
                        {
                            phys.testBox(frameInfo.camera().center);
                        }
                    }, i);
                }
            }

            if (Keys.TO_UP.isActive())
            {
                List<Vector3f> vector3fs = generatePositions(joltBodies.getAllBodies().size(), 0.5f, 1f);

                int in = 0;
                for (Body body : joltBodies.getAllBodies())
                {
                    bodyInterface.setPositionAndRotation(body.getId(), Convert.jomlToPhys(vector3fs.get(in)).toRVec3(), new Quat(), EActivation.Activate);
                    in++;
                }
            }

            if (Keys.TO_DOWN.isActive())
            {
                List<Vector3f> vector3fs = generatePositions(joltBodies.getAllBodies().size(), 0.25f, 1f);

                int in = 0;
                for (Body body : joltBodies.getAllBodies())
                {
                    bodyInterface.setPosition(body.getId(), Convert.jomlToPhys(vector3fs.get(in)).toRVec3(), EActivation.Activate);
                    in++;
                }
            }

            if (Keys.TO_LEFT.isActive())
            {
                titleDraw = !titleDraw;
            }

            if (Keys.TO_RIGHT.isActive())
            {
                doDebugDraw = !doDebugDraw;
            }

            for (Body body : joltBodies.getAllBodies())
            {
                bodyInterface.activateBody(body.getId());
                body.resetSleepTimer();
                bodyInterface.getPositionAndRotation(body.getId(), new RVec3(), new Quat());
            }

            timeToNextTick += 1f / Constants.TICKS_IN_SECOND;
        }

        // This one makes the thing crash & for now it renders the client shape in their face...
        if (doDebugDraw)
        {
            phys.physics.drawBodies(settings, physicsDebugRenderer);
            phys.physics.drawConstraints(physicsDebugRenderer);
            phys.physics.drawConstraintLimits(physicsDebugRenderer);
            phys.physics.drawConstraintReferenceFrame(physicsDebugRenderer);
        }
    }

    public static List<Vector3f> generatePositions(int N, float spacing, float yLevel) {
        List<Vector3f> positions = new ArrayList<>();

        // Number of objects per row/column to form a square grid
        int gridSize = (int) Math.ceil(Math.sqrt(N));

        for (int i = 0; i < N; i++) {
            int row = i / gridSize;
            int col = i % gridSize;

            float x = col * spacing;
            float z = row * spacing;

            positions.add(new Vector3f(x, yLevel, z));
        }

        return positions;
    }

    public static JoltApp getInstance()
    {
        return instance;
    }

    public static void main(String[] args) throws IOException, URISyntaxException
    {
        PackerUtil.PADDING = 0;
        PackerUtil.DUPLICATE_BORDER = false;

        System.setProperty("joml.format", "false");

        Flare.start(new JoltApp());
    }
}
