package steve6472.orbiter;

import com.github.stephengold.joltjni.AaBox;
import com.github.stephengold.joltjni.Quat;
import com.github.stephengold.joltjni.Vec3;
import com.github.stephengold.joltjni.readonly.Vec3Arg;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import steve6472.orbiter.util.AABB;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class Convert
{
    public static Vector3f physToJoml(Vec3Arg vec, Vector3f store)
    {
        return store.set(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Quaternionf physToJoml(Quat quat, Quaternionf store)
    {
        return store.set(quat.getX(), quat.getY(), quat.getZ(), quat.getW());
    }

    public static Vector3f physToJoml(Vec3Arg vec)
    {
        return physToJoml(vec, new Vector3f());
    }

    public static Vector3f physGetToJoml(Function<Vec3Arg, Vec3Arg> getter, Vector3f store)
    {
        Vec3Arg apply = getter.apply(new Vec3());
        return physToJoml(apply, store);
    }

    public static Quaternionf physGetToJoml(Function<Quat, Quat> getter, Quaternionf store)
    {
        Quat apply = getter.apply(new Quat());
        return physToJoml(apply, store);
    }

    public static Quaternionf physGetToJoml(Supplier<Quat> getter, Quaternionf store)
    {
        Quat apply = getter.get();
        return physToJoml(apply, store);
    }

    public static Vector3f physGetToJoml(Function<Vec3Arg, Vec3Arg> getter)
    {
        return physGetToJoml(getter, new Vector3f());
    }

    public static Quaternionf physGetToJomlQuat(Function<Quat, Quat> getter)
    {
        return physGetToJoml(getter, new Quaternionf());
    }

    public static AABB physToJoml(AaBox box)
    {
        return AABB.fromMinMax(physToJoml(box.getMin()), physToJoml(box.getMax()));
    }

/*    public static Matrix4f physToJoml(Mat44 mat, Matrix4f store)
    {
        store.set(
            mat.m00, mat.m10, mat.m20, mat.m30,   // First row
            mat.m01, mat.m11, mat.m21, mat.m31,   // Second row
            mat.m02, mat.m12, mat.m22, mat.m32,   // Third row
            mat.m03, mat.m13, mat.m23, mat.m33    // Fourth row
        );

        return store;
    }

    public static Matrix3f physToJoml(com.jme3.math.Matrix3f mat, Matrix3f store)
    {
        store.set(
            mat.get(0, 0), mat.get(0, 1), mat.get(0, 2),   // First row
            mat.get(1, 0), mat.get(1, 1), mat.get(1, 2),   // Second row
            mat.get(2, 0), mat.get(2, 1), mat.get(2, 2)   // Third row
        );

        return store;
    }*/

    /*
     * JOML to Physics
     */

    public static Vec3Arg jomlToPhys(Vector3f vec, Vec3 store)
    {
        store.set(vec.x, vec.y, vec.z);
        return store;
    }

    public static Quat jomlToPhys(Quaternionf quat, Quat store)
    {
        store.set(quat.x, quat.y, quat.z, quat.w);
        return store;
    }

    public static Vec3Arg jomlToPhys(Vector3f vec)
    {
        return jomlToPhys(vec, new Vec3());
    }

    public static Quat jomlToPhys(Quaternionf quat)
    {
        return jomlToPhys(quat, new Quat());
    }

//    public static com.jme3.math.Matrix3f jomlToPhys(Matrix3f mat, com.jme3.math.Matrix3f store)
//    {
//        store.set(0, 0, mat.m00);
//        store.set(1, 0, mat.m10);
//        store.set(2, 0, mat.m20);
//        store.set(0, 1, mat.m01);
//        store.set(1, 1, mat.m11);
//        store.set(2, 1, mat.m21);
//        store.set(0, 2, mat.m02);
//        store.set(1, 2, mat.m12);
//        store.set(2, 2, mat.m22);
//
//        return store;
//    }
}
