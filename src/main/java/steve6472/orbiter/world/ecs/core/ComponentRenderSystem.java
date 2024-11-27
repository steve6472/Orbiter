package steve6472.orbiter.world.ecs.core;

import dev.dominion.ecs.api.Dominion;
import steve6472.flare.MasterRenderer;
import steve6472.orbiter.world.EntityModify;
import steve6472.orbiter.world.World;

public interface ComponentRenderSystem
{
    void tick(MasterRenderer renderer, Dominion dominion, World world);
}