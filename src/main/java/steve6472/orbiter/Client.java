package steve6472.orbiter;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import steve6472.core.log.Log;
import steve6472.flare.Camera;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.input.UserInput;
import steve6472.flare.tracy.FlareProfiler;
import steve6472.flare.tracy.Profiler;
import steve6472.flare.vr.VrInput;
import steve6472.orbiter.audio.SoundMaster;
import steve6472.orbiter.player.PCPlayer;
import steve6472.orbiter.player.Player;
import steve6472.orbiter.rendering.snapshot.SnapshotPools;
import steve6472.orbiter.rendering.snapshot.WorldRenderState;
import steve6472.orbiter.rendering.snapshot.WorldSnapshot;
import steve6472.orbiter.util.PhysicsRayTrace;
import steve6472.orbiter.world.World;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class Client
{
    private static final Logger LOGGER = Log.getLogger(Client.class);
    private final UUID clientUUID;
    private final PhysicsRayTrace rayTrace;
    private final SoundMaster soundMaster;
    private Player player;
    private Camera camera;
    private World world;

    private final SnapshotPools pools = new SnapshotPools();
    public AtomicReference<WorldRenderState> worldRenderState = new AtomicReference<>();
    private WorldSnapshot freeableSnapshot;

    public Client()
    {
        clientUUID = UUID.randomUUID();
        rayTrace = new PhysicsRayTrace(this);
        LOGGER.info("Client UUID: " + clientUUID);
        soundMaster = new SoundMaster();
        soundMaster.setup();
    }

    public void handleInput(UserInput userInput, VrInput vrInput, float frameTime)
    {
        Profiler profiler = FlareProfiler.frame();
        profiler.push("player input");
        if (world != null && player != null)
            player.handleInput(userInput, vrInput, camera, frameTime);

        profiler.popPush("soundmaster");
        soundMaster.setListenerOrientation(camera.getViewMatrix());
        Vector3f eyePos = camera.viewPosition;
        soundMaster.setListenerPosition(eyePos.x, eyePos.y, eyePos.z);
        profiler.pop();
    }

    public void render(FrameInfo frameInfo, MemoryStack memoryStack)
    {
        if (world != null)
        {
            world.debugRender(frameInfo.frameTime());
        }
    }

    public void setCamera(Camera camera)
    {
        this.camera = camera;
    }

    public void setWorld(World world)
    {
        this.world = world;
        if (world != null)
        {
            this.player = new PCPlayer(clientUUID, this);
            world.ecsEngine().addEntity(player.ecsEntity());
            world.startTicking();
        } else
        {
            this.player = null;
            WorldRenderState renderState = worldRenderState.get();
            if (renderState != null)
            {
                renderState.lastSnapshot.free(pools);
                renderState.currentSnapshot.free(pools);
                worldRenderState.set(null);
            }
        }
    }

    public Camera getCamera()
    {
        return camera;
    }

    public Player player()
    {
        return player;
    }

    public World getWorld()
    {
        return world;
    }

    public UUID getClientUUID()
    {
        return clientUUID;
    }

    public PhysicsRayTrace getRayTrace()
    {
        return rayTrace;
    }

    public SoundMaster getSoundMaster()
    {
        return soundMaster;
    }

    public void snapshotWorldState()
    {
        if (freeableSnapshot != null)
        {
            freeableSnapshot.free(pools);
            freeableSnapshot = null;
        }

        if (world == null)
            return;

        WorldRenderState previousRenderState = worldRenderState.get();

        WorldSnapshot previousSnapshot;
        WorldSnapshot currentSnapshot = world.createSnapshot(pools, clientUUID);
        currentSnapshot.snapshotTimeNano = System.nanoTime();
        currentSnapshot.cameraPosition.set(player.getEyePos());

        // First frame of the world - use current snapshot as previous as well.
        if (previousRenderState == null)
        {
            previousSnapshot = currentSnapshot;
        } else
        {
            // Subsequent frames of the world - use previous snapshot
            freeableSnapshot = previousRenderState.lastSnapshot;
            previousSnapshot = previousRenderState.currentSnapshot;
        }

        WorldRenderState renderState = new WorldRenderState(previousSnapshot, currentSnapshot);
        renderState.lastSnapshotTimeNano = currentSnapshot.snapshotTimeNano;
        worldRenderState.set(renderState);
    }
}
