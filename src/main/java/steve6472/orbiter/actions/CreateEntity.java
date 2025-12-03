package steve6472.orbiter.actions;

import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.Registries;
import steve6472.orbiter.util.Holder;
import steve6472.orbiter.util.ValueSource;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;
import steve6472.orlang.OrlangValue;
import steve6472.orlang.codec.OrCode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public record CreateEntity(OrCode condition, EntitySelection entitySelection, Holder<EntityBlueprint> blueprint, Map<String, ValueSource> arguments) implements Action
{
    public static final Codec<CreateEntity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Action.conditionCodec(),
        Action.entitySelectionCodec(),
        Holder.create(Registries.ENTITY_BLUEPRINT).fieldOf("blueprint").forGetter(CreateEntity::blueprint),
        Codec.unboundedMap(Codec.STRING, ValueSource.CODEC).optionalFieldOf("arguments", Map.of()).forGetter(CreateEntity::arguments)
    ).apply(instance, CreateEntity::new));

    @Override
    public void execute(World world, Entity entity, OrlangEnv environment)
    {
        Map<String, OrlangValue> args = new HashMap<>(arguments.size());
        arguments.forEach((key, source) -> args.put(key, OrlangValue.smartCast(source.get(environment.env))));
        world.addEntity(blueprint.get(), UUID.randomUUID(), args, true);
    }

    @Override
    public ActionType<?> getType()
    {
        return ActionType.CREATE_ENTITY;
    }
}
