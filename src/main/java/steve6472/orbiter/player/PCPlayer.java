package steve6472.orbiter.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.github.stephengold.joltjni.*;
import org.joml.Vector2i;
import org.joml.Vector3f;
import steve6472.core.registry.Key;
import steve6472.flare.Camera;
import steve6472.flare.input.UserInput;
import steve6472.flare.tracy.FlareProfiler;
import steve6472.flare.tracy.Profiler;
import steve6472.flare.vr.VrInput;
import steve6472.orbiter.*;
import steve6472.orbiter.settings.Keybinds;
import steve6472.orbiter.settings.Settings;
import steve6472.orbiter.ui.GlobalProperties;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.physics.Collision;
import steve6472.orbiter.world.ecs.components.physics.PCCharacter;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public static final int JUMP_COOLDOWN = 3;

    private final Client client;
    public final ExtendedUpdateSettings updateSettings;
    public final CharacterVirtual character;
    public final BodyFilter allBodies;
    public final ShapeFilter allShapes;
    public final Entity ecsEntity;
    private float jumpCooldown = 0;

    public static float REACH = 2;
    public static float STRENGTH = 1;
    private final Map<String, Runnable> PATTERNS = new HashMap<>();

    public Vector3f gravity = new Vector3f(Constants.GRAVITY);
    public float jumpStrength = 3;
    public float groundFriction = 0.8f;
    public float airFriction = 0.85f;

    public PCPlayer(UUID uuid, Client client)
    {
        this.client = client;
        ecsEntity = new Entity();
        List<Component> components = Registries.ENTITY_BLUEPRINT.get(CLIENT_PLAYER_BLUEPRINT).createEntityComponents(ecsEntity, uuid);
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

        CharacterVirtualSettings settings = new CharacterVirtualSettings();
        settings.setShape(convexCollisionShape);
        settings.setSupportingVolume(new Plane(Vec3.sAxisY(), 0.7f));
        settings.setMaxStrength(500);

        PhysicsSystem physics = client.getWorld().physics();
        character = new CharacterVirtual(settings, new RVec3(0, 1, 0), new Quat(), Constants.PhysicsFlags.CLIENT_PLAYER, physics);

        allBodies = new BodyFilter();
        allShapes = new ShapeFilter();

        updateSettings = new ExtendedUpdateSettings();
        updateSettings.setStickToFloorStepDown(Vec3.sZero());
        updateSettings.setWalkStairsStepUp(Vec3.sZero());


        PATTERNS.put("lel", () -> teleport(new Vector3f(0, 4, 0)));
        PATTERNS.put("lllll", () ->
        {
            EntityBlueprint blueprint = Registries.ENTITY_BLUEPRINT.get(Constants.key("magic_tower_new"));
            client.getWorld().addEntity(blueprint, UUID.randomUUID(), Map.of(), true);
        });
        PATTERNS.put("elefiri", () ->
        {
            OrbiterApp.getInstance().window().closeWindow();
        });
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
    public void handleInput(UserInput userInput, VrInput vrInput, Camera camera, float frameTime, boolean isMouseGrabbed)
    {
        Profiler profiler = FlareProfiler.frame();
        profiler.push("camera loook");
        processCameraLook(userInput, camera, isMouseGrabbed);
        profiler.pop();
    }

    private void processCameraLook(UserInput userInput, Camera camera, boolean isMouseGrabbed)
    {
        Vector2i mousePos = userInput.getMousePositionRelativeToTopLeftOfTheWindow();
        if (isMouseGrabbed)
            camera.head(mousePos.x, mousePos.y, Settings.SENSITIVITY.get());
        camera.updateViewMatrix();
    }

    public void worldTick(Camera camera, float timePerStep)
    {
        boolean isMouseGrabbed = OrbiterApp.getInstance().isMouseGrabbed();
        double speed = 25;

        if (Keybinds.SPRINT.isActive())
        {
            speed *= 1.75d;
        }

        double x = 0;
        double z = 0;

        jumpCooldown = Math.max(--jumpCooldown, 0);

        if (isMouseGrabbed && Keybinds.FORWARD.isActive())
        {
            x += Math.sin(camera.yaw()) * -speed;
            z += Math.cos(camera.yaw()) * -speed;
        }

        if (isMouseGrabbed && Keybinds.BACKWARD.isActive())
        {
            x += Math.sin(camera.yaw()) * speed;
            z += Math.cos(camera.yaw()) * speed;
        }

        if (isMouseGrabbed && Keybinds.LEFT.isActive())
        {
            x += Math.sin(camera.yaw() + Math.PI / 2.0) * -speed;
            z += Math.cos(camera.yaw() + Math.PI / 2.0) * -speed;
        }

        if (isMouseGrabbed && Keybinds.RIGHT.isActive())
        {
            x += Math.sin(camera.yaw() + Math.PI / 2.0) * speed;
            z += Math.cos(camera.yaw() + Math.PI / 2.0) * speed;
        }

        Vector3f linearVelocity = Convert.physToJoml(character.getLinearVelocity());
        linearVelocity.add((float) x * timePerStep, 0, (float) z * timePerStep);

        if (character.isSupported())
        {
            linearVelocity.mul(groundFriction, 1, groundFriction);
            // set Y to 0
            linearVelocity.setComponent(1, 0);
            if (isMouseGrabbed && Keybinds.JUMP.isActive() && jumpCooldown == 0)
            {
                linearVelocity.add(0, jumpStrength, 0);
                jumpCooldown = JUMP_COOLDOWN;
            }
        } else
        {
            linearVelocity.mul(airFriction, 1, airFriction);
        }

        linearVelocity.add(gravity.x * timePerStep, gravity.y * timePerStep, gravity.z * timePerStep);

        character.setLinearVelocity(Convert.jomlToPhys(linearVelocity));

        RVec3 position = character.getPosition();
        if (position.y() < -10)
        {
            position.setY(2);
            character.setPosition(position);
            character.setLinearVelocity(new Vec3(0, 0, 0));
        }
    }

    public void castHex(String pattern)
    {
        Runnable runnable = PATTERNS.get(pattern);
        if (runnable != null)
            runnable.run();
        else
            System.err.println("Unknown pattern " + pattern);
    }
}
