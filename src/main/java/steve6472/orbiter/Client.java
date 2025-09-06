package steve6472.orbiter;

import org.lwjgl.system.MemoryStack;
import steve6472.core.log.Log;
import steve6472.flare.Camera;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.input.UserInput;
import steve6472.flare.vr.VrInput;
import steve6472.orbiter.player.PCPlayer;
import steve6472.orbiter.player.Player;
import steve6472.orbiter.util.PhysicsRayTrace;
import steve6472.orbiter.world.World;

import java.util.UUID;
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
    private Player player;
    private Camera camera;
    private World world;

    public Client()
    {
        clientUUID = UUID.randomUUID();
        rayTrace = new PhysicsRayTrace(this);
        LOGGER.info("Client UUID: " + clientUUID);
    }

    public void handleInput(UserInput userInput, VrInput vrInput, float frameTime)
    {
        if (world != null && player != null)
            player.handleInput(userInput, vrInput, camera, frameTime);
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
        } else
        {
            this.player = null;
        }
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

    public void tickClient(float frameTime)
    {
        if (world != null)
        {
            world.tick(frameTime);
        }
    }
}
