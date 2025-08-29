package steve6472.orbiter.world.particle;

import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.particle.blueprints.ParticleLocalSpaceBlueprint;
import steve6472.orbiter.world.particle.blueprints.ParticleMaxAgeBlueprint;
import steve6472.orbiter.world.particle.blueprints.ParticleModelBlueprint;
import steve6472.orbiter.world.particle.blueprints.ParticleScaleBlueprint;
import steve6472.orbiter.world.particle.core.PCBlueprint;
import steve6472.orbiter.world.particle.core.PCBlueprintEntry;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
@SuppressWarnings("unused")
public class ParticleComponentBlueprints
{
    public static final PCBlueprintEntry<ParticleScaleBlueprint> SCALE = register(ParticleScaleBlueprint.KEY, ParticleScaleBlueprint.CODEC);
    public static final PCBlueprintEntry<ParticleModelBlueprint> MODEL = register(ParticleModelBlueprint.KEY, ParticleModelBlueprint.CODEC);
    public static final PCBlueprintEntry<ParticleMaxAgeBlueprint> MAX_AGE = register(ParticleMaxAgeBlueprint.KEY, ParticleMaxAgeBlueprint.CODEC);
    public static final PCBlueprintEntry<ParticleLocalSpaceBlueprint> LOCAL_SPACE = register(ParticleLocalSpaceBlueprint.KEY, ParticleLocalSpaceBlueprint.CODEC);

    private static <T extends PCBlueprint<?>> PCBlueprintEntry<T> register(Key key, Codec<T> codec)
    {
        if (Registries.PARTICLE_COMPONENT_BLUEPRINT.get(key) != null)
            throw new RuntimeException("Blueprint with key " + key + " already exists!");

        PCBlueprintEntry<T> obj = new PCBlueprintEntry<>(key, codec);
        Registries.PARTICLE_COMPONENT_BLUEPRINT.register(key, obj);
        return obj;
    }
}
