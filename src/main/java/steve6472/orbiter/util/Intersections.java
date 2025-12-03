package steve6472.orbiter.util;

import com.badlogic.ashley.core.Entity;
import com.github.stephengold.joltjni.*;
import com.github.stephengold.joltjni.readonly.ConstShape;
import com.github.stephengold.joltjni.readonly.ConstSubShape;
import org.joml.*;
import steve6472.core.log.Log;
import steve6472.core.util.ColorUtil;
import steve6472.orbiter.Convert;
import steve6472.orbiter.rendering.gizmo.Gizmos;
import steve6472.orbiter.rendering.gizmo.shapes.FilledLineCuboidRotated;
import steve6472.orbiter.rendering.gizmo.shapes.SphereGizmo;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.physics.Collision;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.components.physics.Rotation;

import java.lang.Math;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 11/30/2025
 * Project: Orbiter <br>
 */
public class Intersections
{
    private static final Logger LOGGER = Log.getLogger(Intersections.class);

    private static final Quaternionf NO_ROTATION = new Quaternionf();
    private static final boolean DEBUG_INTERSECTIONS = true;
    private static final boolean SHOW_ALL = false;
    private static final int DISPLAY_FOR_MS = 300;

    private static final int COLOR_AABB = 0x8080dfc1;
    private static final int COLOR_OBB = 0x80f09090;
    private static final int COLOR_SPHERE = 0x80c1e6df;
    private static final int COLOR_CAPSULE = 0x80dfc1e6;
    private static final int COLOR_POINT = 0x80dfe6c1;
    private static final int COLOR_LOCAL_BOUNDS = 0x20a0a0a0;

    public static final int CAPSULE_PRECISION = 5;

    private static Quaternionf rot(Rotation rotation)
    {
        if (rotation == null)
            return NO_ROTATION;
        return rotation.toQuat();
    }

    private static Quaternionf rot(Entity entity)
    {
        return rot(Components.ROTATION.get(entity));
    }

    public static boolean testAabbEntity(AABB aabb, Entity entity)
    {
        Position position = Components.POSITION.get(entity);
        if (position == null)
            return false;

        Collision collision = Components.COLLISION.get(entity);
        if (collision == null)
            return testAabbPoint(aabb, position.toVec3f());

        return testAabbShape(aabb, collision.shape(), position.toVec3f(), rot(entity), new Vector3f());
    }

    public static boolean testSphereEntity(Vector3f sphereCenter, float sphereRadius, Entity entity)
    {
        Position position = Components.POSITION.get(entity);
        if (position == null)
            return false;

        Collision collision = Components.COLLISION.get(entity);
        if (collision == null)
            return testSpherePoint(sphereCenter, sphereRadius, position.toVec3f());

        return testSphereShape(sphereCenter, sphereRadius, collision.shape(), position.toVec3f(), rot(entity), new Vector3f());
    }

    public static boolean testObbEntity(OBB obb, Entity entity)
    {
        Position position = Components.POSITION.get(entity);
        if (position == null)
            return false;

        Collision collision = Components.COLLISION.get(entity);
        if (collision == null)
            return obb.test(position.toVec3f());

        return testObbShape(obb, collision.shape(), position.toVec3f(), rot(entity), new Vector3f());
    }

    /*
     * Shapes
     */

