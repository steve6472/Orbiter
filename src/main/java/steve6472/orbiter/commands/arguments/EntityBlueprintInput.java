package steve6472.orbiter.commands.arguments;

import steve6472.orbiter.world.ecs.core.EntityBlueprint;
import steve6472.orlang.OrlangValue;

import java.util.Map;

/**
 * Created by steve6472
 * Date: 11/25/2025
 * Project: Orbiter <br>
 */
public record EntityBlueprintInput(EntityBlueprint blueprint, Map<String, OrlangValue> arguments)
{
}
