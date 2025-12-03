package steve6472.orbiter.actions;

import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.flare.assets.model.blockbench.animation.controller.AnimationController;
import steve6472.flare.assets.model.blockbench.animation.controller.Controller;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.AnimatedModel;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orlang.codec.OrCode;

/**
 * Created by steve6472
 * Date: 11/30/2025
 * Project: Orbiter <br>
 */
public record AnimationForceTransition(OrCode condition, EntitySelection entitySelection, String controller, String state) implements Action
{
    public static final Codec<AnimationForceTransition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Action.conditionCodec(),
        Action.entitySelectionCodec(),
        Codec.STRING.fieldOf("controller").forGetter(AnimationForceTransition::controller),
        Codec.STRING.fieldOf("state").forGetter(AnimationForceTransition::state)
    ).apply(instance, AnimationForceTransition::new));

    @Override
    public void execute(World world, Entity entity, OrlangEnv environment)
    {
        AnimatedModel animatedModel = Components.ANIMATED_MODEL.get(entity);
        if (animatedModel == null)
            return;

        AnimationController animationController = animatedModel.animationController;
        if (animationController == null)
            return;

        Controller controller = animationController.controllers().get(controller());
        if (controller != null && controller.states().get(state) != null)
        {
            controller.forceTransition(state);
        }
    }

    @Override
    public ActionType<?> getType()
    {
        return ActionType.ANIMATION_FORCE_TRANSITION;
    }
}
