package steve6472.orbiter.world.ecs;

import com.badlogic.ashley.core.Component;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.ecs.components.*;
import steve6472.orbiter.world.ecs.components.physics.*;
import steve6472.orbiter.world.ecs.core.ComponentEntry;

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
    public static final ComponentEntry<Position> POSITION = register("position", Position.class, builder -> builder.persistent(Position.CODEC).network(Position.BUFFER_CODEC));
    public static final ComponentEntry<Rotation> ROTATION = register("rotation", Rotation.class, builder -> builder.persistent(Rotation.CODEC).network(Rotation.BUFFER_CODEC));
    public static final ComponentEntry<Gravity> GRAVITY = register("gravity", Gravity.class, builder -> builder.persistent(Gravity.CODEC).network(Gravity.BUFFER_CODEC));
    public static final ComponentEntry<AngularVelocity> ANGULAR_VELOCITY = register("angular_velocity", AngularVelocity.class, builder -> builder.persistent(AngularVelocity.CODEC).network(AngularVelocity.BUFFER_CODEC));
    public static final ComponentEntry<LinearVelocity> LINEAR_VELOCITY = register("linear_velocity", LinearVelocity.class, builder -> builder.persistent(LinearVelocity.CODEC).network(LinearVelocity.BUFFER_CODEC));
    public static final ComponentEntry<AngularFactor> ANGULAR_FACTOR = register("angular_factor", AngularFactor.class, builder -> builder.persistent(AngularFactor.CODEC).network(AngularFactor.BUFFER_CODEC));
    public static final ComponentEntry<LinearFactor> LINEAR_FACTOR = register("linear_factor", LinearFactor.class, builder -> builder.persistent(LinearFactor.CODEC).network(LinearFactor.BUFFER_CODEC));
    public static final ComponentEntry<AngularDamping> ANGULAR_DAMPING = register("angular_damping", AngularDamping.class, builder -> builder.persistent(AngularDamping.CODEC).network(AngularDamping.BUFFER_CODEC));
    public static final ComponentEntry<LinearDamping> LINEAR_DAMPING = register("linear_damping", LinearDamping.class, builder -> builder.persistent(LinearDamping.CODEC).network(LinearDamping.BUFFER_CODEC));
    public static final ComponentEntry<Friction> FRICTION = register("friction", Friction.class, builder -> builder.persistent(Friction.CODEC).network(Friction.BUFFER_CODEC));
    public static final ComponentEntry<Mass> MASS = register("mass", Mass.class, builder -> builder.persistent(Mass.CODEC).network(Mass.BUFFER_CODEC));
    public static final ComponentEntry<Collision> COLLISION = register("collision", Collision.class, builder -> builder.persistent(Collision.CODEC).network(Collision.BUFFER_CODEC));

    /*
     * Rendering
     */
    public static final ComponentEntry<IndexModel> MODEL = register("model", IndexModel.class, builder -> builder.persistent(IndexModel.CODEC).network(IndexModel.BUFFER_CODEC));

    /*
     * MP Specific
     */
    public static final ComponentEntry<MPControlled> MP_CONTROLLED = register("mp_controlled", MPControlled.class, builder -> builder.persistent(MPControlled.CODEC).network(MPControlled.BUFFER_CODEC));

    /*
     * Tags
     */
    public static final ComponentEntry<Tag.Physics> TAG_PHYSICS = register("tag_physics", Tag.Physics.class, builder -> builder.persistent(Tag.PHYSICS.codec()).network(Tag.PHYSICS.networkCodec()));
    public static final ComponentEntry<Tag.FireflyAI> TAG_FIREFLY_AI = register("tag_firefly_ai", Tag.FireflyAI.class, builder -> builder.persistent(Tag.FIREFLY_AI.codec()).network(Tag.FIREFLY_AI.networkCodec()));
    public static final ComponentEntry<Tag.ClientHandled> TAG_CLIENT_HANDLED = register("tag_client_handled", Tag.ClientHandled.class, builder -> builder.persistent(Tag.CLIENT_HANDLED.codec()).network(Tag.CLIENT_HANDLED.networkCodec()));

    /*
     * Internal
     */
    public static final ComponentEntry<UUIDComp> UUID = register("uuid", UUIDComp.class, builder -> builder.persistent(UUIDComp.CODEC));
    public static final ComponentEntry<NetworkUpdates> NETWORK_UPDATES = register("network_updates", NetworkUpdates.class, builder -> builder);
    public static final ComponentEntry<NetworkRemove> NETWORK_REMOVE = register("network_remove", NetworkRemove.class, builder -> builder);
    public static final ComponentEntry<NetworkAdd> NETWORK_ADD = register("network_add", NetworkAdd.class, builder -> builder);

    /*
     * Register functions
     */

    private static <T extends Component> ComponentEntry<T> register(String id, Class<T> clazz, UnaryOperator<ComponentEntry.Builder<T>> builder)
    {
        return register(Key.withNamespace(Constants.NAMESPACE, id), clazz, builder);
    }

    private static <T extends Component> ComponentEntry<T> register(Key key, Class<T> clazz, UnaryOperator<ComponentEntry.Builder<T>> builder)
    {
        var entryBuilder = builder.apply(ComponentEntry.builder())._key(key).clazz(clazz);
        ComponentEntry<T> build = entryBuilder.build();
        if (Registries.COMPONENT.get(key) != null)
            throw new RuntimeException("Component with key " + key + " already exists!");
        Registries.COMPONENT.register(key, build);
        return build;
    }

    public static <T extends Component> Optional<ComponentEntry<T>> getComponentByClass(Class<T> clazz)
    {
        for (ComponentEntry<?> value : Registries.COMPONENT.getMap().values())
        {
            if (value.componentClass().equals(clazz))
                return Optional.of((ComponentEntry<T>) value);
        }
        return Optional.empty();
    }
}
