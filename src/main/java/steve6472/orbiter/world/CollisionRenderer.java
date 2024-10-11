package steve6472.orbiter.world;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import steve6472.orbiter.Convert;
import steve6472.volkaniums.render.debug.objects.Capsule;

import static steve6472.volkaniums.render.debug.DebugRender.*;

/**
 * Created by steve6472
 * Date: 10/11/2024
 * Project: Orbiter <br>
 */
public class CollisionRenderer
{
    public static final boolean ENABLE_CHARACTERS = false;
    public static final boolean ENABLE_GHOSTS = false;

    public static void render(PhysicsSpace space)
    {
        space.getRigidBodyList().forEach(CollisionRenderer::renderBody);

        if (ENABLE_GHOSTS)
            space.getGhostObjectList().forEach(CollisionRenderer::renderGhost);

        if (ENABLE_CHARACTERS)
            space.getCharacterList().forEach(CollisionRenderer::renderCharacter);
    }

    private static void renderBody(PhysicsRigidBody body)
    {
        CollisionShape collisionShape = body.getCollisionShape();
        Matrix4f matrix4f = Convert.physGetTransformToJoml(body, new Matrix4f());

        renderShape(collisionShape, matrix4f);
    }

    private static void renderGhost(PhysicsGhostObject body)
    {
        CollisionShape collisionShape = body.getCollisionShape();
        Matrix4f matrix4f = Convert.physGetTransformToJoml(body, new Matrix4f());

        matrix4f.scale(Convert.physGetToJoml(collisionShape::getScale));

        renderShape(collisionShape, matrix4f);
    }

    private static void renderCharacter(PhysicsCharacter body)
    {
        CollisionShape collisionShape = body.getCollisionShape();
        Matrix4f matrix4f = Convert.physGetTransformToJoml(body, new Matrix4f());

        renderShape(collisionShape, matrix4f);
    }

    private static void renderShape(CollisionShape shape, Matrix4f bodyTransform)
    {
        if (shape instanceof BoxCollisionShape box)
        {
            renderBox(box, bodyTransform);
        } else if (shape instanceof CapsuleCollisionShape capsule)
        {
            renderCapsule(capsule, bodyTransform);
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

        addDebugObjectForFrame(new Capsule(radius, height, quality, KHAKI), bodyTransform);
    }
}
