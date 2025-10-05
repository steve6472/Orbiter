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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public record PhysicsBodyBlueprint(Optional<Key> model, Optional<Key> collision, float mass) implements Blueprint<PhysicsBodyBlueprint>
{
    public static final Key KEY = Constants.key("physics_body");
    public static final Codec<PhysicsBodyBlueprint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Constants.KEY_CODEC.optionalFieldOf("model").forGetter(PhysicsBodyBlueprint::model),
        Constants.KEY_CODEC.optionalFieldOf("collision").forGetter(PhysicsBodyBlueprint::collision),
        Codec.FLOAT.optionalFieldOf("mass", 1f).forGetter(PhysicsBodyBlueprint::mass)
    ).apply(instance, PhysicsBodyBlueprint::new));

    @Override
    public List<Component> createComponents()
    {
        List<Component> components = new ArrayList<>();
        components.add(new Position());
        components.add(new Rotation());
        components.add(new AngularVelocity());
        components.add(new LinearVelocity());
        components.add(new Friction());
        components.add(Tag.PHYSICS);

        if (model.isPresent())
        {
            components.add(new IndexModel(FlareRegistries.STATIC_MODEL.get(model.get())));
            components.add(new Collision(collision.orElse(model.get())));
        } else
        {
            if (collision.isEmpty())
                throw new RuntimeException("Body without collision. Specify 'collision' and/or 'model'");

            components.add(new Collision(collision.get()));
        }
        return components;
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
