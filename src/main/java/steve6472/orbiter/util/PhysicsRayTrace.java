package steve6472.orbiter.util;

import com.badlogic.ashley.core.Entity;
import com.github.stephengold.joltjni.*;
import com.github.stephengold.joltjni.readonly.ConstShape;
import com.github.stephengold.joltjni.readonly.ConstSubShape;
import org.joml.Vector3f;
import steve6472.core.util.MathUtil;
import steve6472.flare.Camera;
import steve6472.orbiter.Client;
import steve6472.orbiter.Convert;
import steve6472.orbiter.Registries;
import steve6472.orbiter.player.PCPlayer;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.collision.OrbiterCollisionShape;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.physics.Collision;
import steve6472.orbiter.world.ecs.systems.ClickECS;

import java.util.Optional;
import java.util.UUID;

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
    private int lookAtSubshapeOrdinal;

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
                t ->
                {
                    lookAtObject = t;
                    lookAtSubshapeOrdinal = -1;

                    int bodyId = lookAtObject.getBodyId();
                    World world = client.getWorld();

                    UUID uuid = world.bodyMap().getUUIDById(bodyId);
                    if (uuid == null)
                        return;

                    Body body = world.bodyMap().getBodyByUUID(uuid);
                    if (body == null)
                        return;

                    ConstShape shape = body.getShape();
                    ConstShape leafShape = shape.getLeafShape(lookAtObject.getSubShapeId2(), new int[1]);

                    if (shape instanceof CompoundShape compoundShape)
                    {
                        ConstSubShape[] subShapes = compoundShape.getSubShapes();
                        for (int i = 0; i < subShapes.length; i++)
                        {
                            if (subShapes[i].getShape().equals(leafShape))
                            {
                                lookAtSubshapeOrdinal = i;
                                return;
                            }
                        }
                    }

                },
                () ->
                {
                    lookAtObject = null;
                    lookAtSubshapeOrdinal = -1;
                }
            );
    }

    public RayCastResult getLookAtObject()
    {
        return lookAtObject;
    }

    public int getLookAtSubshapeOrdinal()
    {
        return lookAtSubshapeOrdinal;
    }
}
