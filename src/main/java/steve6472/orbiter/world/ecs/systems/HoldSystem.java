package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.github.stephengold.joltjni.*;
import com.github.stephengold.joltjni.enumerate.*;
import org.joml.Vector3f;
import steve6472.core.util.MathUtil;
import steve6472.flare.Camera;
import steve6472.flare.tracy.FlareProfiler;
import steve6472.flare.tracy.Profiler;
import steve6472.orbiter.Client;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Convert;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.player.PCPlayer;
import steve6472.orbiter.settings.Keybinds;

import java.util.Optional;

/**
 * Created by steve6472
 * Date: 10/7/2025
 * Project: Orbiter <br>
 */
public class HoldSystem extends EntitySystem
{
    private final Client client;

    private Body holdPoint;
    private float holdPointDistance = PCPlayer.REACH;

    private Body heldBody;
    private TwoBodyConstraint holdConstraint;
    private final PreviousState previousState = new PreviousState();

    private static class PreviousState
    {
        boolean allowSleeping;
        float angularDamping;
        float maxLinearVelocity;
        float maxAngularVelocity;

        void storeState(Body body)
        {
            allowSleeping = body.getAllowSleeping();
            angularDamping = body.getMotionProperties().getAngularDamping();
            maxLinearVelocity = body.getMotionProperties().getMaxLinearVelocity();
            maxAngularVelocity = body.getMotionProperties().getMaxAngularVelocity();
        }

        void restoreState(Body body)
        {
            body.setAllowSleeping(allowSleeping);
            body.getMotionProperties().setAngularDamping(angularDamping);
            body.getMotionProperties().setMaxLinearVelocity(maxLinearVelocity);
            body.getMotionProperties().setMaxAngularVelocity(maxAngularVelocity);
        }
    }

    public HoldSystem()
    {
        client = OrbiterApp.getInstance().getClient();
    }

    private Body createHoldPoint()
    {
        ShapeSettings shape;
        shape = new EmptyShapeSettings();

        BodyCreationSettings bcs = new BodyCreationSettings(
            shape,
            new RVec3(),
            new Quat(),
            EMotionType.Dynamic,
            Constants.Physics.OBJ_LAYER_MOVING);
        bcs.setMotionType(EMotionType.Kinematic);
        bcs.getMassPropertiesOverride().setMass(0.1f);
        bcs.setOverrideMassProperties(EOverrideMassProperties.CalculateInertia);
        bcs.setAllowSleeping(false);

        BodyInterface bodyInterface = OrbiterApp.getInstance().getClient().getWorld().physics().getBodyInterface();
        Body holdPoint = bodyInterface.createBody(bcs);
        bodyInterface.addBody(holdPoint.getId(), EActivation.Activate);
        return holdPoint;
    }

    public void prePhysicsTickUpdate(float timePerStep)
    {
        Camera camera = OrbiterApp.getInstance().camera();
        Vector3f direction = MathUtil.yawPitchToVector(camera.yaw() + (float) (Math.PI * 0.5f), camera.pitch());
        updateHoldPoint(camera, direction, timePerStep);
    }

    private void updateHoldPoint(Camera camera, Vector3f direction, float timePerStep)
    {
        if (holdPoint == null)
            return;

        if (!isHolding())
        {
            Optional<RayCastResult> resultOpt = client.getRayTrace().rayTraceGetFirst(camera.viewPosition, direction, PCPlayer.REACH, true);
            resultOpt.ifPresent(result -> holdPointDistance = result.getFraction() * PCPlayer.REACH);
        }

        Vector3f holdPos = new Vector3f(direction).mul(holdPointDistance).add(camera.viewPosition);
        holdPoint.moveKinematic(Convert.jomlToPhys(holdPos).toRVec3(), new Quat(), timePerStep);
    }

    private void startHolding()
    {
        // Already holding
        if (isHolding())
            return;

        Camera camera = OrbiterApp.getInstance().camera();

        Vector3f direction = MathUtil.yawPitchToVector(camera.yaw() + (float) (Math.PI * 0.5f), camera.pitch());
        Optional<RayCastResult> resultOpt = client.getRayTrace().rayTraceGetFirst(camera.viewPosition, direction, PCPlayer.REACH, true);
        resultOpt.ifPresent(result ->
        {
            heldBody = client.getWorld().bodyMap().getBodyById(result.getBodyId());
            if (heldBody == null)
                return;

            if (heldBody.isSensor())
                return;

            previousState.storeState(heldBody);

            heldBody.setAllowSleeping(false);
            heldBody.getMotionProperties().setAngularDamping(0.4f);
            heldBody.getMotionProperties().setMaxLinearVelocity(10f);
            heldBody.getMotionProperties().setMaxAngularVelocity((float) Math.PI * 3f);

            SixDofConstraintSettings settings = new SixDofConstraintSettings();
            settings.setSpace(EConstraintSpace.WorldSpace);
            settings.makeFixedAxis(EAxis.TranslationX);
            settings.makeFixedAxis(EAxis.TranslationY);
            settings.makeFixedAxis(EAxis.TranslationZ);

            final float damping = 0.75f;
            final float frequency = 2f; // in Hertz

            SpringSettings xSpring
                = settings.getLimitsSpringSettings(EAxis.TranslationX);
            xSpring.setDamping(damping);
            xSpring.setFrequency(frequency);
            SpringSettings ySpring
                = settings.getLimitsSpringSettings(EAxis.TranslationY);
            ySpring.setDamping(damping);
            ySpring.setFrequency(frequency);
            SpringSettings zSpring
                = settings.getLimitsSpringSettings(EAxis.TranslationZ);
            zSpring.setDamping(damping);
            zSpring.setFrequency(frequency);

            Vector3f pos = new Vector3f(direction).mul(PCPlayer.REACH).mul(result.getFraction()).add(camera.viewPosition);

            settings.setPosition1(holdPoint.getPosition());
            settings.setPosition2(Convert.jomlToPhys(pos).toRVec3());

            PhysicsSystem physics = client.getWorld().physics();
            physics.getBodyInterface().activateBody(result.getBodyId());

            holdConstraint = settings.create(holdPoint, heldBody);
            client.getWorld().physics().addConstraint(holdConstraint);
            holdPointDistance = PCPlayer.REACH * result.getFraction();
        });
    }

    private void endHolding()
    {
        // Already not holding
        if (!isHolding())
            return;

        client.getWorld().physics().removeConstraint(holdConstraint);
        holdPointDistance = PCPlayer.REACH;

        previousState.restoreState(heldBody);

        heldBody = null;
        holdConstraint = null;
    }

    private boolean isHolding()
    {
        return holdConstraint != null;
    }

    @Override
    public void update(float deltaTime)
    {
        Profiler profiler = FlareProfiler.world();
        profiler.push("holdSystem");
        if (holdPoint == null)
            holdPoint = createHoldPoint();

        if (Keybinds.HOLD_OBJECT.isActive())
        {
            startHolding();
        } else
        {
            endHolding();
        }
        profiler.pop();
    }
}