    public static boolean testAabbShape(AABB aabb, ConstShape shape, Vector3f shapePosition, Quaternionf shapeRotation, Vector3f subShapePosition)
    {
        if (shape == null)
            return false;

        switch (shape)
        {
            case CompoundShape compoundShape ->
            {
                if (compoundShape.getNumSubShapes() > 1)
                {
                    AaBox localBounds = shape.getLocalBounds();
                    Matrix4f mat = new Matrix4f().translate(shapePosition).rotate(shapeRotation);
                    localBounds = localBounds.transformed(new Mat44(mat.get(new float[16])));
                    AABB localAabb = Convert.physToJoml(localBounds);
                    boolean r = Intersectionf.testAabAab(aabb.getMin(), aabb.getMax(), localAabb.getMin(), localAabb.getMax());

                    if (DEBUG_INTERSECTIONS && (r || SHOW_ALL))
                    {
                        Gizmos.filledLineCuboid(localAabb, COLOR_LOCAL_BOUNDS).stayForMs(DISPLAY_FOR_MS);
                    }

                    if (!r)
                        return false;
                }

                for (ConstSubShape subShape : compoundShape.getSubShapes())
                {
                    Vec3 positionCom = subShape.getPositionCom();
                    if (testAabbShape(aabb, subShape.getShape(), shapePosition, shapeRotation, Convert.physToJoml(positionCom)))
                        return true;
                }
            }
            case BoxShape boxShape ->
            {
                Vector3f center = new Vector3f(subShapePosition).add(shapePosition);
                Vector3f halfExtent = Convert.physToJoml(boxShape.getHalfExtent());
                return testAabbObb(aabb, OBB.fromAabbAndRotation(AABB.fromCenterHalfSize(center, halfExtent), shapeRotation));
            }
            case SphereShape sphereShape ->
            {
                return testAabbSphere(aabb, new Vector3f(subShapePosition).add(shapePosition), sphereShape.getRadius());
            }
            case CapsuleShape capsuleShape ->
            {
                return testAabbCapsule(aabb, new Vector3f(subShapePosition).add(shapePosition), capsuleShape.getHalfHeightOfCylinder(), capsuleShape.getRadius(), shapeRotation);
            }
            default -> LOGGER.warning("Unknown shape for intersection test " + shape.getClass().getSimpleName());
        }

        return false;
    }

    public static boolean testSphereShape(Vector3f sphereCenter, float sphereRadius, ConstShape shape, Vector3f shapePosition, Quaternionf shapeRotation, Vector3f subShapePosition)
    {
        if (shape == null)
            return false;

        switch (shape)
        {
            case CompoundShape compoundShape ->
            {
                if (compoundShape.getNumSubShapes() > 1)
                {
                    AaBox localBounds = shape.getLocalBounds();
                    Matrix4f mat = new Matrix4f().translate(shapePosition).rotate(shapeRotation);
                    localBounds = localBounds.transformed(new Mat44(mat.get(new float[16])));
                    AABB localAabb = Convert.physToJoml(localBounds);
                    AABB sphereBoundingBox = AABB.fromCenterRadius(sphereCenter, sphereRadius);
                    boolean r = Intersectionf.testAabAab(sphereBoundingBox.getMin(), sphereBoundingBox.getMax(), localAabb.getMin(), localAabb.getMax());

                    if (DEBUG_INTERSECTIONS && (r || SHOW_ALL))
                    {
                        Gizmos.filledLineCuboid(localAabb, COLOR_LOCAL_BOUNDS).stayForMs(DISPLAY_FOR_MS);
                    }

                    if (!r)
                        return false;
                }

                for (ConstSubShape subShape : compoundShape.getSubShapes())
                {
                    Vec3 positionCom = subShape.getPositionCom();
                    if (testSphereShape(sphereCenter, sphereRadius, subShape.getShape(), shapePosition, shapeRotation, Convert.physToJoml(positionCom)))
                        return true;
                }
            }
            case BoxShape boxShape ->
            {
                Vector3f center = new Vector3f(subShapePosition).add(shapePosition);
                Vector3f halfExtent = Convert.physToJoml(boxShape.getHalfExtent());
                return testObbSphere(OBB.fromAabbAndRotation(AABB.fromCenterHalfSize(center, halfExtent), shapeRotation), sphereCenter, sphereRadius, true);
            }
            case SphereShape sphereShape ->
            {
                return testSphereSphere(sphereCenter, sphereRadius, new Vector3f(subShapePosition).add(shapePosition), sphereShape.getRadius());
            }
            case CapsuleShape capsuleShape ->
            {
                return testSphereCapsule(sphereCenter, sphereRadius, new Vector3f(subShapePosition).add(shapePosition), capsuleShape.getHalfHeightOfCylinder(), capsuleShape.getRadius(), shapeRotation);
            }
            default -> LOGGER.warning("Unknown shape for intersection test " + shape.getClass().getSimpleName());
        }

        return false;
    }

