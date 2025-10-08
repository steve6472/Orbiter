package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.github.stephengold.joltjni.*;
import com.github.stephengold.joltjni.readonly.Vec3Arg;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import steve6472.core.util.MathUtil;
import steve6472.flare.Camera;
import steve6472.flare.render.debug.DebugRender;
import steve6472.orbiter.Client;
import steve6472.orbiter.Convert;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.settings.Keybinds;
import steve6472.orbiter.world.World;

import java.util.Collection;

/**
 * Created by steve6472
 * Date: 10/7/2025
 * Project: Orbiter <br>
 */
public class AttractTestSystem extends EntitySystem
{
    private final Client client;

    public AttractTestSystem()
    {
        client = OrbiterApp.getInstance().getClient();
    }

    @Override
    public void update(float deltaTime)
    {
        World world = client.getWorld();
        if (world == null)
            return;

        if (!Keybinds.TEST_ATTRACT.isActive())
            return;

        Camera camera = client.getCamera();
        Vector3f direction = MathUtil.yawPitchToVector(camera.yaw() + (float) (Math.PI * 0.5f), camera.pitch());
        Vector3f add = new Vector3f(direction).mul(3).add(camera.viewPosition);
        Vec3Arg attractionPosition = Convert.jomlToPhys(add);

        Collection<Body> allBodies = world.bodyMap().getAllBodies();
        PhysicsSystem physics = world.physics();
        BodyInterface bodyInterface = physics.getBodyInterface();
        for (Body body : allBodies)
        {
            if (!body.isDynamic()) continue; // only attract dynamic bodies

            // Get current body position
            RVec3 bodyPos = body.getPosition();

            // Compute direction vector from body to attraction point
            float dx = attractionPosition.getX() - (float) bodyPos.getX();
            float dy = attractionPosition.getY() - (float) bodyPos.getY();
            float dz = attractionPosition.getZ() - (float) bodyPos.getZ();

            float distanceSq = dx * dx + dy * dy + dz * dz;
            if (distanceSq < 1e-6f) continue;

            float distance = (float) Math.sqrt(distanceSq);

            // Normalize direction
            float invLen = 1.0f / distance;
            float nx = dx * invLen;
            float ny = dy * invLen;
            float nz = dz * invLen;


            // -- Force model --
            // Base strength constant (tune this)
            float baseStrength = 40.0f;

            // Scale force by mass (so heavier objects move slower)
            float mass = body.getShape().getMassProperties().getMass();

            // Attenuate less aggressively than 1/r² (e.g. 1/r or 1/(r + c))
            float strength = baseStrength / (distance + 2.0f);
            if (distance <= 1f) strength *= (float) ((Math.log(strength + 0.01) * 0.8 + 2f) / 2f); // fade out very close
            if (distance <= 0.03f) strength *= distance * 0.2f; // fade out very close

            // Optional: clamp the force to avoid explosions
            float maxForce = 75.0f;
            strength = Math.min(strength, maxForce);

            // Final force vector
            Vec3 force = new Vec3(nx * strength * mass, ny * strength * mass, nz * strength * mass);

            // Apply force (impulse can be too aggressive for continuous attraction)
            body.resetSleepTimer();
            bodyInterface.activateBody(body.getId());
            body.addForce(force);

            DebugRender.addDebugObjectForMs(DebugRender.lineSphere(0.5f, 2, DebugRender.LIGHT_GRAY), 15, new Matrix4f().translate(add));
        }
    }
}
