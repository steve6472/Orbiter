package steve6472.orbiter.util;

import org.joml.*;

/**
 * Created by steve6472
 * Date: 12/2/2025
 * Project: Orbiter <br>
 */
public class OBB
{
    public final Vector3fc center;
    public final Vector3fc localX;
    public final Vector3fc localY;
    public final Vector3fc localZ;
    public final Vector3fc halfSizes;

    private OBB(Vector3fc center, Vector3fc localX, Vector3fc localY, Vector3fc localZ, Vector3fc halfSizes)
    {
        this.center = center;
        this.localX = localX;
        this.localY = localY;
        this.localZ = localZ;
        this.halfSizes = halfSizes;
    }

    public static OBB fromAabbAndRotation(AABB box, Quaternionf rotation)
    {
        Vector3f localX = new Vector3f(1, 0, 0).rotate(rotation);
        Vector3f localY = new Vector3f(0, 1, 0).rotate(rotation);
        Vector3f localZ = new Vector3f(0, 0, 1).rotate(rotation);
        return new OBB(box.getCenter(), localX, localY, localZ, box.getHalfSize());
    }

    public Quaternionf getRotation()
    {
        return new Quaternionf().setFromNormalized(new Matrix3f(localX, localY, localZ));
    }

    public Vector3f closestPoint(Vector3f point)
    {
        Vector3f result = new Vector3f(center);
        Vector3f dir = point.sub(center, new Vector3f());

        for (int i = 0; i < 3; i++)
        {
            Vector3fc axis;
            if (i == 0) axis = localX;
            else if (i == 1) axis = localY;
            else axis = localZ;

            float distance = dir.dot(axis);

            if (distance > halfSizes.get(i))
                distance = halfSizes.get(i);
            if (distance < - halfSizes.get(i))
                distance = -halfSizes.get(i);

            result = result.add(new Vector3f(axis).mul(distance));
        }

        return result;
    }

    public boolean test(Vector3f point)
    {
        Vector3f dir = point.sub(center, new Vector3f());

        for (int i = 0; i < 3; i++)
        {
            Vector3fc axis;
            if (i == 0) axis = localX;
            else if (i == 1) axis = localY;
            else axis = localZ;

            float distance = dir.dot(axis);

            if (distance > halfSizes.get(i))
                return false;
            if (distance < -halfSizes.get(i))
                return false;
        }

        return true;
    }

    public boolean testObb(OBB other)
    {
        return Intersectionf.testObOb(
            center.x(), center.y(), center.z(),
            localX.x(), localX.y(), localX.z(),
            localY.x(), localY.y(), localY.z(),
            localZ.x(), localZ.y(), localZ.z(),
            halfSizes.x(), halfSizes.y(), halfSizes.z(),
            other.center.x(), other.center.y(), other.center.z(),
            other.localX.x(), other.localX.y(), other.localX.z(),
            other.localY.x(), other.localY.y(), other.localY.z(),
            other.localZ.x(), other.localZ.y(), other.localZ.z(),
            other.halfSizes.x(), other.halfSizes.y(), other.halfSizes.z()
        );
    }

    public Vector3f center()
    {
        return new Vector3f(center);
    }

    public Vector3f localX()
    {
        return new Vector3f(localX);
    }

    public Vector3f localY()
    {
        return new Vector3f(localY);
    }

    public Vector3f localZ()
    {
        return new Vector3f(localZ);
    }

    public Vector3f halfSizes()
    {
        return new Vector3f(halfSizes);
    }
}
