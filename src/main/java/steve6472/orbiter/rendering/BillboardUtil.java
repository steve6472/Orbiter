package steve6472.orbiter.rendering;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import steve6472.flare.Camera;
import steve6472.flare.FlareConstants;
import steve6472.orbiter.world.particle.components.ParticleBillboard;

public class BillboardUtil
{
    public static Matrix4f makeBillboard(Vector3f position, Vector3f cameraPos, Camera camera, ParticleBillboard billboard)
    {
        Matrix4f mat = new Matrix4f();
        Quaternionf cameraRot = new Quaternionf().setFromUnnormalized(camera.getViewMatrix());

        switch (billboard.billboard)
        {
            case FIXED -> {}
            case ROTATE_XYZ -> mat.rotation(cameraRot.invert());
            case ROTATE_Y -> mat.rotateY(camera.yaw());
            case LOOKAT_XYZ -> mat.billboardSpherical(position, cameraPos);
            case LOOKAT_Y -> mat.billboardCylindrical(position, cameraPos, FlareConstants.CAMERA_UP);
            default -> throw new IllegalArgumentException("Unsupported billboard mode: " + billboard.billboard);
        }

        // Apply translation
        mat.m30(position.x);
        mat.m31(position.y);
        mat.m32(position.z);

        return mat;
    }

    public static Matrix4f applySpin(Matrix4f billboardMat, float angleRadians) {
        Matrix4f spin = new Matrix4f().rotate(angleRadians, 0, 0, 1);
        return new Matrix4f(billboardMat).mul(spin);
    }
}