package steve6472.orbiter.actions;

import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orlang.codec.OrCode;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public record Empty(OrCode condition, EntitySelection entitySelection) implements Action
{
    public static final Codec<Empty> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Action.conditionCodec(),
        Action.entitySelectionCodec()
    ).apply(instance, Empty::new));

    @Override
    public void execute(World world, Entity entity, OrlangEnv environment)
    {

    }

    @Override
    public ActionType<?> getType()
    {
        // TODO: set type
        return ActionType.EMPTY;
    }
}
