package steve6472.orbiter.util;

import com.github.stephengold.joltjni.*;
import org.joml.Vector3f;
import steve6472.core.util.MathUtil;
import steve6472.flare.Camera;
import steve6472.orbiter.Client;
import steve6472.orbiter.Convert;
import steve6472.orbiter.player.PCPlayer;

import java.util.Optional;

/**
 * Created by steve6472
 * Date: 9/3/2025
 * Project: Orbiter <br>
 */
public class PhysicsRayTrace
{
    private final BroadPhaseLayerFilter broadPhaseLayerFilter = new BroadPhaseLayerFilter();
    private final ObjectLayerFilter objectLayerFilter = new ObjectLayerFilter();
    private final AllHitCastRayCollector allHitCollector = new AllHitCastRayCollector();
    private final ClosestHitCastRayCollector closestHitCollector = new ClosestHitCastRayCollector();
    private final BodyFilter noBodyFilter = new BodyFilter();
    private final Client client;
    private RayCastResult lookAtObject;

    public PhysicsRayTrace(Client client)
    {
        this.client = client;
    }

    public RRayCast createRay(Vector3f position, Vector3f direction, float distance)
    {
        return new RRayCast(Convert.jomlToPhys(position).toRVec3(), Convert.jomlToPhys(new Vector3f(direction).mul(distance)));
    }

    /// @param position Starting position
    /// @param direction Normalized vector
    /// @param distance Max distance
    public void rayTrace(Vector3f position, Vector3f direction, float distance, CastRayCollector collector, BodyFilter bodyFilter)
    {
        RRayCast ray = createRay(position, direction, distance);
        collector.reset();
        client.getWorld().physics().getNarrowPhaseQuery().castRay(ray, new RayCastSettings(), collector, broadPhaseLayerFilter, objectLayerFilter, bodyFilter);
    }

    public Optional<RayCastResult> rayTraceGetFirst(Vector3f position, Vector3f direction, float distance, boolean excludeClientPlayer)
    {
        BodyFilter bodyFilter = noBodyFilter;
        if (excludeClientPlayer)
        {
            bodyFilter = new IgnoreMultipleBodiesFilter();
            ((IgnoreMultipleBodiesFilter) bodyFilter).ignoreBody(((PCPlayer) client.player()).character.getBodyId());
        }

        rayTrace(position, direction, distance, closestHitCollector, bodyFilter);
        if (!closestHitCollector.hadHit())
            return Optional.empty();

        return Optional.of(closestHitCollector.getHit());
    }

    public Optional<RayCastResult> rayTraceGetFirst(Camera camera, float distance, boolean excludeClientPlayer)
    {
        Vector3f direction = MathUtil.yawPitchToVector(camera.yaw() + (float) (Math.PI * 0.5f), camera.pitch());
        return rayTraceGetFirst(camera.viewPosition, direction, distance, excludeClientPlayer);
    }

    public void updateLookAt(Camera camera, float reach)
    {
        rayTraceGetFirst(camera, reach, true)
            .ifPresentOrElse(
                t -> lookAtObject = t,
                () -> lookAtObject = null
            );
    }

    public RayCastResult getLookAtObject()
    {
        return lookAtObject;
    }
}
