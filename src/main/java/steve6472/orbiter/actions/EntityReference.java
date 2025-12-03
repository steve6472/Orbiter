package steve6472.orbiter.actions;

import com.badlogic.ashley.core.Entity;
import org.jetbrains.annotations.Nullable;
import steve6472.core.log.Log;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 11/29/2025
 * Project: Orbiter <br>
 */
public record EntityReference(EntitySelection defaultSelection, @Nullable Entity initiator, @Nullable Entity iterated)
{
    public static EntityReference empty()
    {
        return new EntityReference(EntitySelection.INITIATOR, null, null);
    }

    public EntityReference withDefaultSelection(EntitySelection selection)
    {
        return new EntityReference(selection, initiator, iterated);
    }

    public EntityReference withInitiator(Entity entity)
    {
        return new EntityReference(defaultSelection, entity, iterated);
    }

    public EntityReference withIterated(Entity entity)
    {
        return new EntityReference(defaultSelection, initiator, entity);
    }

    public Optional<Entity> get(EntitySelection selection)
    {
        return switch (selection)
        {
            case INITIATOR -> Optional.ofNullable(initiator);
            case ITERATED -> Optional.ofNullable(iterated);
            case UNSELECTED -> throw new IllegalStateException("Tried to reference 'unselected'");
        };
    }

    public Optional<Entity> get()
    {
        return get(defaultSelection);
    }
}