    public static boolean testObbShape(OBB obb, ConstShape shape, Vector3f shapePosition, Quaternionf shapeRotation, Vector3f subShapePosition)
    {
        if (shape == null)
            return false;

        switch (shape)
        {
            case CompoundShape compoundShape ->
            {
                for (ConstSubShape subShape : compoundShape.getSubShapes())
                {
                    Vec3 positionCom = subShape.getPositionCom();
                    if (testObbShape(obb, subShape.getShape(), shapePosition, shapeRotation, Convert.physToJoml(positionCom)))
                        return true;
                }
            }
            case BoxShape boxShape ->
            {
                Vector3f center = new Vector3f(subShapePosition).add(shapePosition);
                Vector3f halfExtent = Convert.physToJoml(boxShape.getHalfExtent());
                return testObbObb(obb, OBB.fromAabbAndRotation(AABB.fromCenterHalfSize(center, halfExtent), shapeRotation));
            }
            case SphereShape sphereShape ->
            {
                return testObbSphere(obb, new Vector3f(subShapePosition).add(shapePosition), sphereShape.getRadius(), true);
            }
            case CapsuleShape capsuleShape ->
            {
                return testObbCapsule(obb, new Vector3f(subShapePosition).add(shapePosition), capsuleShape.getHalfHeightOfCylinder(), capsuleShape.getRadius(), shapeRotation);
            }
            default -> LOGGER.warning("Unknown shape for intersection test " + shape.getClass().getSimpleName());
        }

        return false;
    }

    /*
     * Primitives
     */

    /*
     * AABB
     */

    public static boolean testAabbPoint(AABB aabb, Vector3f pointPosition)
    {
        boolean r = aabb.containsPoint(pointPosition);

        if (DEBUG_INTERSECTIONS && (r || SHOW_ALL))
        {
            Gizmos.filledLineCuboid(aabb, COLOR_AABB).stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
            Gizmos.point(pointPosition, COLOR_POINT, 3f).stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
        }

        return r;
    }

    public static boolean testAabbSphere(AABB aabb, Vector3f sphereCenter, float sphereRadius)
    {
        boolean r = Intersectionf.testAabSphere(aabb.getMin(), aabb.getMax(), sphereCenter, sphereRadius * sphereRadius);

        if (DEBUG_INTERSECTIONS && (r || SHOW_ALL))
        {
            Gizmos.filledLineCuboid(aabb, COLOR_AABB).stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
            Gizmos.addGizmo(new SphereGizmo(sphereCenter, sphereRadius, 12, COLOR_SPHERE, 1f))
                .stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
        }

        return r;
    }

    public static boolean testAabbAabb(AABB aabb, AABB secondAabb)
    {
        boolean r = Intersectionf.testAabAab(aabb.getMin(), aabb.getMax(), secondAabb.getMin(), secondAabb.getMax());

        if (DEBUG_INTERSECTIONS && (r || SHOW_ALL))
        {
            Gizmos.filledLineCuboid(aabb, COLOR_AABB).stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
            Gizmos.filledLineCuboid(secondAabb, COLOR_AABB).stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
        }

        return r;
    }

