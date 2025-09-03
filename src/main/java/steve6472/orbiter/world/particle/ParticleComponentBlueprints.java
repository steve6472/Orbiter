package steve6472.orbiter.world.particle;

import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.particle.blueprints.*;
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
    public static final PCBlueprintEntry<ParticleBillboardBlueprint> BILLBOARD = register(ParticleBillboardBlueprint.KEY, ParticleBillboardBlueprint.CODEC);
    public static final PCBlueprintEntry<ParticleEnvironmentBlueprint> ENVIRONMENT = register(ParticleEnvironmentBlueprint.KEY, ParticleEnvironmentBlueprint.CODEC);
    public static final PCBlueprintEntry<ParticleDirectionBlueprint> DIRECTION = register(ParticleDirectionBlueprint.KEY, ParticleDirectionBlueprint.CODEC);
    public static final PCBlueprintEntry<ParticleInitialSpeedBlueprint> INITIAL_SPEED = register(ParticleInitialSpeedBlueprint.KEY, ParticleInitialSpeedBlueprint.CODEC);
    public static final PCBlueprintEntry<ParticleLinearAccelerationBlueprint> LINEAR_ACCELERATION = register(ParticleLinearAccelerationBlueprint.KEY, ParticleLinearAccelerationBlueprint.CODEC);
    public static final PCBlueprintEntry<ParticleLinearDragCoefficientBlueprint> LINEAR_DRAG_COEFFICIENT = register(ParticleLinearDragCoefficientBlueprint.KEY, ParticleLinearDragCoefficientBlueprint.CODEC);
    public static final PCBlueprintEntry<ParticleRotationBlueprint> ROTATION = register(ParticleRotationBlueprint.KEY, ParticleRotationBlueprint.CODEC);
    public static final PCBlueprintEntry<ParticlePipelineBlueprint> PIPELINE = register(ParticlePipelineBlueprint.KEY, ParticlePipelineBlueprint.CODEC);
    public static final PCBlueprintEntry<ParticleTintBlueprint> TINT = register(ParticleTintBlueprint.KEY, ParticleTintBlueprint.CODEC);

    private static <T extends PCBlueprint<?>> PCBlueprintEntry<T> register(Key key, Codec<T> codec)
    {
        if (Registries.PARTICLE_COMPONENT_BLUEPRINT.get(key) != null)
            throw new RuntimeException("Blueprint with key " + key + " already exists!");

        PCBlueprintEntry<T> obj = new PCBlueprintEntry<>(key, codec);
        Registries.PARTICLE_COMPONENT_BLUEPRINT.register(key, obj);
        return obj;
    }
}
