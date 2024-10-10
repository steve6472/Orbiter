package steve6472.orbiter.world.ecs.core;

import dev.dominion.ecs.api.Dominion;
import steve6472.orbiter.world.EntityModify;
import steve6472.orbiter.world.World;

public interface ComponentSystem extends EntityModify
{
    void tick(Dominion dominion, World world);
}