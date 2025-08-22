package steve6472.orbiter.world.ecs.blueprints;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.world.ecs.components.physics.Collision;
import steve6472.orbiter.world.ecs.core.Blueprint;

import java.util.List;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public record CollisionBlueprint(Key key) implements Blueprint<CollisionBlueprint>
{
    public static final Key KEY = Key.defaultNamespace("collision");
    public static final Codec<CollisionBlueprint> CODEC = Key.CODEC.xmap(CollisionBlueprint::new, CollisionBlueprint::key);

    @Override
    public List<Component> createComponents()
    {
        return List.of(new Collision(key));
    }

    @Override
    public Codec<CollisionBlueprint> codec()
    {
        return CODEC;
    }

    @Override
    public Key key()
    {
        return KEY;
    }
}
