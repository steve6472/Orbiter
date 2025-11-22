package steve6472.orbiter.world.ecs.blueprints;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.world.ecs.core.Blueprint;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 11/22/2025
 * Project: Orbiter <br>
 */
public final class PrimitiveBlueprint<B extends Component> implements Blueprint<PrimitiveBlueprint<B>>
{
    private final Supplier<B> constructor;
    private final Key key;
    private final Codec<PrimitiveBlueprint<B>> codec;

    public PrimitiveBlueprint(Key key, Supplier<B> constructor)
    {
        this.key = key;
        this.constructor = constructor;
        this.codec = Codec.unit(() -> this);
    }

    @Override
    public List<Component> createComponents()
    {
        return List.of(constructor.get());
    }

    @Override
    public Key key()
    {
        return key;
    }

    @Override
    public Codec<PrimitiveBlueprint<B>> codec()
    {
        return codec;
    }
}
