package steve6472.orbiter.world.particle.blueprints;

import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector2f;
import org.joml.Vector4f;
import steve6472.core.registry.Key;
import steve6472.core.util.ExtraCodecs;
import steve6472.flare.FlareConstants;
import steve6472.flare.registry.FlareRegistries;
import steve6472.orbiter.Constants;
import steve6472.orbiter.world.particle.components.PlaneModel;
import steve6472.orbiter.world.particle.core.PCBlueprint;
import steve6472.orbiter.world.particle.core.ParticleComponent;
import steve6472.orlang.OrlangEnvironment;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticlePlaneModelBlueprint(Key texture, Vector2f uv, Vector2f uvSize, Vector2f textureSize) implements PCBlueprint<ParticlePlaneModelBlueprint>
{
    public static final Key KEY = Constants.key("plane_model");
    public static final Codec<ParticlePlaneModelBlueprint> CODEC_FULL = RecordCodecBuilder.create(instance -> instance.group(
        Constants.KEY_CODEC.fieldOf("texture").forGetter(ParticlePlaneModelBlueprint::texture),
        ExtraCodecs.VEC_2F.optionalFieldOf("uv", new Vector2f(0, 0)).forGetter(ParticlePlaneModelBlueprint::uv),
        ExtraCodecs.VEC_2F.optionalFieldOf("uv_size", new Vector2f(1, 1)).forGetter(ParticlePlaneModelBlueprint::uvSize),
        ExtraCodecs.VEC_2F.optionalFieldOf("texture_size", new Vector2f(1, 1)).forGetter(ParticlePlaneModelBlueprint::textureSize)
    ).apply(instance, ParticlePlaneModelBlueprint::new));
    public static final Codec<ParticlePlaneModelBlueprint> CODEC_TEXTURE = Constants.KEY_CODEC.xmap(ParticlePlaneModelBlueprint::new, ParticlePlaneModelBlueprint::key);
    public static final Codec<ParticlePlaneModelBlueprint> CODEC = Codec.withAlternative(CODEC_TEXTURE, CODEC_FULL);

    public ParticlePlaneModelBlueprint(Key texture)
    {
        this(texture, new Vector2f(0, 0), new Vector2f(1, 1), new Vector2f(1, 1));
    }

    @Override
    public ParticleComponent create(PooledEngine particleEngine, OrlangEnvironment environment)
    {
        PlaneModel component = particleEngine.createComponent(PlaneModel.class);
        Vector4f uvRect = FlareRegistries.ATLAS.get(Constants.ATLAS_PARTICLE).getSprite(texture).uv();

        float newStartX = mapUV(uv.x, textureSize.x, uvRect.x, uvRect.z);
        float newStartY = mapUV(uv.y, textureSize.y, uvRect.y, uvRect.w);

        float newEndX = mapUV(uv.x + uvSize.x, textureSize.x, uvRect.x, uvRect.z);
        float newEndY = mapUV(uv.y + uvSize.y, textureSize.y, uvRect.y, uvRect.w);

        component.uv.set(newStartX, newStartY, newEndX, newEndY);
        return component;
    }

    private static float mapUV(float value, float inMax, float outMin, float outMax)
    {
        return outMin + (outMax - outMin) * value / inMax;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<ParticlePlaneModelBlueprint> codec()
    {
        return CODEC;
    }
}
