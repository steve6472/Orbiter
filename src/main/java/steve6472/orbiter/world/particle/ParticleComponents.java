package steve6472.orbiter.world.particle;

import com.badlogic.ashley.core.Component;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Registries;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.world.particle.components.LocalSpace;
import steve6472.orbiter.world.particle.components.MaxAge;
import steve6472.orbiter.world.particle.components.ParticleFollowerId;
import steve6472.orbiter.world.ecs.core.ComponentEntry;
import steve6472.orbiter.world.particle.components.ParticleModel;
import steve6472.orbiter.world.particle.components.Position;
import steve6472.orbiter.world.particle.components.Scale;

import java.util.function.UnaryOperator;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
@SuppressWarnings("unused")
public class ParticleComponents
{
    // With blueprints
    public static final ComponentEntry<Scale> SCALE = register("scale", Scale.class);
    public static final ComponentEntry<MaxAge> MAX_AGE = register("max_age", MaxAge.class);
    public static final ComponentEntry<ParticleModel> MODEL = register("model", ParticleModel.class);
    public static final ComponentEntry<LocalSpace> LOCAL_SPACE = register("local_space", LocalSpace.class);

    // Programmatically
    public static final ComponentEntry<Position> POSITION = register("position", Position.class);
    public static final ComponentEntry<ParticleFollowerId> PARTICLE_FOLLOWER = register("particle_follower", ParticleFollowerId.class);
    public static final ComponentEntry<OrlangEnvironment> PARTICLE_ENVIRONMENT = register("particle_environment", OrlangEnvironment.class);

    /*
     * Register functions
     */

    private static <T extends Component> ComponentEntry<T> register(String id, Class<T> clazz)
    {
        return register(Key.withNamespace(Constants.NAMESPACE, id), clazz, b -> b);
    }

    private static <T extends Component> ComponentEntry<T> register(Key key, Class<T> clazz, UnaryOperator<ComponentEntry.Builder<T>> builder)
    {
        var entryBuilder = builder.apply(ComponentEntry.builder())._key(key).clazz(clazz);
        ComponentEntry<T> build = entryBuilder.build();
        if (Registries.PARTICLE_COMPONENT.get(key) != null)
            throw new RuntimeException("Component with key " + key + " already exists!");
        Registries.PARTICLE_COMPONENT.register(key, build);
        return build;
    }
}
