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
import steve6472.flare.assets.model.blockbench.animation.Animation;
import steve6472.flare.assets.model.blockbench.animation.controller.AnimationController;
import steve6472.flare.assets.model.primitive.PrimitiveSkinModel;
import steve6472.flare.registry.FlareRegistries;
import steve6472.orbiter.Constants;
import steve6472.orbiter.util.Holder;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class AnimatedModel implements Component, Pool.Poolable
{
    public static final Codec<AnimatedModel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Constants.KEY_CODEC.fieldOf("model").forGetter(e -> e.model.key()),
        Holder.create(FlareRegistries.ANIMATION_CONTROLLER).fieldOf("controller").forGetter(e -> Holder.fromValue(e.animationController))
    ).apply(instance, (key, controllerHolder) -> new AnimatedModel(FlareRegistries.ANIMATED_MODEL.get(key), controllerHolder)));

    public static final BufferCodec<ByteBuf, AnimatedModel> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.KEY, e -> e.model().key(),
        BufferCodecs.KEY, e -> e.animationController.key(),
        (key, controllerKey) -> new AnimatedModel(FlareRegistries.ANIMATED_MODEL.get(key), Holder.fromValue(FlareRegistries.ANIMATION_CONTROLLER.get(controllerKey))));

    public Model model;
    public LoadedModel loadedModel;
    public AnimationController animationController;

    public AnimatedModel() {}

    public AnimatedModel(Model model, Holder<AnimationController> controllerHolder)
    {
        this.model = model;
        this.loadedModel = FlareRegistries.ANIMATED_LOADED_MODEL.get(model.key());
        animationController = controllerHolder.get().createForModel(loadedModel);
    }

    public Model model()
    {
        return model;
    }

    @Override
    public void reset()
    {
        model = null;
        loadedModel = null;
    }
}
