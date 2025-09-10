package steve6472.orbiter.world.ecs.blueprints;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.flare.assets.model.blockbench.LoadedModel;
import steve6472.flare.assets.model.blockbench.animation.controller.AnimationController;
import steve6472.flare.assets.model.primitive.PrimitiveSkinModel;
import steve6472.flare.registry.FlareRegistries;
import steve6472.orbiter.Constants;
import steve6472.orbiter.util.Holder;
import steve6472.orbiter.world.ecs.components.AnimatedModel;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.ecs.core.Blueprint;

import java.util.List;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public record AnimatedModelBlueprint(Key modelKey, Holder<AnimationController> controllerHolder) implements Blueprint<AnimatedModelBlueprint>
{
    public static final Key KEY = Constants.key("animated_model");
    public static final Codec<AnimatedModelBlueprint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Constants.KEY_CODEC.fieldOf("model").forGetter(e -> e.modelKey),
        Holder.create(FlareRegistries.ANIMATION_CONTROLLER).fieldOf("controller").forGetter(e -> e.controllerHolder)
    ).apply(instance, AnimatedModelBlueprint::new));

    @Override
    public List<Component> createComponents()
    {
        return List.of(new AnimatedModel(FlareRegistries.ANIMATED_MODEL.get(modelKey), controllerHolder));
    }

    @Override
    public Codec<AnimatedModelBlueprint> codec()
    {
        return CODEC;
    }

    @Override
    public Key key()
    {
        return KEY;
    }
}
