package steve6472.orbiter.world.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.flare.assets.model.Model;
import steve6472.flare.assets.model.blockbench.LoadedModel;
import steve6472.flare.assets.model.blockbench.anim.AnimationController;
import steve6472.flare.assets.model.primitive.PrimitiveSkinModel;
import steve6472.flare.registry.FlareRegistries;
import steve6472.orbiter.Constants;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class AnimatedModel implements Component, Pool.Poolable
{
    public static final Codec<AnimatedModel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Constants.KEY_CODEC.fieldOf("key").forGetter(e -> e.model().key()),
        Codec.STRING.fieldOf("animation").forGetter(e -> e.animationName),
        Codec.BOOL.fieldOf("model").forGetter(e -> e.animationController.timer.isLooping())
    ).apply(instance, (key, animationName, looping) -> new AnimatedModel(FlareRegistries.STATIC_MODEL.get(key), animationName, looping)));

    public static final BufferCodec<ByteBuf, AnimatedModel> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.KEY, e -> e.model().key(),
        BufferCodecs.STRING, e -> e.animationName,
        BufferCodecs.BOOL, e -> e.animationController.timer.isLooping(),
        (key, animationName, looping) -> new AnimatedModel(FlareRegistries.STATIC_MODEL.get(key), animationName, looping));

    public Model model;
    public PrimitiveSkinModel primitiveSkinModel;
    public LoadedModel loadedModel;
    public AnimationController animationController;
    public String animationName;

    public AnimatedModel() {}

    public AnimatedModel(Model model, String animationName, boolean looping)
    {
        this.model = model;
        this.loadedModel = FlareRegistries.ANIMATED_LOADED_MODEL.get(model.key());
        this.primitiveSkinModel = loadedModel.toPrimitiveSkinModel();
        this.animationController = new AnimationController(loadedModel.getAnimationByName(animationName), primitiveSkinModel.skinData, loadedModel);
        this.animationController.timer.start();
        this.animationController.timer.setLoop(looping);
        this.animationName = animationName;
    }

    public Model model()
    {
        return model;
    }

    @Override
    public void reset()
    {
        model = null;
        primitiveSkinModel = null;
        loadedModel = null;
    }
}
