package steve6472.orbiter.commands.impl;

import com.badlogic.ashley.core.Entity;
import com.github.stephengold.joltjni.RayCastResult;
import com.mojang.brigadier.CommandDispatcher;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.orbiter.Client;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.commands.Command;
import steve6472.orbiter.commands.CommandSource;
import steve6472.orbiter.util.PhysicsRayTrace;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.Properties;
import steve6472.orbiter.world.ecs.systems.ClickECS;

import java.util.UUID;
import java.util.logging.Logger;

public class PrintProperties extends Command
{
	public PrintProperties(CommandDispatcher<CommandSource> dispatcher)
	{
		super(dispatcher);
	}

	public void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(
			literal("print_properties")
				.executes(c ->
				{

					Client client = OrbiterApp.getInstance().getClient();
					World world = client.getWorld();
					if (world == null)
					{
						c.getSource().sendError("Not in world");
						return -1;
					}

					PhysicsRayTrace rayTrace = client.getRayTrace();
					RayCastResult lookAtObject = rayTrace.getLookAtObject();
					if (lookAtObject == null)
					{
						c.getSource().sendError("Not looking at physics object");
						return -1;
					}

					int bodyId = lookAtObject.getBodyId();
					UUID uuid = world.bodyMap().getUUIDById(bodyId);
					if (uuid == null)
					{
						c.getSource().sendError("Physics object is not an ECS entity");
						return -1;
					}

					Entity entity = ClickECS.findEntity(client, uuid);
					if (entity == null)
					{
						c.getSource().sendError("ECS Entity with UUID '%s' not found".formatted(uuid));
						return -1;
					}

					Properties properties = Components.PROPERTIES.get(entity);
					if (properties == null)
					{
						c.getSource().sendFeedback("Entity has no properties component");
						return 0;
					}

					c.getSource().sendFeedback("Entity @" + Integer.toHexString(entity.hashCode()));
					for (Key key : properties.keys())
					{
						Object o = properties.get(key);
						c.getSource().sendFeedback(key + " = " + o);
					}

					return 0;
				})
		);
	}
}
