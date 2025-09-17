package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import org.joml.Matrix4f;
import steve6472.flare.MasterRenderer;
import steve6472.flare.ui.font.render.Billboard;
import steve6472.flare.ui.font.render.TextLine;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.RenderECSSystem;
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
public class RemoveEventComponents extends IteratingProfiledSystem implements RenderECSSystem
{
    @SuppressWarnings("unchecked")
    private static final Class<? extends Component>[] EVENTS = new Class[]{
        Click.class
    };

    public RemoveEventComponents()
    {
        super(Family.one(EVENTS).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        for (Class<? extends Component> event : EVENTS)
        {
            entity.remove(event);
        }
    }
}
