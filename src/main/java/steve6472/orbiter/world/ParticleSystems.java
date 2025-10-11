package steve6472.orbiter.world;

import com.badlogic.ashley.core.PooledEngine;
import steve6472.core.util.Profiler;
import steve6472.orbiter.util.ProfilerPrint;
import steve6472.orbiter.world.particle.systems.*;

/**
 * Created by steve6472
 * Date: 8/24/2025
 * Project: Orbiter <br>
 */
public class ParticleSystems
{
    private final World world;
    private final PooledEngine engine;
    public final Profiler profiler = new Profiler(60);

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
        // Should be first
        engine.addSystem(new ParticleUpdateEnvSystem());

        engine.addSystem(new ParticleVelocitySystem());
        engine.addSystem(new ParticleRotationSystem());
        engine.addSystem(new ParticleMaxAgeSystem(world));
        engine.addSystem(new RemoveInvalidFollowerSystem(world));
    }

    public void runTickSystems(float frameTime)
    {
        profiler.start();
        engine.update(frameTime);
        profiler.end();

//        ProfilerPrint.sout(profiler, "Count", engine.getEntities().size());
    }
}
