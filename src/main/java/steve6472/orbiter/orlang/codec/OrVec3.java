package steve6472.orbiter.orlang.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector3d;
import steve6472.orbiter.orlang.OrlangEnvironment;

import java.util.List;

/**
 * Created by steve6472
 * Date: 8/27/2025
 * Project: Orbiter <br>
 */
public class OrVec3
{
    public static final Codec<OrVec3> CODEC_LIST = OrNumValue.CODEC.listOf().flatXmap(list -> {
        if (list.size() != 3)
            return DataResult.error(() -> "List size incorrect, has to be 3");
        return DataResult.success(new OrVec3(list.get(0), list.get(1), list.get(2)));
    }, vec3 -> DataResult.success(List.of(vec3.x, vec3.y, vec3.z)));

    public static final Codec<OrVec3> CODEC_STRUCT = RecordCodecBuilder.create(instance -> instance.group(
        OrNumValue.CODEC.fieldOf("x").forGetter(v -> v.x),
        OrNumValue.CODEC.fieldOf("y").forGetter(v -> v.y),
        OrNumValue.CODEC.fieldOf("z").forGetter(v -> v.z)
    ).apply(instance, OrVec3::new));

    public static final Codec<OrVec3> CODEC = Codec.withAlternative(CODEC_LIST, CODEC_STRUCT);

    private final OrNumValue x, y, z;

    public OrVec3(OrNumValue x, OrNumValue y, OrNumValue z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public OrVec3(double x, double y, double z)
    {
        this.x = new OrNumValue(x);
        this.y = new OrNumValue(y);
        this.z = new OrNumValue(z);
    }

    public OrVec3()
    {
        this(0, 0, 0);
    }

    public boolean hadFirstEval()
    {
        return x.hadFirstEval() && y.hadFirstEval() && z.hadFirstEval();
    }

    public Vector3d get(Vector3d store)
    {
        return store.set(x.get(), y.get(), z.get());
    }

    public Vector3d get()
    {
        return get(new Vector3d());
    }

    public double x()
    {
        return x.get();
    }

    public double y()
    {
        return y.get();
    }

    public double z()
    {
        return z.get();
    }

    public float fx()
    {
        return x.fget();
    }

    public float fy()
    {
        return y.fget();
    }

    public float fz()
    {
        return z.fget();
    }

    public void evaluate(OrlangEnvironment environment)
    {
        x.evaluate(environment);
        y.evaluate(environment);
        z.evaluate(environment);
    }

    public Vector3d evaluateAndGet(Vector3d store, OrlangEnvironment environment)
    {
        evaluate(environment);
        return get(store);
    }

    public Vector3d evaluateAndGet(OrlangEnvironment environment)
    {
        return evaluateAndGet(new Vector3d(), environment);
    }

    public OrVec3 copy()
    {
        return new OrVec3(x.copy(), y.copy(), z.copy());
    }

    @Override
    public String toString()
    {
        return "OrVec3{" + "x=" + x + "\n, y=" + y + "\n, z=" + z + '}';
    }
}
