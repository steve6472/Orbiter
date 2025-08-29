package steve6472.orbiter.world.particle.blueprints;

import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.flare.assets.model.Model;
import steve6472.flare.registry.FlareRegistries;
import steve6472.orbiter.Constants;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.util.Holder;
import steve6472.orbiter.world.particle.components.ParticleModel;
import steve6472.orbiter.world.particle.core.PCBlueprint;
import steve6472.orbiter.world.particle.core.ParticleComponent;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleModelBlueprint(Holder<Model> model) implements PCBlueprint<ParticleModelBlueprint>
{
    public static final Key KEY = Constants.key("model");
    public static final Codec<ParticleModelBlueprint> CODEC = Holder.create(FlareRegistries.STATIC_MODEL).xmap(ParticleModelBlueprint::new, ParticleModelBlueprint::model);

    @Override
    public ParticleComponent create(PooledEngine particleEngine, OrlangEnvironment environment)
    {
        ParticleModel component = particleEngine.createComponent(ParticleModel.class);
        component.model = model.get();
        return component;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<ParticleModelBlueprint> codec()
    {
        return CODEC;
    }
}
