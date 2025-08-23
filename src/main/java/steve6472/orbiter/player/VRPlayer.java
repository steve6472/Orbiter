package steve6472.orbiter.player;

import com.badlogic.ashley.core.Entity;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.joints.PhysicsJoint;
import com.jme3.bullet.joints.SixDofSpringJoint;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Matrix3f;
import com.jme3.math.Transform;
import com.mojang.datafixers.util.Pair;
import jme3utilities.math.MyMath;
import org.joml.*;
import org.lwjgl.openvr.*;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.flare.Camera;
import steve6472.flare.input.UserInput;
import steve6472.flare.registry.FlareRegistries;
import steve6472.flare.render.debug.DebugRender;
import steve6472.flare.vr.DeviceType;
import steve6472.flare.vr.VrInput;
import steve6472.flare.vr.input.InputType;
import steve6472.flare.vr.input.VrAction;
import steve6472.flare.vr.input.VrActionSet;
import steve6472.orbiter.Client;
import steve6472.orbiter.Convert;
import steve6472.orbiter.world.EntityModify;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.components.physics.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class VRPlayer implements Player
{
    private static final Logger LOGGER = Log.getLogger(VRPlayer.class);
    private final Client client;

    private Vector3f eyePos = new Vector3f();

    private final VrActionSet actionSet;
    private final VrAction<InputAnalogActionData> leftTriggerPosition;
    private final VrAction<InputDigitalActionData> leftTriggerTouch;
    private final VrAction<InputDigitalActionData> leftTriggerClick;

    public VRPlayer(Client client)
    {
        this.client = client;

        actionSet = new VrActionSet("/actions/default");
        leftTriggerPosition = actionSet.addAction("/actions/default/in/TriggerPosition", InputType.ANALOG);
        leftTriggerTouch = actionSet.addAction("/actions/default/in/TriggerTouch", InputType.DIGITAL);
        leftTriggerClick = actionSet.addAction("/actions/default/in/TriggerClick", InputType.DIGITAL);
    }

    @Override
    public void teleport(Vector3f position)
    {

    }

    @Override
    public void applyMotion(Vector3f motion)
    {

    }

    @Override
    public Entity ecsEntity()
    {
        return null;
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

        handleHand(vrInput);

        actionSet.updateAll();
        handleTrigger(leftTriggerPosition.get().x(), vrInput);
    }

    private Entity handEntity;

    private Entity createHandEntity(float x, float y, float z)
    {
        /*Key model = Key.defaultNamespace("blockbench/static/controller");
        return client.getWorld().addEntity(
            FlareRegistries.STATIC_MODEL.get(model),
            new Collision(model),
            new Position(x, y, z),
            new Rotation(),
            new Mass(5),
            new Gravity(0, 0, 0),
            new AngularDamping(1),
            new LinearDamping(1),
            new LinearVelocity(),
            Tag.PHYSICS,
            Tag.CLIENT_HANDLED
            );*/
        return null;
    }

    private void handleHand(VrInput vrInput)
    {
        Optional<Pair<DeviceType, Matrix4f>> controller = vrInput
            .getPoses()
            .stream()
            .filter(a -> a.getFirst() == DeviceType.CONTROLLER)
            .findFirst();

        controller.ifPresentOrElse(pair -> {
            Matrix4f transform = pair.getSecond();
            Vector3f position = transform.transformPosition(new Vector3f());
            AxisAngle4f axisRot = transform.getRotation(new AxisAngle4f());
            Quaternionf rotation = new Quaternionf(axisRot);

            if (handEntity == null)
            {
                handEntity = createHandEntity(position.x, position.y, position.z);
                LOGGER.info("Created hand entity");
            }

            PhysicsRigidBody body = client.getWorld().bodyMap().get(Components.UUID.get(handEntity).uuid());
            body.activate(true);

            Position posComp = Components.POSITION.get(handEntity);
            posComp.set(position.x, position.y, position.z);
            posComp.modifyBody(body);
            EntityModify._markModified(handEntity, Position.class);

            Rotation rotComp = Components.ROTATION.get(handEntity);
            rotComp.set(rotation.x, rotation.y, rotation.z, rotation.w);
            rotComp.modifyBody(body);
            EntityModify._markModified(handEntity, Rotation.class);

//            if (OrbiterApp.getInstance().getSteam().connections != null)
//                NetworkSync.syncEntity(handEntity, OrbiterApp.getInstance().getSteam().connections);

        }, () -> {
            if (handEntity != null)
            {
                client.getWorld().removeEntity(Components.UUID.get(handEntity).uuid(), true);
                handEntity = null;
                LOGGER.info("Deleted hand entity");
            }
        });
    }

    boolean jointsExist = false;

    private void handleTrigger(float triggerValue, VrInput vrInput)
    {
        if (triggerValue < 0.8f)
            removeJoints();
        else
            createJoints(vrInput);
    }

    private void createJoints(VrInput vrInput)
    {
        if (jointsExist || handEntity == null)
            return;

        Optional<Pair<DeviceType, Matrix4f>> controller = vrInput
            .getPoses()
            .stream()
            .filter(a -> a.getFirst() == DeviceType.CONTROLLER)
            .findFirst();

        controller.ifPresent(pair ->
        {
            Matrix4f transform = pair.getSecond();
            Vector3f position = transform.transformPosition(new Vector3f());
            AxisAngle4f axisRot = transform.getRotation(new AxisAngle4f());
            Quaternionf rotation = new Quaternionf(axisRot);

            float reach = 1.5f;
            final float span = 0.025f;

            Vector3f[] offsets = new Vector3f[]
                {
                    new Vector3f(span, -span, -0.08f),
                    new Vector3f(-span, -span, -0.08f),
                    new Vector3f(span, span, -0.08f),
                    new Vector3f(-span, span, -0.08f)
                };

            PhysicsCollisionObject firstHit = null;

            for (Vector3f offset : offsets)
            {
                Vector3f jointPos = new Vector3f(position);

                jointPos.add(new Vector3f(offset).rotate(rotation));

                Vector3f direction = new Vector3f(0, 0, -1);
                direction.rotate(rotation);
                direction.mul(reach);
                Vector3f endPoint = new Vector3f(jointPos);
                endPoint.add(direction);

                if (jointPos.equals(endPoint))
                {
                    LOGGER.warning("Can not create joint, start and end ray pos are the same!");
                    continue;
                }
                DebugRender.addDebugObjectForMs(DebugRender.line(jointPos, endPoint, DebugRender.GOLD), 1);

                List<PhysicsRayTestResult> physicsRayTestResults = client.getWorld()
                    .physics()
                    .rayTest(Convert.jomlToPhys(jointPos), Convert.jomlToPhys(endPoint));

                if (physicsRayTestResults.isEmpty())
                    continue;

                PhysicsRayTestResult collision = physicsRayTestResults.getFirst();
                PhysicsCollisionObject collisionObject = collision.getCollisionObject();

                // You can only grab one object at a time
                if (firstHit == null)
                    firstHit = collisionObject;
                else
                    if (firstHit != collisionObject)
                        continue;

                if (!(collisionObject instanceof PhysicsRigidBody grabbedObject))
                    continue;

                // Don't grab static objects
                if (grabbedObject.getMass() <= 0)
                    continue;

                PhysicsRigidBody body = client.getWorld().bodyMap().get(Components.UUID.get(handEntity).uuid());

                // Don't grab itself
                if (body == grabbedObject)
                    continue;

//                PhysicsJoint joint = new Point2PointJoint(body, grabbedObject, Convert.jomlToPhys(offset), collision.getHitNormalLocal(null));

                Vector3f intersection = new Vector3f(jointPos).add(new Vector3f(direction).normalize().mul(collision.getHitFraction() * reach));
                DebugRender.addDebugObjectForS(DebugRender.lineCube(intersection, 0.01f, DebugRender.ROYAL_BLUE), 1);

                com.jme3.math.Vector3f pivot = MyMath.transform(grabbedObject
                    .getTransform(new Transform())
                    .invert(), Convert.jomlToPhys(intersection), new com.jme3.math.Vector3f());

                Matrix3f rotInA = grabbedObject.getPhysicsRotationMatrix(new Matrix3f());
                Matrix3f rotInB = body.getPhysicsRotationMatrix(new Matrix3f());
                PhysicsJoint joint = new SixDofSpringJoint(grabbedObject, body, pivot, Convert.jomlToPhys(offset), rotInA, rotInB, false);
                client.getWorld().physics().addJoint(joint);

//                PeerConnections<SteamPeer> connections = OrbiterApp.getInstance().getSteam().connections;
//                if (connections != null)
//                    connections.broadcastMessage(new AddJoint(
//                        ((UUID) grabbedObject.getUserObject()),
//                        ((UUID) body.getUserObject()),
//                        Convert.physToJoml(pivot),
//                        offset,
//                        Convert.physToJoml(rotInA, new org.joml.Matrix3f()),
//                        Convert.physToJoml(rotInB, new org.joml.Matrix3f())));

                jointsExist = true;
                DebugRender.addDebugObjectForS(DebugRender.line(jointPos, endPoint, DebugRender.RED), 3);
            }
        });
    }

    private void removeJoints()
    {
        if (!jointsExist || handEntity == null)
            return;

        PhysicsRigidBody body = client.getWorld().bodyMap().get(Components.UUID.get(handEntity).uuid());
        for (PhysicsJoint physicsJoint : body.listJoints())
        {
            client.getWorld().physics().removeJoint(physicsJoint);
            body.removeJoint(physicsJoint);
        }
        jointsExist = false;

//        PeerConnections<SteamPeer> connections = OrbiterApp.getInstance().getSteam().connections;
//        if (connections != null)
//            connections.broadcastMessage(new ClearJoints(((UUID) body.getUserObject())));
    }
}