    public static boolean testAabbCapsule(AABB aabb, Vector3f capsulePosition, float halfHeight, float radius, Quaternionf capsuleRotation)
    {
        boolean r = true;

        // Basically just rotate a OBB round and check if all collisions pass, yes it's horrible but idc rn

        AABB capsuleAABB = AABB.fromCenterHalfSize(capsulePosition, new Vector3f(radius, halfHeight, radius));
        Vector3f halfSizes = capsuleAABB.getSize().mul(0.5f);
        for (int i = 0; i < CAPSULE_PRECISION; i++)
        {
            Quaternionf boxRotation = new Quaternionf(capsuleRotation).rotateY((float) ((Math.PI * 0.5d) * (i / (double) CAPSULE_PRECISION)));

            boolean testSection = Intersectionf.testObOb(
                aabb.getCenter(),
                new Vector3f(1, 0, 0),
                new Vector3f(0, 1, 0),
                new Vector3f(0, 0, 1),
                aabb.getSize().mul(0.5f),
                capsuleAABB.getCenter(),
                new Vector3f(1, 0, 0).rotate(boxRotation),
                new Vector3f(0, 1, 0).rotate(boxRotation),
                new Vector3f(0, 0, 1).rotate(boxRotation),
                halfSizes
            );

            if (!testSection)
            {
                // one box did not intersect
                r = false;
                break;
            }
        }

        // Intersects all the boxes, short-circuit and do not test the spheres

        if (!r)
        {
            float radiusSquared = radius * radius;
            Vector3f topSphereCenter = new Vector3f(capsulePosition).add(new Vector3f(0, halfHeight, 0).rotate(capsuleRotation));
            Vector3f bottomSphereCenter = new Vector3f(capsulePosition).add(new Vector3f(0, -halfHeight, 0).rotate(capsuleRotation));

            r = Intersectionf.testAabSphere(aabb.getMin(), aabb.getMax(), topSphereCenter, radiusSquared);
            if (!r)
                r = Intersectionf.testAabSphere(aabb.getMin(), aabb.getMax(), bottomSphereCenter, radiusSquared);
        }

        if (DEBUG_INTERSECTIONS && (r || SHOW_ALL))
        {
            Vector3f topSphereCenter = new Vector3f(capsulePosition).add(new Vector3f(0, halfHeight, 0).rotate(capsuleRotation));
            Vector3f bottomSphereCenter = new Vector3f(capsulePosition).add(new Vector3f(0, -halfHeight, 0).rotate(capsuleRotation));

            Gizmos.filledLineCuboid(aabb, COLOR_AABB).stayForMs(DISPLAY_FOR_MS).alwaysOnTop();

            Gizmos.addGizmo(new SphereGizmo(topSphereCenter, radius, 8, COLOR_CAPSULE, 1f)).stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
            Gizmos.addGizmo(new SphereGizmo(bottomSphereCenter, radius, 8, COLOR_CAPSULE, 1f)).stayForMs(DISPLAY_FOR_MS).alwaysOnTop();

            int capsuleColor = ColorUtil.multiplyAlpha(COLOR_CAPSULE, 1f / CAPSULE_PRECISION);
            for (int i = 0; i < CAPSULE_PRECISION; i++)
            {
                Quaternionf boxRotation = new Quaternionf(capsuleRotation).rotateY((float) ((Math.PI * 0.5d) * (i / (double) CAPSULE_PRECISION)));
                Gizmos.addGizmo(new FilledLineCuboidRotated(capsuleAABB.getCenter(), halfSizes.x, halfSizes.y, halfSizes.z, capsuleColor, capsuleColor, 1, boxRotation))
                    .stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
            }
        }

        return r;
    }

    public static boolean testAabbObb(AABB aabb, OBB obb)
    {
        boolean r = Intersectionf.testObOb(
            aabb.getCenter(),
            new Vector3f(1, 0, 0),
            new Vector3f(0, 1, 0),
            new Vector3f(0, 0, 1),
            aabb.getSize().mul(0.5f),
            obb.center(),
            obb.localX(),
            obb.localY(),
            obb.localZ(),
            obb.halfSizes()
        );

        if (DEBUG_INTERSECTIONS && (r || SHOW_ALL))
        {
            Gizmos.filledLineCuboid(aabb, COLOR_AABB).stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
            Gizmos.addGizmo(new FilledLineCuboidRotated(obb.center, obb.halfSizes.x(), obb.halfSizes.y(), obb.halfSizes.z(), COLOR_OBB, COLOR_OBB, 1, obb.getRotation()))
                .stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
        }

        return r;
    }

    /*
     * Sphere
     */

    public static boolean testSpherePoint(Vector3f sphereCenter, float sphereRadius, Vector3f pointPosition)
    {
        boolean r = sphereCenter.distance(pointPosition) <= sphereRadius;

        if (DEBUG_INTERSECTIONS && (r || SHOW_ALL))
        {
            Gizmos.addGizmo(new SphereGizmo(sphereCenter, sphereRadius, 12, COLOR_SPHERE, 1f))
                .stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
            Gizmos.point(pointPosition, COLOR_POINT, 3f).stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
        }

        return r;
    }

    public static boolean testSphereSphere(Vector3f sphereCenter, float sphereRadius, Vector3f otherSphereCenter, float otherSphereRadius)
    {
        boolean r = Intersectionf.testSphereSphere(sphereCenter, sphereRadius * sphereRadius, otherSphereCenter, otherSphereRadius * otherSphereRadius);

        if (DEBUG_INTERSECTIONS && (r || SHOW_ALL))
        {
            Gizmos.addGizmo(new SphereGizmo(sphereCenter, sphereRadius, 12, COLOR_SPHERE, 1f))
                .stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
            Gizmos.addGizmo(new SphereGizmo(otherSphereCenter, otherSphereRadius, 12, COLOR_SPHERE, 1f))
                .stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
        }

        return r;
    }

