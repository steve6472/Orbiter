package steve6472.orbiter.world.particle.blueprints;

import com.badlogic.ashley.core.PooledEngine;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.orlang.Gradient;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.orlang.codec.OrNumValue;
import steve6472.orbiter.world.particle.components.TintRGBA;
import steve6472.orbiter.world.particle.core.PCBlueprint;
import steve6472.orbiter.world.particle.core.ParticleComponent;

import java.util.Optional;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleTintBlueprint(Either<TintRGBA, Gradient> tint) implements PCBlueprint<ParticleTintBlueprint>
{
    public static final Key KEY = Constants.key("tint");
    public static final Codec<ParticleTintBlueprint> CODEC = Codec.either(TintRGBA.CODEC, Gradient.CODEC).xmap(ParticleTintBlueprint::new, ParticleTintBlueprint::tint);

    @Override
    public ParticleComponent create(PooledEngine particleEngine, OrlangEnvironment environment)
    {
        Optional<TintRGBA> left = tint.left();
        Optional<Gradient> right = tint.right();
        if (left.isPresent())
        {
            TintRGBA component = particleEngine.createComponent(TintRGBA.class);
            component.setFrom(left.get());
            return component;
        }
        if (right.isPresent())
        {
            Gradient component = particleEngine.createComponent(Gradient.class);
            component.setFrom(right.get());
            return component;
        }
        throw new RuntimeException("Only color rgba is supported for now");
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<ParticleTintBlueprint> codec()
    {
        return CODEC;
    }
}
