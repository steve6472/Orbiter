package steve6472.orbiter.world.ecs.blueprints;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.flare.assets.model.blockbench.LoadedModel;
import steve6472.flare.assets.model.primitive.PrimitiveSkinModel;
import steve6472.flare.registry.FlareRegistries;
import steve6472.orbiter.Constants;
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
public record AnimatedModelBlueprint(Key modelKey, String animationName, boolean looping) implements Blueprint<AnimatedModelBlueprint>
{
    public static final Key KEY = Constants.key("animated_model");
    public static final Codec<AnimatedModelBlueprint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Constants.KEY_CODEC.fieldOf("model").forGetter(AnimatedModelBlueprint::modelKey),
        Codec.STRING.fieldOf("animation").forGetter(AnimatedModelBlueprint::animationName),
        Codec.BOOL.fieldOf("loop").forGetter(AnimatedModelBlueprint::looping)
    ).apply(instance, AnimatedModelBlueprint::new));

    @Override
    public List<Component> createComponents()
    {
        return List.of(new AnimatedModel(FlareRegistries.ANIMATED_MODEL.get(modelKey), animationName, looping));
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
