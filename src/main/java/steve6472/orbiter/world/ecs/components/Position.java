package steve6472.orbiter.world.ecs.components;

import org.joml.Vector3f;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class Position
{
    private double x, y, z;
    boolean modified;

    public Position(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.modified = true;
    }

    public Position()
    {
        this(0, 0, 0);
    }

    public void set(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.modified = true;
    }

    public void add(double x, double y, double z)
    {
        this.x += x;
        this.y += y;
        this.z += z;
        this.modified = true;
    }

    public boolean modified()
    {
        return modified;
    }

    public void resetModified()
    {
        this.modified = false;
    }

    public double x()
    {
        return x;
    }

    public double y()
    {
        return y;
    }

    public double z()
    {
        return z;
    }

    public Vector3f toVec3f()
    {
        return new Vector3f((float) x, (float) y, (float) z);
    }

    @Override
    public String toString()
    {
        return String.format("Position{" + "x=%.6f, y=%.6f, z=%.6f, modified=" + modified + '}', x, y, z);
    }
}
