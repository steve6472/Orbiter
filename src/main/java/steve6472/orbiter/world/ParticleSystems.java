package steve6472.orbiter.world;

import com.badlogic.ashley.core.PooledEngine;
import steve6472.orbiter.world.ecs.systems.particle.ParticleLifetimeSystem;
import steve6472.orbiter.world.ecs.systems.particle.RemoveInvalidFollowerSystem;

/**
 * Created by steve6472
 * Date: 8/24/2025
 * Project: Orbiter <br>
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class ParticleSystems
{
    private final World world;
    private final PooledEngine engine;

    public ParticleSystems(World world, PooledEngine engine)
    {
        this.world = world;
        this.engine = engine;
    }

    /*
     * Systems
     */

    public void init()
    {
        engine.addSystem(new ParticleLifetimeSystem(world));
        engine.addSystem(new RemoveInvalidFollowerSystem(world));
    }

    public void runTickSystems(float frameTime)
    {
        engine.update(frameTime);
    }
}
