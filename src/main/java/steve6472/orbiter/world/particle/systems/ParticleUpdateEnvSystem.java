package steve6472.orbiter.world.particle.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import steve6472.orbiter.orlang.Orlang;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.orlang.OrlangValue;
import steve6472.orbiter.orlang.codec.OrCode;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.particle.components.MaxAge;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;
import steve6472.orbiter.world.particle.ParticleComponents;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class ParticleUpdateEnvSystem extends IteratingProfiledSystem
{
    public ParticleUpdateEnvSystem()
    {
        super(Family.all(OrlangEnvironment.class).get());
    }

    private long now;

    @Override
    public void update(float deltaTime)
    {
        now = System.currentTimeMillis();
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        OrlangEnvironment env = ParticleComponents.PARTICLE_ENVIRONMENT.get(entity);

        MaxAge maxAge = ParticleComponents.MAX_AGE.get(entity);
        if (maxAge != null)
        {
            env.setValue(MaxAge.AGE, OrlangValue.num(maxAge.calculateAge(now)));
            env.setValue(MaxAge.MAX_AGE, OrlangValue.num(maxAge.maxAge));
        }

        OrCode tick = env.expressions.get("tick");
        if (tick != null)
        {
            Orlang.interpreter.interpret(tick, env);
        }

        OrCode frame = env.expressions.get("frame");
        if (frame != null)
        {
            Orlang.interpreter.interpret(frame, env);
        }
    }
}
