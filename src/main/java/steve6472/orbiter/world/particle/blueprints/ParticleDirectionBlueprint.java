package steve6472.orbiter.world.particle.blueprints;

import com.badlogic.ashley.core.PooledEngine;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import org.joml.Vector3f;
import steve6472.core.registry.Key;
import steve6472.core.registry.StringValue;
import steve6472.orbiter.Constants;
import steve6472.orbiter.world.particle.components.Velocity;
import steve6472.orbiter.world.particle.core.PCBlueprint;
import steve6472.orbiter.world.particle.core.ParticleComponent;
import steve6472.orlang.OrlangEnvironment;
import steve6472.orlang.codec.OrVec3;

import java.util.Locale;
import java.util.Optional;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleDirectionBlueprint(Either<DirectionEnum, OrVec3> direction) implements PCBlueprint<ParticleDirectionBlueprint>
{
    public static final Key KEY = Constants.key("direction");
    public static final Codec<ParticleDirectionBlueprint> CODEC = Codec.either(DirectionEnum.CODEC, OrVec3.CODEC).xmap(ParticleDirectionBlueprint::new, ParticleDirectionBlueprint::direction);

    @Override
    public ParticleComponent create(PooledEngine particleEngine, OrlangEnvironment environment)
    {
        throw new UnsupportedOperationException("Velocity is special case");
    }

    public void direction(Velocity velocity, float initialSpeed, Vector3f position, OrlangEnvironment environment)
    {
        Optional<DirectionEnum> left = direction.left();
        if (left.isPresent())
        {
            DirectionEnum directionEnum = left.get();
            switch (directionEnum)
            {
                case INWARDS ->
                {
                    velocity.set(position.x, position.y, position.z, -initialSpeed);
                }
                case OUTWARDS ->
                {
                    velocity.set(position.x, position.y, position.z, initialSpeed);
                }
                default -> throw new IllegalStateException("Unexpected value: " + directionEnum);
            }
        } else if (direction.right().isPresent())
        {
            OrVec3 orVec3 = direction.right().get();
            orVec3.evaluate(environment);
            velocity.set(orVec3.fx(), orVec3.fy(), orVec3.fz(), initialSpeed);
        } else
        {
            throw new IllegalStateException("Neither left nor right is present");
        }
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<ParticleDirectionBlueprint> codec()
    {
        return CODEC;
    }

    public enum DirectionEnum implements StringValue
    {
        INWARDS, OUTWARDS;

        public static final Codec<DirectionEnum> CODEC = StringValue.fromValues(DirectionEnum::values);

        @Override
        public String stringValue()
        {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
