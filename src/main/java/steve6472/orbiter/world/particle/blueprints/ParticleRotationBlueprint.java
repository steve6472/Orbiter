package steve6472.orbiter.world.particle.blueprints;

import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.orlang.codec.OrNumValue;
import steve6472.orbiter.world.particle.components.Rotation;
import steve6472.orbiter.world.particle.core.PCBlueprint;
import steve6472.orbiter.world.particle.core.ParticleComponent;

import java.util.Optional;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleRotationBlueprint(OrNumValue initial, Optional<OrNumValue> rate, Optional<OrNumValue> acceleration, Optional<OrNumValue> dragCoefficient) implements PCBlueprint<ParticleRotationBlueprint>
{
    public static final Key KEY = Constants.key("rotation");
    public static final Codec<ParticleRotationBlueprint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        OrNumValue.CODEC.fieldOf("initial").forGetter(ParticleRotationBlueprint::initial),
        OrNumValue.CODEC.optionalFieldOf("rate").forGetter(ParticleRotationBlueprint::rate),
        OrNumValue.CODEC.optionalFieldOf("acceleration").forGetter(ParticleRotationBlueprint::acceleration),
        OrNumValue.CODEC.optionalFieldOf("drag_coefficient").forGetter(ParticleRotationBlueprint::dragCoefficient)
    ).apply(instance, ParticleRotationBlueprint::new));

    @Override
    public ParticleComponent create(PooledEngine particleEngine, OrlangEnvironment environment)
    {
        Rotation component = particleEngine.createComponent(Rotation.class);
        component.rotation = (float) initial.evaluateAndGet(environment);
        rate.ifPresent(o -> component.rate = (float) o.evaluateAndGet(environment));
        acceleration.ifPresent(o -> component.acceleration = o);
        dragCoefficient.ifPresent(o -> component.dragCoefficient = o);
        return component;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<ParticleRotationBlueprint> codec()
    {
        return CODEC;
    }
}
