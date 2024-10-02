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
public interface Player
{
    /*
     * Player manipulation
     */

    void teleport(Vector3f position);
    void applyMotion(Vector3f motion);

    /*
     * Getters
     */
    Vector3f getFeetPos();
    Vector3f getEyePos();

    void handleInput(UserInput userInput, VrInput vrInput, Camera camera, float frameTime);
}
