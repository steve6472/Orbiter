package steve6472.orbiter.orlang;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import steve6472.core.util.ColorUtil;
import steve6472.orbiter.orlang.codec.OrNumValue;
import steve6472.orbiter.world.particle.core.ParticleComponent;

import java.util.*;

/**
 * Created by steve6472
 * Date: 9/2/2025
 * Project: Orbiter <br>
 */
public class Gradient implements ParticleComponent
{
    public OrNumValue interpolant;
    public List<Entry> gradient;
    public boolean oklab;

    public static final Codec<Gradient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        OrNumValue.CODEC.fieldOf("interpolant").forGetter(o -> o.interpolant),
        Codec.unboundedMap(Codec.STRING, Entry.CODEC_UNTIMED).xmap(map -> {
            List<Entry> entries = new ArrayList<>(map.size());
            map.forEach((t, e) -> entries.add(e.withTime(Double.parseDouble(t))));
            entries.sort(Comparator.comparingDouble(Entry::time));
            return entries;
        }, entries -> {
            Map<String, Entry> map = new HashMap<>(entries.size());
            for (Entry entry : entries)
            {
                map.put(Double.toString(entry.time), entry);
            }
            return map;
        }).fieldOf("gradient").forGetter(o -> o.gradient),
        Codec.BOOL.optionalFieldOf("oklab", false).forGetter(o -> o.oklab)
    ).apply(instance, Gradient::new));

    public Gradient(OrNumValue interpolant, List<Entry> gradient, boolean oklab)
    {
        this.interpolant = interpolant;
        this.gradient = gradient;
        this.oklab = oklab;
    }

    public Gradient()
    {
    }

    private double mix(double x, double y, double a)
    {
        return x * (1 - a) + y * a;
    }

    // https://bottosson.github.io/posts/oklab
    private static final Matrix3f kCONEtoLMS = new Matrix3f(
        0.4122214708f,  0.2119034982f,  0.0883024619f,
        0.5363325363f,  0.6806995451f,  0.2817188376f,
        0.0514459929f,  0.1073969566f,  0.6299787005f
    );

    private static final Matrix3f kLMStoCONE = new Matrix3f(
        4.0767416621f,  -1.2684380046f, -0.0041960863f,
       -3.3077115913f,   2.6097574011f, -0.7034186147f,
        0.2309699292f,  -0.3413193965f,  1.7076147010f
    );

    private static final double ONE_THIRD = 1.0 / 3.0;

    // https://www.shadertoy.com/view/ttcyRS
    Vector3f oklab_mix(Vector3f colA, Vector3f colB, float h)
    {
        // rgb to cone (arg of pow can't be negative)

        Vector3f mulA = colA.mul(kCONEtoLMS, new Vector3f());
        Vector3f mulB = colB.mul(kCONEtoLMS, new Vector3f());

        double lmsAx = Math.pow(mulA.x, ONE_THIRD);
        double lmsAy = Math.pow(mulA.y, ONE_THIRD);
        double lmsAz = Math.pow(mulA.z, ONE_THIRD);

        double lmsBx = Math.pow(mulB.x, ONE_THIRD);
        double lmsBy = Math.pow(mulB.y, ONE_THIRD);
        double lmsBz = Math.pow(mulB.z, ONE_THIRD);
        // lerp
        Vector3f lms = new Vector3f(
            (float) mix(lmsAx, lmsBx, h),
            (float) mix(lmsAy, lmsBy, h),
            (float) mix(lmsAz, lmsBz, h)
        );
        return lms.mul(lms).mul(lms).mul(kLMStoCONE);
    }

    public void apply(OrlangEnvironment env, Vector4f toTint)
    {
        double t = interpolant.evaluateAndGet(env);
        t = Math.clamp(t, 0, 1);

        // Pick gradient start - end
        Entry start, end;
        int segmentIndex = -1;
        for (int i = 0; i < gradient.size() - 1; i++)
        {
            if (t >= gradient.get(i).time() && t <= gradient.get(i + 1).time())
            {
                segmentIndex = i;
                break;
            }
        }

        if (segmentIndex == -1)
        {
            if (t < gradient.getFirst().time())
            {
                start = gradient.get(0);
                end = gradient.get(1);
            } else
            {
                start = gradient.get(gradient.size() - 2);
                end = gradient.getLast();
            }
        }  else
        {
            start = gradient.get(segmentIndex);
            end = gradient.get(segmentIndex + 1);
        }

        double localT = (t - start.time()) / (end.time() - start.time());
        if (oklab)
        {
            Vector3f v = oklab_mix(new Vector3f(start.r, start.g, start.b), new Vector3f(end.r, end.g, end.b), (float) localT);
            toTint.set(v.x, v.y, v.z, mix(start.a, end.a, localT));
        } else
        {
            toTint.set(
                mix(start.r, end.r, localT),
                mix(start.g, end.g, localT),
                mix(start.b, end.b, localT),
                mix(start.a, end.a, localT)
            );
        }
    }

    @Override
    public void reset()
    {
        interpolant = null;
        gradient = null;
        oklab = false;
    }

    public void setFrom(Gradient gradient)
    {
        this.interpolant = gradient.interpolant;
        this.gradient = gradient.gradient;
        this.oklab = gradient.oklab;
    }

    public record Entry(double time, float r, float g, float b, float a)
    {
        public static final Codec<Entry> CODEC_UNTIMED = Codec.STRING.xmap(s -> {
            if (s.startsWith("#"))
                s = s.substring(1);
            int rgba = Integer.parseUnsignedInt(s, 16);
            float[] colors = ColorUtil.getColors(rgba);
            return new Entry(0, colors[0], colors[1], colors[2], colors[3]);
        }, e -> {
            int color = ColorUtil.getColor(e.r, e.g, e.b, e.a);
            return "#" + Integer.toHexString(color);
        });

        public Entry withTime(double time)
        {
            return new Entry(time, r, g, b, a);
        }
    }
}
