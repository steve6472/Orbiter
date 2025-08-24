package steve6472.orbiter.world.ecs.blueprints;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.world.ecs.components.physics.Rotation;
import steve6472.orbiter.world.ecs.core.Blueprint;

import java.util.List;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public class RotationBlueprint implements Blueprint<RotationBlueprint>
{
    public static final Key KEY = Key.defaultNamespace("rotation");
    private static final RotationBlueprint INSTANCE = new RotationBlueprint();
    public static final Codec<RotationBlueprint> CODEC = Codec.unit(INSTANCE);

    private RotationBlueprint() {}

    @Override
    public List<Component> createComponents()
    {
        return List.of(new Rotation());
    }

    @Override
    public Codec<RotationBlueprint> codec()
    {
        return CODEC;
    }

    @Override
    public Key key()
    {
        return KEY;
    }
}
