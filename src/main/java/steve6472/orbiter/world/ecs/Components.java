package steve6472.orbiter.world.ecs;

import steve6472.core.registry.Key;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.ecs.components.MPControlled;
import steve6472.orbiter.world.ecs.components.Position;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.core.Component;

import java.util.function.UnaryOperator;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public class Components
{
    /*
     * Physics/position
     */
    public static final Component<Position> POSITION = register("position", Position.class, builder -> builder.persistent(Position.CODEC).network(Position.BUFFER_CODEC));

    /*
     * Rendering
     */
    public static final Component<IndexModel> MODEL = register("model", IndexModel.class, builder -> builder.persistent(IndexModel.CODEC).network(IndexModel.BUFFER_CODEC));

    /*
     * MP Specific
     */
    public static final Component<MPControlled> MP_CONTROLLED = register("mp_controlled", MPControlled.class, builder -> builder.persistent(MPControlled.CODEC).network(MPControlled.BUFFER_CODEC));

    /*
     * Tags
     */
    public static final Component<Tag.Physics> TAG_PHYSICS = register("tag_physics", Tag.Physics.class, builder -> builder.persistent(Tag.PHYSICS.codec()).network(Tag.PHYSICS.networkCodec()));
    public static final Component<Tag.FireflyAI> TAG_FIREFLY_AI = register("tag_firefly_ai", Tag.FireflyAI.class, builder -> builder.persistent(Tag.FIREFLY_AI.codec()).network(Tag.FIREFLY_AI.networkCodec()));

    /*
     * Register functions
     */

    private static <T> Component<T> register(String id, Class<T> clazz, UnaryOperator<Component.Builder<T>> builder)
    {
        return register(Key.defaultNamespace(id), clazz, builder);
    }

    private static <T> Component<T> register(Key key, Class<T> clazz, UnaryOperator<Component.Builder<T>> builder)
    {
        var builder_ = builder.apply(Component.builder())._key(key);
        builder_.clazz(clazz);
        Component<T> build = builder_.build();
        Registries.COMPONENT.register(key, build);
        return build;
    }
}
