package steve6472.orbiter.util;

import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Created by steve6472
 * Date: 11/22/2025
 * Project: Orbiter <br>
 */
public class AABB
{
    private final Vector3fc min;
    private final Vector3fc max;

    private AABB(Vector3f min, Vector3f max)
    {
        this.min = new Vector3f(min);
        this.max = new Vector3f(max);
    }

    public static AABB fromMinMax(Vector3f min, Vector3f max)
    {
        return new AABB(min, max);
    }

    public static AABB fromCenterHalfSize(Vector3f center, Vector3f halfSize)
    {
        Vector3f min = new Vector3f(center).sub(halfSize);
        Vector3f max = new Vector3f(center).add(halfSize);
        return new AABB(min, max);
    }

    public static AABB fromCenterHalfSize(Vector3f center, float halfWidth, float halfHeight, float halfDepth)
    {
        Vector3f min = new Vector3f(center).sub(halfWidth, halfHeight, halfDepth);
        Vector3f max = new Vector3f(center).add(halfWidth, halfHeight, halfDepth);
        return new AABB(min, max);
    }

    public static AABB fromCenterRadius(Vector3f center, float radius)
    {
        return fromCenterHalfSize(center, radius, radius, radius);
    }

    public static AABB fromSize(float width, float height, float depth)
    {
        Vector3f half = new Vector3f(width, height, depth).mul(0.5f);
        Vector3f min = new Vector3f(half).negate();
        Vector3f max = new Vector3f(half);
        return new AABB(min, max);
    }

    public boolean containsPoint(Vector3f point)
    {
        return (point.x >= min.x() && point.x <= max.x()) && (point.y >= min.y() && point.y <= max.y()) && (point.z >= min.z() && point.z <= max.z());
    }

    public boolean containsPointTranslated(Vector3f center, Vector3f point)
    {
        Vector3f halfSize = new Vector3f(max).sub(min).mul(0.5f);
        Vector3f local = new Vector3f(point).sub(center);

        return Math.abs(local.x) <= halfSize.x && Math.abs(local.y) <= halfSize.y && Math.abs(local.z) <= halfSize.z;
    }

    public Vector3f getMin()
    {
        return new Vector3f(min);
    }

    public Vector3f getMax()
    {
        return new Vector3f(max);
    }

    public Vector3f getCenter()
    {
        return new Vector3f(max).add(min).mul(0.5f);
    }

    public Vector3f getHalfSize()
    {
        return new Vector3f(max).sub(min).mul(0.5f);
    }

    public Vector3f getSize()
    {
        return new Vector3f(max).sub(min);
    }

    public AABB translate(Vector3f pos)
    {
        return AABB.fromMinMax(getMin().add(pos), getMax().add(pos));
    }
}
