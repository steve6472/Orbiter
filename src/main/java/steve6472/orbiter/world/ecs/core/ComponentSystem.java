package steve6472.orbiter.world.ecs.core;

import dev.dominion.ecs.api.Dominion;
import steve6472.orbiter.world.World;

public interface ComponentSystem
{
    void tick(Dominion dominion, World world);
}