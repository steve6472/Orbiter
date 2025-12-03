package steve6472.orbiter.actions;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.log.Log;
import steve6472.orbiter.Registries;
import steve6472.orbiter.util.OrbiterCodecs;
import steve6472.orbiter.util.StringSource;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.ecs.core.ComponentEntry;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;
import steve6472.orlang.codec.OrCode;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public record AddComponentGroups(OrCode condition, EntitySelection entitySelection, List<StringSource> groups) implements Action
{
    private static final Logger LOGGER = Log.getLogger(AddComponentGroups.class);
    public static final Codec<AddComponentGroups> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Action.conditionCodec(),
        Action.entitySelectionCodec(),
        OrbiterCodecs.STRING_SOURCE_LIST_OR_SINGLE.fieldOf("groups").forGetter(e -> e.groups)
    ).apply(instance, AddComponentGroups::new));

    @Override
    public void execute(World world, Entity entity, OrlangEnv environment)
    {
        Components.BLUEPRINT_REFERENCE.ifPresent(entity, blueprintKey -> {
            EntityBlueprint blueprint = Registries.ENTITY_BLUEPRINT.get(blueprintKey.key());
            if (blueprint == null)
            {
                Log.warningOnce(LOGGER, "Entity reference not found '%s'".formatted(blueprintKey.key()));
                return;
            }

            for (StringSource groupSource : groups)
            {
                String group = groupSource.get(environment.env);

                Optional<Map<ComponentEntry<?>, Supplier<Component>>> componentGroupOpt = blueprint.getComponentGroup(group);
                if (componentGroupOpt.isEmpty())
                {
                    Log.warningOnce(LOGGER, "Component group '%s' not found for blueprint '%s'".formatted(group, blueprintKey.key()));
                    continue;
                }

                Map<ComponentEntry<?>, Supplier<Component>> componentGroup = componentGroupOpt.get();
                componentGroup.forEach((_, constructor) -> entity.add(constructor.get()));
            }
        });
    }

    @Override
    public ActionType<?> getType()
    {
        return ActionType.ADD_COMPONENT_GROUPS;
    }
}
