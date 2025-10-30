package steve6472.orbiter.rendering;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;
import steve6472.core.registry.StringValue;
import steve6472.flare.pipeline.builder.PipelineConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by steve6472
 * Date: 10/28/2025
 * Project: Orbiter <br>
 */
public record ParticleMaterial(Settings renderSettings, PipelineConstructor pipeline)
{
    private static final List<ParticleMaterial> MATERIALS = new ArrayList<>(6);

    public static final ParticleMaterial OPAQUE = new ParticleMaterial(new Settings(Transparency.OPAQUE, false, false), OrbiterPipelines.PLANE_ALPHA_TEST.apply(false));
    public static final ParticleMaterial OPAQUE_TINT = new ParticleMaterial(new Settings(Transparency.OPAQUE, false, true), OrbiterPipelines.PLANE_ALPHA_TEST.apply(true));
    public static final ParticleMaterial ALPHA_TEST = new ParticleMaterial(new Settings(Transparency.ALPHA_TEST, false, false), OrbiterPipelines.PLANE_ALPHA_TEST.apply(false));
    public static final ParticleMaterial ALPHA_TEST_TINT = new ParticleMaterial(new Settings(Transparency.ALPHA_TEST, false, true), OrbiterPipelines.PLANE_ALPHA_TEST.apply(true));

    public static final ParticleMaterial BLEND = new ParticleMaterial(new Settings(Transparency.BLEND, false, true), OrbiterPipelines.PLANE);
    public static final ParticleMaterial ADDITIVE = new ParticleMaterial(new Settings(Transparency.ADDITIVE, false, true), OrbiterPipelines.PLANE_ADDITIVE);

    public ParticleMaterial
    {
        MATERIALS.add(this);
    }

    public static @Nullable ParticleMaterial fromSettings(Settings settings)
    {
        for (ParticleMaterial material : MATERIALS)
        {
            if (material.renderSettings.equals(settings))
                return material;
        }

        return null;
    }

    public enum Transparency implements StringValue
    {
        OPAQUE(false),
        ALPHA_TEST(false),
        BLEND(true),
        ADDITIVE(true);

        public static final Codec<Transparency> CODEC = StringValue.fromValues(Transparency::values);

        public final boolean sorted;

        Transparency(boolean sorted)
        {
            this.sorted = sorted;
        }

        @Override
        public String stringValue()
        {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public record Settings(Transparency transparency, boolean shaded, boolean tinted)
    {
        public static final Codec<Settings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Transparency.CODEC.optionalFieldOf("transparency", Transparency.OPAQUE).forGetter(Settings::transparency),
            Codec.BOOL.optionalFieldOf("shaded", false).forGetter(Settings::shaded),
            Codec.BOOL.optionalFieldOf("tinted", false).forGetter(Settings::tinted)
        ).apply(instance, Settings::new));
    }
}
