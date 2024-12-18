package steve6472.orbiter.player;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.ConvexShape;
import com.jme3.bullet.objects.PhysicsCharacter;
import org.joml.Vector2i;
import org.joml.Vector3f;
import steve6472.core.registry.Key;
import steve6472.flare.Camera;
import steve6472.flare.input.UserInput;
import steve6472.flare.vr.VrInput;
import steve6472.orbiter.Convert;
import steve6472.orbiter.Registries;
import steve6472.orbiter.settings.Keybinds;
import steve6472.orbiter.settings.Settings;

import static steve6472.orbiter.Convert.jomlToPhys;
import static steve6472.orbiter.Convert.physGetToJoml;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class PCPlayer implements Player
{
    public static final float RADIUS = 0.5f;
    public static final float HEIGHT = 1.6f;
    public static final float EYE_HEIGHT = 1.5f;
    public static final float STEP_HEIGHT = 0.4f;
    public static final float PENETRATION_CONSTANT = 0.19f;
    public static final int JUMP_COOLDOWN = 4;

    public final PhysicsCharacter character;
    private float jumpCooldown = 0;

    public PCPlayer()
    {
        CollisionShape shape = Registries.COLLISION
            .get(Key.defaultNamespace("blockbench/static/player_capsule"))
            .collisionShape();

        if (!(shape instanceof ConvexShape convex))
            throw new RuntimeException("Player capsule collision is not convex!");

        character = new PhysicsCharacter(convex, STEP_HEIGHT);
        character.warp(Convert.jomlToPhys(new Vector3f(0, 1, 0)));

        character.setJumpSpeed(7f);
        character.setMaxPenetrationDepth(0.2f);
    }

    @Override
    public void teleport(Vector3f position)
    {
        character.warp(jomlToPhys(position));
    }

    @Override
    public void applyMotion(Vector3f motion)
    {
        character.setWalkDirection(jomlToPhys(motion));
    }

    @Override
    public Vector3f getFeetPos()
    {
        return getCenterPos().sub(0, HEIGHT / 2f, 0);
    }

    @Override
    public Vector3f getEyePos()
    {
        return getFeetPos().add(0, EYE_HEIGHT, 0);
    }

    @Override
    public Vector3f getCenterPos()
    {
        return physGetToJoml(character::getPhysicsLocation).add(0, PENETRATION_CONSTANT, 0);
    }

    @Override
    public void handleInput(UserInput userInput, VrInput vrInput, Camera camera, float frameTime)
    {
        character.setWalkDirection(jomlToPhys(new Vector3f()));

        double speed = 0.025;

        if (Keybinds.SPRINT.isActive())
        {
            speed *= 2.5d;
        }

        double x = 0;
        double z = 0;

        jumpCooldown = Math.max(--jumpCooldown, 0);

        if (Keybinds.FORWARD.isActive())
        {
            x += Math.sin(camera.yaw()) * -speed;
            z += Math.cos(camera.yaw()) * -speed;
        }

        if (Keybinds.BACKWARD.isActive())
        {
            x += Math.sin(camera.yaw()) * speed;
            z += Math.cos(camera.yaw()) * speed;
        }

        if (Keybinds.LEFT.isActive())
        {
            x += Math.sin(camera.yaw() + Math.PI / 2.0) * -speed;
            z += Math.cos(camera.yaw() + Math.PI / 2.0) * -speed;
        }

        if (Keybinds.RIGHT.isActive())
        {
            x += Math.sin(camera.yaw() + Math.PI / 2.0) * speed;
            z += Math.cos(camera.yaw() + Math.PI / 2.0) * speed;
        }

        if (Keybinds.JUMP.isActive() && character.onGround() && jumpCooldown == 0)
        {
            character.jump();
            jumpCooldown = JUMP_COOLDOWN;
        }

        Vector2i mousePos = userInput.getMousePositionRelativeToTopLeftOfTheWindow();
        Vector3f eyePos = getEyePos();
        camera.viewPosition.set(eyePos.x, eyePos.y, eyePos.z);
        camera.head(mousePos.x, mousePos.y, Settings.SENSITIVITY.get());
        camera.updateViewMatrix();

        applyMotion(new Vector3f((float) x, 0, (float) z));
    }
}
