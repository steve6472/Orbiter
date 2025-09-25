package steve6472.orbiter.rendering;

import com.badlogic.ashley.core.Entity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import steve6472.flare.Camera;
import steve6472.flare.FlareConstants;
import steve6472.orbiter.world.particle.ParticleComponents;
import steve6472.orbiter.world.particle.components.ParticleBillboard;
import steve6472.orbiter.world.particle.components.Velocity;

public class BillboardUtil
{
    public static Matrix4f makeBillboard(Vector3f position, Entity entity, Camera camera, ParticleBillboard billboard)
    {
//        var textRender = OrbiterApp.getInstance().masterRenderer().textRender();
        Matrix4f mat = new Matrix4f();
        Vector3f cameraPos = camera.viewPosition;
        Quaternionf cameraRot = new Quaternionf().setFromUnnormalized(camera.getViewMatrix());

        switch (billboard.billboard)
        {
            case FIXED -> {}
            case ROTATE_XYZ -> mat.rotation(cameraRot.invert());
            case ROTATE_Y -> mat.rotateY(camera.yaw());
            case LOOKAT_XYZ -> mat.billboardSpherical(position, cameraPos);
            case LOOKAT_Y -> mat.billboardCylindrical(position, cameraPos, FlareConstants.CAMERA_UP);
            case LOOKAT_DIRECTION -> {
                Velocity velocity = ParticleComponents.VELOCITY.get(entity);
                if (velocity != null)
                {
                    Vector3f vec = new Vector3f(velocity.x, velocity.y, velocity.z);
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
            }
            default -> throw new IllegalArgumentException("Unsupported billboard mode: " + billboard.billboard);
        }

        // Apply translation
        mat.m30(position.x);
        mat.m31(position.y);
        mat.m32(position.z);

        return mat;
    }

    public static void applySpin(Matrix4f billboardMat, float angleRadians)
    {
        Matrix4f spin = new Matrix4f().rotate(angleRadians, 0, 0, 1);
        billboardMat.mul(spin);
    }
}