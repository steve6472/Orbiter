package steve6472.orbiter.rendering.particle;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import steve6472.orbiter.rendering.OrbiterSBO;
import steve6472.orbiter.rendering.SBOModelArray;

public class TintedTransform implements SBOModelArray.Entry, Transform, Tint
{
    private final Matrix4f transform = new Matrix4f();
    private final Vector4f tint = new Vector4f(1, 1, 1, 1);

    @Override
    public void reset()
    {
        transform.identity();
        tint.set(1, 1, 1, 1);
    }

    @Override
    public Object toStruct()
    {
        return OrbiterSBO.MODEL_TINT_ENTRY.create(transform, tint);
    }

    @Override
    public Matrix4f transform()
    {
        return transform;
    }

    @Override
    public Vector4f tint()
    {
        return tint;
    }
}