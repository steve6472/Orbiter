package steve6472.orbiter.world.ecs.blueprints;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.flare.registry.FlareRegistries;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.components.physics.*;
import steve6472.orbiter.world.ecs.core.Blueprint;

import java.util.List;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public record PhysicsBodyBlueprint(Key model, Key collision, float mass) implements Blueprint<PhysicsBodyBlueprint>
{
    private static final Key FROM_MODEL = Key.withNamespace("from", "model");

    public static final Key KEY = Key.defaultNamespace("physics_body");
    public static final Codec<PhysicsBodyBlueprint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Key.CODEC.fieldOf("model").forGetter(PhysicsBodyBlueprint::model),
        Key.CODEC.optionalFieldOf("collision", FROM_MODEL).forGetter(PhysicsBodyBlueprint::collision),
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
            new Collision(collision.equals(FROM_MODEL) ? model : collision),
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
