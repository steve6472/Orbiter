package steve6472.orbiter.world.ecs.blueprints;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.flare.registry.FlareRegistries;
import steve6472.orbiter.Constants;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.components.physics.*;
import steve6472.orbiter.world.ecs.core.Blueprint;

import java.util.List;
import java.util.Optional;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public record PhysicsBodyBlueprint(Key model, Optional<Key> collision, float mass) implements Blueprint<PhysicsBodyBlueprint>
{
    public static final Key KEY = Constants.key("physics_body");
    public static final Codec<PhysicsBodyBlueprint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Key.CODEC.fieldOf("model").forGetter(PhysicsBodyBlueprint::model),
        Key.CODEC.optionalFieldOf("collision").forGetter(PhysicsBodyBlueprint::collision),
        Codec.FLOAT.optionalFieldOf("mass", 1f).forGetter(PhysicsBodyBlueprint::mass)
    ).apply(instance, PhysicsBodyBlueprint::new));

    @Override
    public List<Component> createComponents()
    {
        return List.of(
            new Position(),
            new Rotation(),
            new AngularVelocity(),
            new LinearVelocity(),
            new AngularFactor(),
            new LinearFactor(),
            new AngularDamping(0.2f),
            new LinearDamping(0.2f),
            new Friction(),
            new Mass(mass),
            new IndexModel(FlareRegistries.STATIC_MODEL.get(model)),
            new Collision(collision.orElse(model)),
            Tag.PHYSICS
        );
    }

    @Override
    public Codec<PhysicsBodyBlueprint> codec()
    {
        return CODEC;
    }

    @Override
    public Key key()
    {
        return KEY;
    }
}
