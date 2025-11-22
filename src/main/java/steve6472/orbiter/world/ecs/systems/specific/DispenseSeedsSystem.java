package steve6472.orbiter.world.ecs.systems.specific;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.core.util.RandomUtil;
import steve6472.flare.assets.model.blockbench.animation.controller.AnimationController;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.Registries;
import steve6472.orbiter.rendering.gizmo.Gizmos;
import steve6472.orbiter.util.AABB;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orbiter.world.ecs.components.physics.LinearVelocity;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.components.physics.Rotation;
import steve6472.orbiter.world.ecs.components.specific.CropPlot;
import steve6472.orbiter.world.ecs.components.specific.LifetimeTicks;
import steve6472.orbiter.world.ecs.components.specific.Seed;
import steve6472.orbiter.world.ecs.components.specific.SeedDispenser;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;
import steve6472.orbiter.world.ecs.systems.VelocitySystem;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 11/22/2025
 * Project: Orbiter <br>
 */
public class DispenseSeedsSystem extends IteratingProfiledSystem
{
    private static final Logger LOGGER = Log.getLogger(DispenseSeedsSystem.class);

    public static boolean VIZUALIZE_TRAJECTORY = false;

    public DispenseSeedsSystem()
    {
        super(Family.all(UUIDComp.class, Position.class, Rotation.class, SeedDispenser.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        final Family CROP_PLOT_FAMILY = Family.all(UUIDComp.class, Position.class, CropPlot.class).get();

        Position position = Components.POSITION.get(entity);
        Rotation rotation = Components.ROTATION.get(entity);
        SeedDispenser dispenser = Components.SEED_DISPENSER.get(entity);
        if (dispenser.currentLevel <= 0)
            return;
        Quaternionf quat = rotation.toQuat();

        Vector3f targetPos = new Vector3f(0, -0.2f, -2).rotate(quat).add(position.toVec3f());
        float dispenseRange = 2f;
        float dispenseHeight = 0.75f;
        AABB box = AABB.fromSize(dispenseRange, dispenseHeight, dispenseRange);
        Gizmos.filledLineCuboid(box.translate(targetPos), 0x4030a080);

        dispenser.cooldown = Math.max(0, dispenser.cooldown - 1);

        ImmutableArray<Entity> cropPlots = getEngine().getEntitiesFor(CROP_PLOT_FAMILY);
        for (Entity cropPlotEntity : cropPlots)
        {
            Position plotPosition = Components.POSITION.get(cropPlotEntity);
            CropPlot cropPlot = Components.CROP_PLOT.get(cropPlotEntity);

            boolean validTarget = box.containsPoint(targetPos, plotPosition.toVec3f());
            if (validTarget)
                Gizmos.point(plotPosition.toVec3f(), cropPlot.hasSeed ? 0xffff0000 : 0xff00ff00).alwaysOnTop();

            if (cropPlot.hasSeed || !validTarget)
                continue;

            if (dispenser.cooldown > 0)
                continue;

            dispenser.cooldown = dispenser.maxCooldown;
            dispenser.currentLevel--;

            Vector3f launchStart = position.toVec3f();

            Components.ANIMATED_MODEL.ifPresent(entity, model -> {
                AnimationController.LocatorInfo locator = model.animationController.getLocator("launch_point");
                if (locator != null)
                    launchStart.set(locator.position());
            });

            final float gravity = Constants.GRAVITY.y();

            LaunchResult launchResult = computeLaunchVelocity(launchStart, plotPosition.toVec3f(), gravity, 0.75f);
            long flightTicks = (int) Math.ceil(launchResult.flightTicks);

            if (VIZUALIZE_TRAJECTORY)
            {
                Gizmos.line(launchStart, new Vector3f(launchStart).add(launchResult.velocity), 0xffff8030).alwaysOnTop().stayForMs(1000).fadeOut();
                Vector3f[] vector3fs = VelocitySystem.simulateTrajectory(launchStart, launchResult.velocity, (int) flightTicks);
                for (Vector3f vector3f : vector3fs)
                {
                    Gizmos.point(vector3f, 0xffffffff).stayForMs(1000).fadeOut();
                }
            }

            Key projectileKey = Constants.key("seed_projectile");
            EntityBlueprint projectileBlueprint = Registries.ENTITY_BLUEPRINT.get(projectileKey);
            if (projectileBlueprint == null)
            {
                Log.warningOnce(LOGGER, "entity '%s' not found, seed dispenser will not work".formatted(projectileKey));
                return;
            }

            Entity projectileEntity = OrbiterApp.getInstance().getClient().getWorld().addEntity(projectileBlueprint, UUID.randomUUID(), true, launchStart);
            projectileEntity.add(new Position(launchStart.x, launchStart.y, launchStart.z));
            projectileEntity.add(new LinearVelocity(launchResult.velocity.x, launchResult.velocity.y, launchResult.velocity.z));
            projectileEntity.add(new LifetimeTicks(flightTicks));
            projectileEntity.add(new Seed());
            Quaternionf quaternionf = randomQuaternion();
            projectileEntity.add(new Rotation(quaternionf.x, quaternionf.y, quaternionf.z, quaternionf.w));

            return;
        }
    }

    private static Quaternionf randomQuaternion()
    {
        // Use uniform random numbers
        double u1 = RandomUtil.randomDouble(0, 1);
        double u2 = RandomUtil.randomDouble(0, 1);
        double u3 = RandomUtil.randomDouble(0, 1);

        double sqrt1MinusU1 = Math.sqrt(1 - u1);
        double sqrtU1 = Math.sqrt(u1);

        double theta1 = 2 * Math.PI * u2;
        double theta2 = 2 * Math.PI * u3;

        float x = (float) (sqrt1MinusU1 * Math.sin(theta1));
        float y = (float) (sqrt1MinusU1 * Math.cos(theta1));
        float z = (float) (sqrtU1 * Math.sin(theta2));
        float w = (float) (sqrtU1 * Math.cos(theta2));

        return new Quaternionf(x, y, z, w);
    }

    private static LaunchResult computeLaunchVelocity(Vector3f start, Vector3f end, float gravity, float maxHeightIncrease)
    {
        float x0 = start.x, y0 = start.y, z0 = start.z;
        float x1 = end.x, y1 = end.y, z1 = end.z;

        float yPeak = y0 + maxHeightIncrease;
        gravity = Math.abs(gravity);

        // Vertical component required to reach yPeak
        float vy = (float) Math.sqrt(2f * gravity * maxHeightIncrease);

        // Time going up
        float tUp = vy / gravity;

        // Time coming down to target Y
        float tDown = (float) Math.sqrt(2f * (yPeak - y1) / gravity);

        // Total flight time in seconds
        float totalTime = tUp + tDown;

        // Horizontal velocity components
        float vx = (x1 - x0) / totalTime;
        float vz = (z1 - z0) / totalTime;

        Vector3f velocity = new Vector3f(vx, vy, vz);

        // Time in ticks
        float ticks = totalTime * Constants.TICKS_IN_SECOND;

        return new LaunchResult(velocity, totalTime, ticks);
    }

    private record LaunchResult(Vector3f velocity, float flightSeconds, float flightTicks)
    {
    }
}
