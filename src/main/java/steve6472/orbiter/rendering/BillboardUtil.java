package steve6472.orbiter.rendering;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import steve6472.flare.Camera;
import steve6472.flare.FlareConstants;

public class BillboardUtil
{
    public static void makeBillboard(Matrix4f mat, Vector3f position, float velX, float velY, float velZ, Camera camera, Billboard billboard)
    {
//        var textRender = OrbiterApp.getInstance().masterRenderer().textRender();
        Vector3f cameraPos = camera.viewPosition;
        Quaternionf cameraRot = new Quaternionf().setFromUnnormalized(camera.getViewMatrix());

        switch (billboard)
        {
            case FIXED -> {}
            case ROTATE_XYZ -> mat.rotation(cameraRot.invert());
            case ROTATE_Y -> mat.rotateY(camera.yaw());
            case LOOKAT_XYZ -> mat.billboardSpherical(position, cameraPos);
            case LOOKAT_Y -> mat.billboardCylindrical(position, cameraPos, FlareConstants.CAMERA_UP);
            case LOOKAT_DIRECTION -> {
                Vector3f vec = new Vector3f(velX, velY, velZ);
                vec.normalize();

                Matrix4f dummy = new Matrix4f();
                dummy.translate(position);

                Quaternionf q = new Quaternionf().rotationTo(new Vector3f(1, 0, 0), vec);
                dummy.rotate(q);

                vec.set(cameraPos);

                Matrix4f dummyInv = new Matrix4f(dummy).invert();
                dummyInv.transformPosition(vec);

                float rotX = (float) Math.atan2(-vec.y, vec.z);
                Quaternionf rotQuat = new Quaternionf().rotationX(rotX);
                Quaternionf finalQuat = new Quaternionf(q).mul(rotQuat);

                mat.rotate(finalQuat);
            }
            default -> throw new IllegalArgumentException("Unsupported billboard mode: " + billboard);
        }
    }
}