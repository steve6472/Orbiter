package steve6472.orbiter.player;

import org.joml.Vector3f;
import steve6472.volkaniums.Camera;
import steve6472.volkaniums.input.UserInput;
import steve6472.volkaniums.vr.VrInput;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class VRPlayer implements Player
{
    @Override
    public void teleport(Vector3f position)
    {

    }

    @Override
    public void applyMotion(Vector3f motion)
    {

    }

    @Override
    public Vector3f getFeetPos()
    {
        return null;
    }

    @Override
    public Vector3f getEyePos()
    {
        return null;
    }

    @Override
    public void handleInput(UserInput userInput, VrInput vrInput, Camera camera, float frameTime)
    {

    }
}
