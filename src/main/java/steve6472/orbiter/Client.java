package steve6472.orbiter;

import steve6472.orbiter.player.PCPlayer;
import steve6472.orbiter.player.Player;
import steve6472.orbiter.player.VRPlayer;
import steve6472.volkaniums.Camera;
import steve6472.volkaniums.input.UserInput;
import steve6472.volkaniums.vr.VrData;
import steve6472.volkaniums.vr.VrInput;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class Client
{
    private final Player player;
    private final Camera camera;

    public Client(Camera camera)
    {
        this.camera = camera;
        player = VrData.VR_ON ? new VRPlayer() : new PCPlayer();
    }

    public void handleInput(UserInput userInput, VrInput vrInput, float frameTime)
    {
        player.handleInput(userInput, vrInput, camera, frameTime);
    }

    public Player player()
    {
        return player;
    }

    public void tickClient()
    {
    }
}
