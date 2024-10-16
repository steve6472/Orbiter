package steve6472.orbiter.world.ecs.blueprints;

import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.core.Blueprint;

import java.util.Collection;
import java.util.List;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public class PositionBlueprint implements Blueprint<PositionBlueprint>
{
    public static final Key KEY = Key.defaultNamespace("position");
    private static final PositionBlueprint INSTANCE = new PositionBlueprint();
    public static final Codec<PositionBlueprint> CODEC = Codec.unit(INSTANCE);

    private PositionBlueprint() {}

    @Override
    public List<?> createComponents()
    {
        return List.of(new Position());
    }

    @Override
    public Codec<PositionBlueprint> codec()
    {
        return CODEC;
    }

    @Override
    public Key key()
    {
        return KEY;
    }
}
