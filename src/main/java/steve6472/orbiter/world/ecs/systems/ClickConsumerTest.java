package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import org.joml.Matrix4f;
import steve6472.core.registry.Key;
import steve6472.flare.MasterRenderer;
import steve6472.flare.assets.model.blockbench.animation.controller.AnimationController;
import steve6472.flare.ui.font.render.Billboard;
import steve6472.flare.ui.font.render.TextLine;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.RenderECSSystem;
import steve6472.orbiter.world.ecs.components.AnimatedModel;
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orbiter.world.ecs.components.event.Click;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class ClickConsumerTest extends IteratingProfiledSystem implements RenderECSSystem
{
    public ClickConsumerTest()
    {
        super(Family.all(Click.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        Click click = Components.CLICK.get(entity);
        if (click.id() == 0)
            return;

        AnimatedModel animatedModel = Components.ANIMATED_MODEL.get(entity);
        if (animatedModel == null)
            return;

        AnimationController controller = animatedModel.animationController;
        if (controller == null)
            return;

        if (controller.key().equals(Key.withNamespace("orbiter", "rift_reactor")))
        {
            controller.controllers().get("button_" + click.id()).forceTransition("pressed");
        }
    }
}
