package steve6472.orbiter.world.ecs;

import steve6472.core.registry.Key;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.ecs.components.*;
import steve6472.orbiter.world.ecs.core.Component;

import java.util.Optional;
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
     * Internal
     */
    public static final Component<NetworkUpdates> NETWORK_UPDATES = register("network_updates", NetworkUpdates.class, builder -> builder);
    public static final Component<NetworkRemove> NETWORK_REMOVE = register("network_remove", NetworkRemove.class, builder -> builder);
    public static final Component<NetworkAdd> NETWORK_ADD = register("network_add", NetworkAdd.class, builder -> builder);

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
        if (Registries.COMPONENT.get(key) != null)
            throw new RuntimeException("Compoent with key " + key + " already exists!");
        Registries.COMPONENT.register(key, build);
        return build;
    }

    public static <T> Optional<Component<T>> getComponentByClass(Class<T> clazz)
    {
        for (Component<?> value : Registries.COMPONENT.getMap().values())
        {
            if (value.componentClass().equals(clazz))
                return Optional.of((Component<T>) value);
        }
        return Optional.empty();
    }
}