    public static boolean testSphereCapsule(Vector3f sphereCenter, float sphereRadius, Vector3f capsulePosition, float halfHeight, float radius, Quaternionf capsuleRotation)
    {
        boolean r = true;

        // Basically just rotate a OBB round and check if all collisions pass, yes it's horrible but idc rn

        AABB capsuleAABB = AABB.fromCenterHalfSize(capsulePosition, new Vector3f(radius, halfHeight, radius));
        Vector3f halfSizes = capsuleAABB.getSize().mul(0.5f);
        for (int i = 0; i < CAPSULE_PRECISION; i++)
        {
            Quaternionf boxRotation = new Quaternionf(capsuleRotation).rotateY((float) ((Math.PI * 0.5d) * (i / (double) CAPSULE_PRECISION)));

            boolean testSection = testObbSphere(OBB.fromAabbAndRotation(capsuleAABB, boxRotation), sphereCenter, sphereRadius, false);

            if (!testSection)
            {
                // one box did not intersect
                r = false;
                break;
            }
        }

        // Intersects all the boxes, short-circuit and do not test the spheres

        if (!r)
        {
            float radiusSquared = radius * radius;
            float sphereRadiusSquared = sphereRadius * sphereRadius;
            Vector3f topSphereCenter = new Vector3f(capsulePosition).add(new Vector3f(0, halfHeight, 0).rotate(capsuleRotation));
            Vector3f bottomSphereCenter = new Vector3f(capsulePosition).add(new Vector3f(0, -halfHeight, 0).rotate(capsuleRotation));

            r = Intersectionf.testSphereSphere(sphereCenter, sphereRadiusSquared, topSphereCenter, radiusSquared);
            if (!r)
                r = Intersectionf.testSphereSphere(sphereCenter, sphereRadiusSquared, bottomSphereCenter, radiusSquared);
        }

        if (DEBUG_INTERSECTIONS && (r || SHOW_ALL))
        {
            Gizmos.addGizmo(new SphereGizmo(sphereCenter, sphereRadius, 12, COLOR_SPHERE, 1f))
                .stayForMs(DISPLAY_FOR_MS).alwaysOnTop();

            Vector3f topSphereCenter = new Vector3f(capsulePosition).add(new Vector3f(0, halfHeight, 0).rotate(capsuleRotation));
            Vector3f bottomSphereCenter = new Vector3f(capsulePosition).add(new Vector3f(0, -halfHeight, 0).rotate(capsuleRotation));

            Gizmos.addGizmo(new SphereGizmo(topSphereCenter, radius, 8, COLOR_CAPSULE, 1f)).stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
            Gizmos.addGizmo(new SphereGizmo(bottomSphereCenter, radius, 8, COLOR_CAPSULE, 1f)).stayForMs(DISPLAY_FOR_MS).alwaysOnTop();

            int capsuleColor = ColorUtil.multiplyAlpha(COLOR_CAPSULE, 1f / CAPSULE_PRECISION);
            for (int i = 0; i < CAPSULE_PRECISION; i++)
            {
                Quaternionf boxRotation = new Quaternionf(capsuleRotation).rotateY((float) ((Math.PI * 0.5d) * (i / (double) CAPSULE_PRECISION)));
                Gizmos.addGizmo(new FilledLineCuboidRotated(capsuleAABB.getCenter(), halfSizes.x, halfSizes.y, halfSizes.z, capsuleColor, capsuleColor, 1, boxRotation))
                    .stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
            }
        }

        return r;
    }

    /*
     * OBB
     */

    public static boolean testObbSphere(OBB obb, Vector3f sphereCenter, float sphereRadius, boolean canDebug)
    {
        Vector3f closestPoint = obb.closestPoint(sphereCenter);
        float distSq = new Vector3f(sphereCenter).sub(closestPoint).lengthSquared();
        float radiusSq = sphereRadius * sphereRadius;
        boolean r = distSq < radiusSq;

        if (canDebug && (DEBUG_INTERSECTIONS && (r || SHOW_ALL)))
        {
            Gizmos.addGizmo(new FilledLineCuboidRotated(obb.center, obb.halfSizes.x(), obb.halfSizes.y(), obb.halfSizes.z(), COLOR_OBB, COLOR_OBB, 1, obb.getRotation()))
                .stayForMs(DISPLAY_FOR_MS).alwaysOnTop();

            Gizmos.addGizmo(new SphereGizmo(sphereCenter, sphereRadius, 12, COLOR_SPHERE, 1f))
                .stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
        }

        return r;
    }

