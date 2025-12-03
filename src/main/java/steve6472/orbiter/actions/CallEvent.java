package steve6472.orbiter.actions;

import com.badlogic.ashley.core.Entity;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Registries;
import steve6472.orbiter.properties.Property;
import steve6472.orbiter.util.ValueSource;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.ecs.components.Properties;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;
import steve6472.orlang.OrlangValue;
import steve6472.orlang.codec.OrCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public record CallEvent(OrCode condition, EntitySelection entitySelection, Key event, Map<String, ValueSource> arguments) implements Action
{
    public static final Codec<CallEvent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Action.conditionCodec(),
        Action.entitySelectionCodec(),
        Constants.KEY_CODEC.fieldOf("event").forGetter(CallEvent::event),
        Codec.unboundedMap(Codec.STRING, ValueSource.CODEC).optionalFieldOf("arguments", Map.of()).forGetter(CallEvent::arguments)
    ).apply(instance, CallEvent::new));

    @Override
    public void execute(World world, Entity entity, OrlangEnv environment)
    {
        Components.BLUEPRINT_REFERENCE.ifPresent(entity, blueprintReference -> {
            EntityBlueprint blueprint = Registries.ENTITY_BLUEPRINT.get(blueprintReference.key());
            blueprint.getEvent(event).ifPresent(action ->
            {
                Map<String, OrlangValue> args = new HashMap<>(arguments.size());
                arguments.forEach((key, source) -> args.put(key, OrlangValue.smartCast(source.get(environment.env))));
                Action.executeAction(action, world, environment, args, ref -> ref.withInitiator(entity));
            });
        });
    }

    @Override
    public ActionType<?> getType()
    {
        return ActionType.CALL_EVENT;
    }
}
