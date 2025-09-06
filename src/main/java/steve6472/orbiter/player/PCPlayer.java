package steve6472.orbiter.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.ConvexShape;
import com.jme3.bullet.joints.SixDofSpringJoint;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Matrix3f;
import com.jme3.math.Transform;
import jme3utilities.math.MyMath;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import steve6472.core.registry.Key;
import steve6472.core.util.MathUtil;
import steve6472.flare.Camera;
import steve6472.flare.input.UserInput;
import steve6472.flare.render.debug.DebugRender;
import steve6472.flare.vr.VrInput;
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
    public static final int JUMP_COOLDOWN = 4;

    private final Client client;
    public final PhysicsCharacter character;
    public final Entity ecsEntity;
    private float jumpCooldown = 0;

    public static float REACH = 2;
    public static float STRENGTH = 1;

    private float holdPointDistance = REACH;
    private final PhysicsRigidBody holdPoint;
    private SixDofSpringJoint holdJoint;

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

        character = new PhysicsCharacter(convexCollisionShape, STEP_HEIGHT);
        character.warp(jomlToPhys(new Vector3f(0, 1, 0)));
        character.setUserIndex(Constants.CLIENT_PLAYER_MAGIC_CONSTANT);

        character.setJumpSpeed(7f);
        character.setMaxPenetrationDepth(PENETRATION_CONSTANT);

        holdPoint = createHoldPoint();
        OrbiterApp.getInstance().getClient().getWorld().physics().add(holdPoint);
    }

    private PhysicsRigidBody createHoldPoint()
    {
        PhysicsRigidBody body = new PhysicsRigidBody(new CompoundCollisionShape(1));
        body.setContactResponse(false);
        body.setProtectGravity(true);
        body.setGravity(new com.jme3.math.Vector3f(0, 0, 0));
        body.setLinearDamping(1);
        body.setAngularDamping(1);
        body.setMass(STRENGTH);
        body.setUserIndex2(~Constants.PhysicsFlags.NEVER_DEBUG_RENDER);
        return body;
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
        Vector3f vector3f = physGetToJoml(character::getPhysicsLocation);
        // compensate for.. something I guess
        vector3f.add(0, 0.04f, 0);
        return vector3f;
    }

    @Override
    public void handleInput(UserInput userInput, VrInput vrInput, Camera camera, float frameTime)
    {
        processMovementAndCamera(userInput, camera);

        if (userInput.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT))
        {
            startHolding(camera);
        } else
        {
            endHolding();
        }

        Vector3f direction = MathUtil.yawPitchToVector(camera.yaw() + (float) (Math.PI * 0.5f), camera.pitch());
        updateHoldPoint(camera, direction);

        Optional<PhysicsRayTestResult> physicsRayTestResult = client.getRayTrace().rayTraceGetFirst(getEyePos(), direction, REACH, true);
        physicsRayTestResult.ifPresent(res ->
        {
            Vector3f hitPosition = new Vector3f(getEyePos()).add(new Vector3f(direction).mul(res.getHitFraction() * REACH));

            DebugRender.addDebugObjectForFrame(
                DebugRender.lineSphere(0.015f, 4, DebugRender.IVORY),
                new Matrix4f().translate(hitPosition));
        });
    }

    private void processMovementAndCamera(UserInput userInput, Camera camera)
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

    private void updateHoldPoint(Camera camera, Vector3f direction)
    {
        holdPoint.setPhysicsLocation(Convert.jomlToPhys(camera.viewPosition).add(Convert.jomlToPhys(direction).mult(holdPointDistance)));

        if (holdJoint == null)
            return;

        com.jme3.math.Vector3f pivotA = holdJoint.getPivotA(null);
        MyMath.transform(holdJoint.getBodyA().getTransform(null), pivotA, pivotA);
        if (Convert.jomlToPhys(camera.viewPosition).distance(pivotA) > REACH)
            endHolding();
    }

    private void startHolding(Camera camera)
    {
        // Already holding
        if (holdJoint != null)
            return;

        Optional<PhysicsRayTestResult> resultOpt = client.getRayTrace().rayTraceGetFirst(camera, REACH, true);
        resultOpt.ifPresent(result ->
        {
            PhysicsCollisionObject collisionObject = result.getCollisionObject();
            if (!(collisionObject instanceof PhysicsRigidBody rigidBody))
                return;
            if (collisionObject == holdPoint)
                return;

            collisionObject.activate(true);
            Transform invertTransform = collisionObject.getTransform(new Transform()).invert();
            Vector3f direction = MathUtil.yawPitchToVector(camera.yaw() + (float) (Math.PI * 0.5f), camera.pitch());
            float hitFraction = result.getHitFraction();
            Vector3f hitPosition = new Vector3f(direction).mul(hitFraction * REACH).add(camera.viewPosition);

            com.jme3.math.Vector3f pivot = MyMath.transform(invertTransform, new com.jme3.math.Vector3f(hitPosition.x, hitPosition.y, hitPosition.z), null);

            holdJoint = new SixDofSpringJoint(rigidBody, holdPoint, pivot, new com.jme3.math.Vector3f(0, 0, 0), rigidBody.getPhysicsRotationMatrix(null), new Matrix3f(), false);
            client.getWorld().physics().add(holdJoint);
            holdPointDistance = hitFraction * holdPointDistance;
        });
    }

    private void endHolding()
    {
        // Already not holding
        if (holdJoint == null)
            return;

        holdJoint.destroy();
        client.getWorld().physics().remove(holdJoint);
        holdJoint = null;
        holdPointDistance = REACH;
    }
}