    public static boolean testObbObb(OBB obb, OBB secondObb)
    {
        boolean r = obb.testObb(secondObb);

        if (DEBUG_INTERSECTIONS && (r || SHOW_ALL))
        {
            Gizmos.addGizmo(new FilledLineCuboidRotated(obb.center, obb.halfSizes.x(), obb.halfSizes.y(), obb.halfSizes.z(), COLOR_OBB, COLOR_OBB, 1, obb.getRotation()))
                .stayForMs(DISPLAY_FOR_MS).alwaysOnTop();

            Gizmos.addGizmo(new FilledLineCuboidRotated(
                secondObb.center, secondObb.halfSizes.x(), secondObb.halfSizes.y(), secondObb.halfSizes.z(), COLOR_OBB, COLOR_OBB, 1, secondObb.getRotation()))
                .stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
        }

        return r;
    }

    public static boolean testObbCapsule(OBB obb, Vector3f capsulePosition, float halfHeight, float radius, Quaternionf capsuleRotation)
    {
        boolean r = true;

        // Basically just rotate a OBB round and check if all collisions pass, yes it's horrible but idc rn

        AABB capsuleAABB = AABB.fromCenterHalfSize(capsulePosition, new Vector3f(radius, halfHeight, radius));
        Vector3f halfSizes = capsuleAABB.getSize().mul(0.5f);
        for (int i = 0; i < CAPSULE_PRECISION; i++)
        {
            Quaternionf boxRotation = new Quaternionf(capsuleRotation).rotateY((float) ((Math.PI * 0.5d) * (i / (double) CAPSULE_PRECISION)));

            boolean testSection = obb.testObb(OBB.fromAabbAndRotation(capsuleAABB, boxRotation));

            if (!testSection)
            {
                // one box did not intersect
                r = false;
                break;
            }
        }

        // Intersects all the boxes, short-circuit and do not test the spheres

        if (!r)
        {
            float radiusSquared = radius * radius;
            Vector3f topSphereCenter = new Vector3f(capsulePosition).add(new Vector3f(0, halfHeight, 0).rotate(capsuleRotation));
            Vector3f bottomSphereCenter = new Vector3f(capsulePosition).add(new Vector3f(0, -halfHeight, 0).rotate(capsuleRotation));

            r = testObbSphere(obb, topSphereCenter, radiusSquared, false);
            if (!r)
                r = testObbSphere(obb, bottomSphereCenter, radiusSquared, false);
        }

        if (DEBUG_INTERSECTIONS && (r || SHOW_ALL))
        {
            Gizmos.addGizmo(new FilledLineCuboidRotated(obb.center, obb.halfSizes.x(), obb.halfSizes.y(), obb.halfSizes.z(), COLOR_OBB, COLOR_OBB, 1, obb.getRotation()))
                .stayForMs(DISPLAY_FOR_MS).alwaysOnTop();

            Vector3f topSphereCenter = new Vector3f(capsulePosition).add(new Vector3f(0, halfHeight, 0).rotate(capsuleRotation));
            Vector3f bottomSphereCenter = new Vector3f(capsulePosition).add(new Vector3f(0, -halfHeight, 0).rotate(capsuleRotation));

            Gizmos.addGizmo(new SphereGizmo(topSphereCenter, radius, 8, COLOR_CAPSULE, 1f)).stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
            Gizmos.addGizmo(new SphereGizmo(bottomSphereCenter, radius, 8, COLOR_CAPSULE, 1f)).stayForMs(DISPLAY_FOR_MS).alwaysOnTop();

            int capsuleColor = ColorUtil.multiplyAlpha(COLOR_CAPSULE, 1f / CAPSULE_PRECISION);
            for (int i = 0; i < CAPSULE_PRECISION; i++)
            {
                Quaternionf boxRotation = new Quaternionf(capsuleRotation).rotateY((float) ((Math.PI * 0.5d) * (i / (double) CAPSULE_PRECISION)));
                Gizmos.addGizmo(new FilledLineCuboidRotated(capsuleAABB.getCenter(), halfSizes.x, halfSizes.y, halfSizes.z, capsuleColor, capsuleColor, 1, boxRotation))
                    .stayForMs(DISPLAY_FOR_MS).alwaysOnTop();
            }
        }

        return r;
    }
}
