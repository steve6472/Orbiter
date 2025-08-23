package steve6472.orbiter.player;

import com.badlogic.ashley.core.Entity;
import org.joml.Vector3f;
import steve6472.flare.Camera;
import steve6472.flare.input.UserInput;
import steve6472.flare.vr.VrInput;

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
    Entity ecsEntity();

    Vector3f getFeetPos();
    Vector3f getEyePos();
    Vector3f getCenterPos();

    void handleInput(UserInput userInput, VrInput vrInput, Camera camera, float frameTime);
}
