package steve6472.orbiter.world.ecs.core;

import com.badlogic.ashley.core.EntitySystem;

import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 11/5/2025
 * Project: Orbiter <br>
 */
public class RunSystem extends EntitySystem
{
    private final Consumer<Float> run;

    public RunSystem(Consumer<Float> run)
    {
        this.run = run;
    }

    @Override
    public void update(float deltaTime)
    {
        run.accept(deltaTime);
    }
}
