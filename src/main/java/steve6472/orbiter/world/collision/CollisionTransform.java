package steve6472.orbiter.world.collision;

import com.jme3.bullet.collision.shapes.CollisionShape;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Created by steve6472
 * Date: 10/19/2024
 * Project: Orbiter <br>
 */
public class CollisionTransform
{
    private final Vector3f offset;
    private final Quaternionf rotation;
    private final CollisionShape shape;
    public short id;

    public static final CollisionTransform EMPTY = new CollisionTransform();

    public CollisionTransform()
    {
        this.offset = new Vector3f();
        this.rotation = new Quaternionf();
        this.shape = null;
    }

    public CollisionTransform(CollisionShape shape)
    {
        this.offset = new Vector3f();
        this.rotation = new Quaternionf();
        this.shape = shape;
    }

    public CollisionTransform(Vector3f offset, Quaternionf rotation, CollisionShape shape)
    {
        this.offset = offset;
        this.rotation = rotation;
        this.shape = shape;
    }

    public boolean hasShape()
    {
        return shape != null;
    }

    public boolean isPrimitive()
    {
        return offset.equals(0, 0, 0) && rotation.equals(0, 0, 0, 1);
    }

    public CollisionShape shape()
    {
        return shape;
    }

    public Vector3f offset()
    {
        return new Vector3f(offset);
    }

    public Quaternionf rotation()
    {
        return new Quaternionf(rotation);
    }

    public CollisionTransform offset(float x, float y, float z)
    {
        return new CollisionTransform(new Vector3f(offset).add(x, y, z), new Quaternionf(rotation), shape);
    }

    public CollisionTransform shape(CollisionShape shape)
    {
        return new CollisionTransform(new Vector3f(offset), new Quaternionf(rotation), shape);
    }

    @Override
    public String toString()
    {
        return "CollisionTransform{" + "offset=" + offset + ", rotation=" + rotation + ", shape=" + shape + '}';
    }
}
