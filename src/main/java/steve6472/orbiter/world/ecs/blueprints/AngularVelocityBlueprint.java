package steve6472.orbiter.world.ecs.blueprints;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.world.ecs.components.physics.AngularFactor;
import steve6472.orbiter.world.ecs.components.physics.AngularVelocity;
import steve6472.orbiter.world.ecs.core.Blueprint;

import java.util.List;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public record AngularVelocityBlueprint(float x, float y, float z) implements Blueprint<AngularVelocityBlueprint>
{
    public static final Key KEY = Constants.key("angular_velocity");
    public static final Codec<AngularVelocityBlueprint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.optionalFieldOf("x", 0f).forGetter(AngularVelocityBlueprint::x),
        Codec.FLOAT.optionalFieldOf("y", 0f).forGetter(AngularVelocityBlueprint::y),
        Codec.FLOAT.optionalFieldOf("z", 0f).forGetter(AngularVelocityBlueprint::z)
    ).apply(instance, AngularVelocityBlueprint::new));

    @Override
    public List<Component> createComponents()
    {
        return List.of(new AngularVelocity(x, y, z));
    }

    @Override
    public List<Component> createParticleComponents(PooledEngine particleEngine)
    {
        AngularVelocity component = particleEngine.createComponent(AngularVelocity.class);
        component.set(x, y, z);
        return List.of(component);
    }

    @Override
    public Codec<AngularVelocityBlueprint> codec()
    {
        return CODEC;
    }

    @Override
    public Key key()
    {
        return KEY;
    }
}
