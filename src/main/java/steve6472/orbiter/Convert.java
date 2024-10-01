package steve6472.orbiter;

import org.joml.Vector3f;

import java.util.function.Function;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class Convert
{
    public static Vector3f physToJoml(com.jme3.math.Vector3f vec, Vector3f store)
    {
        return store.set(vec.x, vec.y, vec.z);
    }

    public static Vector3f physToJoml(com.jme3.math.Vector3f vec)
    {
        return physToJoml(vec, new Vector3f());
    }

    public static Vector3f physGetToJoml(Function<com.jme3.math.Vector3f, com.jme3.math.Vector3f> getter, Vector3f store)
    {
        com.jme3.math.Vector3f apply = getter.apply(new com.jme3.math.Vector3f());
        return physToJoml(apply, store);
    }

    public static Vector3f physGetToJoml(Function<com.jme3.math.Vector3f, com.jme3.math.Vector3f> getter)
    {
        return physGetToJoml(getter, new Vector3f());
    }

    /*
     * JOML to Physics
     */

    public static com.jme3.math.Vector3f jomlToPhys(Vector3f vec, com.jme3.math.Vector3f store)
    {
        return store.set(vec.x, vec.y, vec.z);
    }

    public static com.jme3.math.Vector3f jomlToPhys(Vector3f vec)
    {
        return jomlToPhys(vec, new com.jme3.math.Vector3f());
    }
}
