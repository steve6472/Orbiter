package steve6472.orbiter.world;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.*;
import com.jme3.bullet.collision.shapes.infos.ChildCollisionShape;
import com.jme3.bullet.joints.Constraint;
import com.jme3.bullet.joints.PhysicsJoint;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import steve6472.orbiter.Convert;

import static steve6472.flare.render.debug.DebugRender.*;

/**
 * Created by steve6472
 * Date: 10/11/2024
 * Project: Orbiter <br>
 */
public class PhysicsRenderer
{
    public static final boolean ENABLE_CHARACTERS = false;
    public static final boolean ENABLE_RIGIDBODY = true;
    public static final boolean ENABLE_GHOSTS = false;
    public static final boolean ENABLE_JOINTS = true;

    public static final boolean ENABLE_CENTER = true;

    public static void render(PhysicsSpace space)
    {
        if (ENABLE_RIGIDBODY)
            space.getRigidBodyList().forEach(PhysicsRenderer::renderBody);

        if (ENABLE_GHOSTS)
            space.getGhostObjectList().forEach(PhysicsRenderer::renderGhost);

        if (ENABLE_CHARACTERS)
            space.getCharacterList().forEach(PhysicsRenderer::renderCharacter);

        if (ENABLE_JOINTS)
            space.getJointList().forEach(PhysicsRenderer::renderJoint);
    }

    private static void renderBody(PhysicsRigidBody body)
    {
        CollisionShape collisionShape = body.getCollisionShape();
        Matrix4f matrix4f = Convert.physGetTransformToJoml(body, new Matrix4f());

        if (ENABLE_CENTER)
            renderCenter(matrix4f);

        renderShape(collisionShape, matrix4f);
    }

    private static void renderGhost(PhysicsGhostObject body)
    {
        CollisionShape collisionShape = body.getCollisionShape();
        Matrix4f matrix4f = Convert.physGetTransformToJoml(body, new Matrix4f());

        matrix4f.scale(Convert.physGetToJoml(collisionShape::getScale));

        if (ENABLE_CENTER)
            renderCenter(matrix4f);

        renderShape(collisionShape, matrix4f);
    }

    private static void renderCharacter(PhysicsCharacter body)
    {
        CollisionShape collisionShape = body.getCollisionShape();
        Matrix4f matrix4f = Convert.physGetTransformToJoml(body, new Matrix4f());

        if (ENABLE_CENTER)
            renderCenter(matrix4f);

        renderShape(collisionShape, matrix4f);
    }

    private static void renderJoint(PhysicsJoint joint)
    {
        if (joint instanceof Constraint constraint)
        {
            Vector3f bodyALoc = Convert.physGetToJoml(constraint.getBodyA()::getPhysicsLocation);
            Vector3f bodyBLoc = Convert.physGetToJoml(constraint.getBodyB()::getPhysicsLocation);
            Vector3f bodyAPivot = Convert.physGetToJoml(constraint::getPivotA);

            Quaternionf bodyARot = Convert.physGetToJomlQuat(constraint.getBodyA()::getPhysicsRotation);
            Quaternionf bodyBRot = Convert.physGetToJomlQuat(constraint.getBodyB()::getPhysicsRotation);
            Vector3f bodyBPivot = Convert.physGetToJoml(constraint::getPivotB);

            addDebugObjectForFrame(lineCube(new Vector3f(), 0.005f, CHOCOLATE), new Matrix4f().translate(bodyALoc).rotate(bodyARot).translate(bodyAPivot));
            addDebugObjectForFrame(lineCube(new Vector3f(), 0.005f, WHITE), new Matrix4f().translate(bodyBLoc).rotate(bodyBRot).translate(bodyBPivot));
        }
    }

    private static void renderShape(CollisionShape shape, Matrix4f bodyTransform)
    {
        if (shape instanceof BoxCollisionShape box)
        {
            renderBox(box, bodyTransform);
        } else if (shape instanceof CapsuleCollisionShape shap)
        {
            renderCapsule(shap, bodyTransform);
        } else if (shape instanceof SphereCollisionShape shap)
        {
            renderSphere(shap, bodyTransform);
        } else if (shape instanceof CompoundCollisionShape shap)
        {
            for (ChildCollisionShape childCollisionShape : shap.listChildren())
            {
                CollisionShape shape1 = childCollisionShape.getShape();
                var offset = Convert.jomlToPhys(new Vector3f());
                childCollisionShape.copyOffset(offset);
                renderShape(shape1, new Matrix4f(bodyTransform).translate(offset.x, offset.y, offset.z));
            }
        }
    }

    private static void renderBox(BoxCollisionShape shape, Matrix4f bodyTransform)
    {
        Vector3f halfSizes = Convert.physGetToJoml(shape::getHalfExtents);

        addDebugObjectForFrame(lineCube(new Vector3f(), halfSizes.x, halfSizes.y, halfSizes.z, MINT), bodyTransform);
    }

    private static void renderCapsule(CapsuleCollisionShape shape, Matrix4f bodyTransform)
    {
        float radius = shape.getRadius();
        float height = shape.getHeight();
        int quality = 13;

        addDebugObjectForFrame(lineCapsule(height, radius, quality, KHAKI), bodyTransform);
    }

    private static void renderSphere(SphereCollisionShape shape, Matrix4f bodyTransform)
    {
        float radius = shape.getRadius();
        int quality = 13;

        addDebugObjectForFrame(lineSphere(radius, quality, KHAKI), bodyTransform);
    }

    private static void renderCenter(Matrix4f bodyTransform)
    {
        addDebugObjectForFrame(lineCube(new Vector3f(), 0.05f, DARK_VIOLET), bodyTransform);
    }
}
