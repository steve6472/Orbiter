package steve6472.orbiter.rendering.particle;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import steve6472.flare.struct.Struct;
import steve6472.flare.struct.def.SBO;
import steve6472.orbiter.rendering.OrbiterSBO;
import steve6472.orbiter.rendering.SBOModelArray;

public class JustTransform implements SBOModelArray.Entry, Transform
{
    private final Matrix4f transform = new Matrix4f();

    @Override
    public void reset()
    {
        transform.identity();
    }

    @Override
    public Object toStruct()
    {
        return transform;
    }

    @Override
    public Matrix4f transform()
    {
        return transform;
    }
}