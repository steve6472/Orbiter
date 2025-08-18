package steve6472.orbiter;

import org.lwjgl.system.MemoryStack;
import steve6472.flare.Camera;
import steve6472.flare.core.FrameInfo;
import steve6472.flare.input.UserInput;
import steve6472.flare.vr.VrData;
import steve6472.flare.vr.VrInput;
import steve6472.orbiter.player.PCPlayer;
import steve6472.orbiter.player.Player;
import steve6472.orbiter.player.VRPlayer;
import steve6472.orbiter.world.World;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class Client
{
    private Player player;
    private Camera camera;
    private World world;

    public Client()
    {
    }

    public void handleInput(UserInput userInput, VrInput vrInput, float frameTime)
    {
        if (world != null && player != null)
            player.handleInput(userInput, vrInput, camera, frameTime);
    }

    public void render(FrameInfo frameInfo, MemoryStack memoryStack)
    {
        if (world != null)
            world.debugRender();
    }

    public void setCamera(Camera camera)
    {
        this.camera = camera;
    }

    public void setWorld(World world)
    {
        this.world = world;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public Player player()
    {
        return player;
    }

    public World getWorld()
    {
        return world;
    }

    public void tickClient()
    {
        if (world != null)
            world.tick();
    }
}
