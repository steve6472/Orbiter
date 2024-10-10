package steve6472.orbiter.player;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import steve6472.volkaniums.Camera;
import steve6472.volkaniums.input.UserInput;
import steve6472.volkaniums.vr.DeviceType;
import steve6472.volkaniums.vr.VrInput;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class VRPlayer implements Player
{
    private Vector3f eyePos = new Vector3f();

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
        return eyePos.sub(0, 1f, 0, new Vector3f());
    }

    @Override
    public Vector3f getEyePos()
    {
        return eyePos;
    }

    @Override
    public Vector3f getCenterPos()
    {
        return eyePos.sub(0, 0.5f, 0, new Vector3f());
    }

    @Override
    public void handleInput(UserInput userInput, VrInput vrInput, Camera camera, float frameTime)
    {
        vrInput.getPoses().stream().filter(a -> a.getFirst() == DeviceType.HMD).findFirst().ifPresent(pair -> {
            Matrix4f transform = pair.getSecond();
            eyePos = transform.transformPosition(new Vector3f());
        });
    }
}
