package steve6472.orbiter.world.particle.blueprints;

import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.world.particle.components.LocalSpace;
import steve6472.orbiter.world.particle.core.PCBlueprint;
import steve6472.orbiter.world.particle.core.ParticleComponent;
import steve6472.orlang.OrlangEnvironment;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleLocalSpaceBlueprint(boolean position, boolean rotation, boolean velocity) implements PCBlueprint<ParticleLocalSpaceBlueprint>
{
    public static final Key KEY = Constants.key("local_space");
    public static final Codec<ParticleLocalSpaceBlueprint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("position", false).forGetter(ParticleLocalSpaceBlueprint::position),
        Codec.BOOL.optionalFieldOf("rotation", false).forGetter(ParticleLocalSpaceBlueprint::position),
        Codec.BOOL.optionalFieldOf("velocity", false).forGetter(ParticleLocalSpaceBlueprint::position)
    ).apply(instance, ParticleLocalSpaceBlueprint::new));

    @Override
    public ParticleComponent create(PooledEngine particleEngine, OrlangEnvironment environment)
    {
        LocalSpace component = particleEngine.createComponent(LocalSpace.class);
        component.position = position;
        component.rotation = rotation;
        component.velocity = velocity;
        return component;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<ParticleLocalSpaceBlueprint> codec()
    {
        return CODEC;
    }
}
