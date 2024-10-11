package steve6472.orbiter;

import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Transform;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
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

    public static Quaternionf physToJoml(com.jme3.math.Quaternion quat, Quaternionf store)
    {
        return store.set(quat.getX(), quat.getY(), quat.getZ(), quat.getW());
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

    public static Quaternionf physGetToJoml(Function<com.jme3.math.Quaternion, com.jme3.math.Quaternion> getter, Quaternionf store)
    {
        com.jme3.math.Quaternion apply = getter.apply(new com.jme3.math.Quaternion());
        return physToJoml(apply, store);
    }

    public static Vector3f physGetToJoml(Function<com.jme3.math.Vector3f, com.jme3.math.Vector3f> getter)
    {
        return physGetToJoml(getter, new Vector3f());
    }

    public static Matrix4f physToJoml(com.jme3.math.Matrix4f mat, Matrix4f store)
    {
        store.set(
            mat.m00, mat.m10, mat.m20, mat.m30,   // First row
            mat.m01, mat.m11, mat.m21, mat.m31,   // Second row
            mat.m02, mat.m12, mat.m22, mat.m32,   // Third row
            mat.m03, mat.m13, mat.m23, mat.m33    // Fourth row
        );

        return store;
    }

    public static Matrix4f physGetTransformToJoml(PhysicsRigidBody body, Matrix4f store)
    {
        Transform transform = new Transform();
        body.getTransform(transform);
        return physToJoml(transform.toTransformMatrix(), store);
    }

    public static Matrix4f physGetTransformToJoml(PhysicsGhostObject body, Matrix4f store)
    {
        Transform transform = new Transform();
        body.getTransform(transform);
        return physToJoml(transform.toTransformMatrix(), store);
    }

    public static Matrix4f physGetTransformToJoml(PhysicsCharacter character, Matrix4f store)
    {
        Transform transform = new Transform();
        character.getTransform(transform);
        return physToJoml(transform.toTransformMatrix(), store);
    }

    /*
     * JOML to Physics
     */

    public static com.jme3.math.Vector3f jomlToPhys(Vector3f vec, com.jme3.math.Vector3f store)
    {
        return store.set(vec.x, vec.y, vec.z);
    }

    public static com.jme3.math.Quaternion jomlToPhys(Quaternionf quat, com.jme3.math.Quaternion store)
    {
        return store.set(quat.x, quat.y, quat.z, quat.w);
    }

    public static com.jme3.math.Vector3f jomlToPhys(Vector3f vec)
    {
        return jomlToPhys(vec, new com.jme3.math.Vector3f());
    }

    public static com.jme3.math.Quaternion jomlToPhys(Quaternionf quat)
    {
        return jomlToPhys(quat, new com.jme3.math.Quaternion());
    }
}
