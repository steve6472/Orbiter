package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.mojang.datafixers.util.Pair;
import steve6472.core.log.Log;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.world.World;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class UpdateClientData extends EntitySystem
{
    private static final Logger LOGGER = Log.getLogger(UpdateClientData.class);

    public UpdateClientData()
    {
    }

    private final List<Pair<UUID, Consumer<Entity>>> updateFunctions = new ArrayList<>();

    public void add(UUID uuid, Consumer<Entity> function)
    {
        updateFunctions.add(Pair.of(uuid, function));
    }

    @Override
    public void update(float deltaTime)
    {
        OrbiterApp orbiter = OrbiterApp.getInstance();
        World world = orbiter.getClient().getWorld();

        for (Pair<UUID, Consumer<Entity>> updateFunction : updateFunctions)
        {
            UUID uuid = updateFunction.getFirst();
            Consumer<Entity> modifyFunc = updateFunction.getSecond();
            world.getEntityByUUID(uuid).ifPresentOrElse(modifyFunc, () -> LOGGER.warning("Player entity was not spawned!"));
        }

        updateFunctions.clear();
    }
}
