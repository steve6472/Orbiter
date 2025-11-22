package steve6472.orbiter.world.ecs;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.ecs.blueprints.*;
import steve6472.orbiter.world.ecs.blueprints.ParticleEmittersBlueprint;
import steve6472.orbiter.world.ecs.components.Gravity;
import steve6472.orbiter.world.ecs.components.specific.CropPlot;
import steve6472.orbiter.world.ecs.components.specific.SeedBag;
import steve6472.orbiter.world.ecs.components.specific.SeedDispenser;
import steve6472.orbiter.world.ecs.core.Blueprint;
import steve6472.orbiter.world.ecs.core.BlueprintEntry;

import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public class Blueprints
{
    public static final BlueprintEntry<PhysicsBodyBlueprint> PHYSICS_BODY = register(PhysicsBodyBlueprint.KEY, PhysicsBodyBlueprint.CODEC);
    public static final BlueprintEntry<PositionBlueprint> POSITION = register(PositionBlueprint.KEY, PositionBlueprint.CODEC);
    public static final BlueprintEntry<RotationBlueprint> ROTATION = register(RotationBlueprint.KEY, RotationBlueprint.CODEC);
    public static final BlueprintEntry<IndexModelBlueprint> MODEL = register(IndexModelBlueprint.KEY, IndexModelBlueprint.CODEC);
    public static final BlueprintEntry<AnimatedModelBlueprint> ANIMATED_MODEL = register(AnimatedModelBlueprint.KEY, AnimatedModelBlueprint.CODEC);
    public static final BlueprintEntry<EnvironmentBlueprint> ENVIRONMENT = register(EnvironmentBlueprint.KEY, EnvironmentBlueprint.CODEC);
    public static final BlueprintEntry<TagsBlueprint> TAGS = register(TagsBlueprint.KEY, TagsBlueprint.CODEC);

    public static final BlueprintEntry<PrimitiveBlueprint<SeedDispenser>> SEED_DISPENSER = registerPrimitive(Constants.key("seed_dispenser"), SeedDispenser::new);
    public static final BlueprintEntry<PrimitiveBlueprint<SeedBag>> SEED_BAG = registerPrimitive(Constants.key("seed_bag"), SeedBag::new);
    public static final BlueprintEntry<PrimitiveBlueprint<CropPlot>> CROP_PLOT = registerPrimitive(Constants.key("crop_plot"), CropPlot::new);
    public static final BlueprintEntry<PrimitiveBlueprint<Gravity>> GRAVITY = registerPrimitive(Constants.key("value"), Gravity::new);

    public static final BlueprintEntry<ParticleEmittersBlueprint> EMITTERS = register(ParticleEmittersBlueprint.KEY, ParticleEmittersBlueprint.CODEC);

    private static <T extends Blueprint<?>> BlueprintEntry<T> register(Key key, Codec<T> codec)
    {
        if (Registries.COMPONENT_BLUEPRINT.get(key) != null)
            throw new RuntimeException("Blueprint with key " + key + " already exists!");

        BlueprintEntry<T> obj = new BlueprintEntry<>(key, codec);
        Registries.COMPONENT_BLUEPRINT.register(key, obj);
        return obj;
    }

    private static <B extends Component> BlueprintEntry<PrimitiveBlueprint<B>> registerPrimitive(Key key, Supplier<B> constructor)
    {
        if (Registries.COMPONENT_BLUEPRINT.get(key) != null)
            throw new RuntimeException("Blueprint with key " + key + " already exists!");

        PrimitiveBlueprint<B> primitiveBlueprint = new PrimitiveBlueprint<>(key, constructor);

        BlueprintEntry<PrimitiveBlueprint<B>> obj = new BlueprintEntry<>(key, primitiveBlueprint.codec());
        Registries.COMPONENT_BLUEPRINT.register(key, obj);
        return obj;
    }
}
