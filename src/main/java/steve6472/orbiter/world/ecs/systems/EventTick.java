package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Registries;
import steve6472.orbiter.actions.Action;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.BlueprintReference;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;

import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Created by steve6472
 * Date: 11/22/2025
 * Project: Orbiter <br>
 */
public class EventTick extends IteratingProfiledSystem
{
    private final World world;

    public EventTick(World world)
    {
        super(Family.all(Tag.HasOnTickEvent.class, BlueprintReference.class, OrlangEnv.class).get());
        this.world = world;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        BlueprintReference blueprintReference = Components.BLUEPRINT_REFERENCE.get(entity);
        EntityBlueprint blueprint = Registries.ENTITY_BLUEPRINT.get(blueprintReference.key());
        Optional<Action> event = blueprint.getEvent(Constants.Events.ON_TICK);
        if (event.isEmpty())
            return;

        Action action = event.get();
        Action.startAction(action, world, entity, Map.of(), UnaryOperator.identity());
    }
}
