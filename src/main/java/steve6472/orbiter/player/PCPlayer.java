package steve6472.orbiter.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.github.stephengold.joltjni.*;
import com.github.stephengold.joltjni.Character;
import com.github.stephengold.joltjni.enumerate.*;
import com.github.stephengold.joltjni.readonly.ConstShapeSettings;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import steve6472.core.registry.Key;
import steve6472.core.util.MathUtil;
import steve6472.flare.Camera;
import steve6472.flare.input.UserInput;
import steve6472.flare.render.debug.DebugRender;
import steve6472.flare.vr.VrInput;
import steve6472.moondust.widget.component.IBounds;
import steve6472.orbiter.*;
import steve6472.orbiter.settings.Keybinds;
import steve6472.orbiter.settings.Settings;
import steve6472.orbiter.ui.GlobalProperties;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.physics.Collision;
import steve6472.orbiter.world.ecs.components.physics.PCCharacter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static steve6472.orbiter.Convert.jomlToPhys;
import static steve6472.orbiter.Convert.physGetToJoml;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class PCPlayer implements Player
{
    public static final Key CLIENT_PLAYER_BLUEPRINT = Constants.key("client_player");

    public static final float RADIUS = 0.5f;
    public static final float HEIGHT = 1.6f;
    public static final float EYE_HEIGHT = 1.5f;
    public static final float STEP_HEIGHT = 0.4f;
    public static final float PENETRATION_CONSTANT = 0.19f;
    public static final int JUMP_COOLDOWN = 10;

    private final Client client;
    public final Character character;
    public final Entity ecsEntity;
    private float jumpCooldown = 0;

    public static float REACH = 2;
    public static float STRENGTH = 1;

    public PCPlayer(UUID uuid, Client client)
    {
        this.client = client;
        ecsEntity = new Entity();
        List<Component> components = Registries.ENTITY_BLUEPRINT.get(CLIENT_PLAYER_BLUEPRINT).createEntityComponents(uuid);
        for (Component component : components)
        {
            ecsEntity.add(component);
        }
        ecsEntity.add(new PCCharacter());

        Collision collision = Components.COLLISION.get(ecsEntity);
        if (collision == null)
            throw new RuntimeException("Player Entity is missing collision!");

        if (!(collision.shape() instanceof ConvexShape convexCollisionShape))
            throw new RuntimeException("Player capsule collision is not convex!");

        CharacterSettings settings = new CharacterSettings();
        settings.setShape(convexCollisionShape);
        settings.setLayer(Constants.Physics.OBJ_LAYER_MOVING);

        PhysicsSystem physics = client.getWorld().physics();
        character = new Character(settings, new RVec3(0, 1, 0), new Quat(), Constants.PhysicsFlags.CLIENT_PLAYER, physics);
        character.addToPhysicsSystem();
    }

    @Override
    public void teleport(Vector3f position)
    {
        character.setPosition(jomlToPhys(position).toRVec3());
    }

    @Override
    public void applyMotion(Vector3f motion)
    {
//        character.addLinearVelocity(jomlToPhys(motion));
    }

    @Override
    public Entity ecsEntity()
    {
        return ecsEntity;
    }

    @Override
    public Vector3f getFeetPos()
    {
        return getCenterPos().sub(0, HEIGHT / 2f, 0);
    }

    @Override
    public Vector3f getEyePos()
    {
        return getFeetPos().add(0, GlobalProperties.EYE_EIGHT.get().floatValue(), 0);
    }

    @Override
    public Vector3f getCenterPos()
    {
        Vector3f vector3f = physGetToJoml(_ -> character.getPosition().toVec3());
        // compensate for.. something I guess
//        vector3f.add(0, 0.04f, 0);
        return vector3f;
    }

    @Override
    public void handleInput(UserInput userInput, VrInput vrInput, Camera camera, float frameTime)
    {
        processMovementAndCamera(userInput, camera);

        Vector3f direction = MathUtil.yawPitchToVector(camera.yaw() + (float) (Math.PI * 0.5f), camera.pitch());

        Optional<RayCastResult> physicsRayTestResult = client.getRayTrace().rayTraceGetFirst(getEyePos(), direction, REACH, true);
        physicsRayTestResult.ifPresent(res ->
        {
            Vector3f hitPosition = new Vector3f(getEyePos()).add(new Vector3f(direction).mul(res.getFraction() * REACH));

            DebugRender.addDebugObjectForFrame(
                DebugRender.lineSphere(0.015f, 4, DebugRender.IVORY),
                new Matrix4f().translate(hitPosition));
        });
    }

    private void processMovementAndCamera(UserInput userInput, Camera camera)
    {
//        character.setWalkDirection(jomlToPhys(new Vector3f()));

        double speed = 1;

        if (Keybinds.SPRINT.isActive())
        {
            speed *= 2.5d;
        }

        double x = 0;
        double z = 0;

        if (character.isSupported())
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

        Vec3 linearVelocity = character.getLinearVelocity();
        linearVelocity.setX((float) x);
        linearVelocity.setZ((float) z);
        character.setLinearVelocity(linearVelocity);

        if (Keybinds.JUMP.isActive() && character.isSupported() && jumpCooldown == 0)
        {
            character.addLinearVelocity(new Vec3(0, 3, 0));
            jumpCooldown = JUMP_COOLDOWN;
        }

        Vector2i mousePos = userInput.getMousePositionRelativeToTopLeftOfTheWindow();
        Vector3f eyePos = getEyePos();
        camera.viewPosition.set(eyePos.x, eyePos.y, eyePos.z);
        camera.head(mousePos.x, mousePos.y, Settings.SENSITIVITY.get());
        camera.updateViewMatrix();

        RVec3 position = character.getPosition();
        if (position.y() < -10)
        {
            position.setY(2);
            character.setPosition(position);
            character.setLinearVelocity(new Vec3(0, 0, 0));
        }

        //        applyMotion(new Vector3f((float) x, 0, (float) z));
    }
}
