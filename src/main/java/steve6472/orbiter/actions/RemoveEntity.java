package steve6472.orbiter.actions;

import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orlang.codec.OrCode;

/**
 * Created by steve6472
 * Date: 11/30/2025
 * Project: Orbiter <br>
 */
public record RemoveEntity(OrCode condition, EntitySelection entitySelection) implements Action
{
    public static final Codec<RemoveEntity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Action.conditionCodec(),
        Action.entitySelectionCodec()
    ).apply(instance, RemoveEntity::new));

    @Override
    public void execute(World world, Entity entity, OrlangEnv environment)
    {
        UUIDComp uuidComp = Components.UUID.get(entity);
        if (uuidComp != null)
        {
            world.removeEntity(uuidComp.uuid(), true);
        } else
        {
            world.ecsEngine().removeEntity(entity);
        }
    }

    @Override
    public ActionType<?> getType()
    {
        return ActionType.REMOVE_ENTITY;
    }
}
