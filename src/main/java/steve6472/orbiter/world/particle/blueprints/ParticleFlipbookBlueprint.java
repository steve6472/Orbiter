package steve6472.orbiter.world.particle.blueprints;

import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.flare.FlareConstants;
import steve6472.flare.assets.model.Model;
import steve6472.flare.registry.FlareRegistries;
import steve6472.orbiter.Constants;
import steve6472.orbiter.util.Holder;
import steve6472.orbiter.world.particle.components.FlipbookModel;
import steve6472.orbiter.world.particle.components.ParticleModel;
import steve6472.orbiter.world.particle.core.PCBlueprint;
import steve6472.orbiter.world.particle.core.ParticleComponent;
import steve6472.orlang.OrlangEnvironment;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleFlipbookBlueprint(Key texture, Key atlas, boolean stretchToMaxAge) implements PCBlueprint<ParticleFlipbookBlueprint>
{
    public static final Key KEY = Constants.key("flipbook");
    public static final Codec<ParticleFlipbookBlueprint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Constants.KEY_CODEC.fieldOf("texture").forGetter(ParticleFlipbookBlueprint::texture),
        Constants.KEY_CODEC.optionalFieldOf("atlas", FlareConstants.ATLAS_BLOCKBENCH).forGetter(ParticleFlipbookBlueprint::atlas),
        Codec.BOOL.optionalFieldOf("stretch_to_max_age", false).forGetter(ParticleFlipbookBlueprint::stretchToMaxAge)
    ).apply(instance, ParticleFlipbookBlueprint::new));

    @Override
    public ParticleComponent create(PooledEngine particleEngine, OrlangEnvironment environment)
    {
        FlipbookModel component = particleEngine.createComponent(FlipbookModel.class);
        component.setup(texture, atlas, stretchToMaxAge);
        return component;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<ParticleFlipbookBlueprint> codec()
    {
        return CODEC;
    }
}
