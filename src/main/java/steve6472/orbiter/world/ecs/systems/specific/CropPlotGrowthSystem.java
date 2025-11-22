package steve6472.orbiter.world.ecs.systems.specific;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.components.specific.CropPlot;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;

/**
 * Created by steve6472
 * Date: 11/22/2025
 * Project: Orbiter <br>
 */
public class CropPlotGrowthSystem extends IteratingProfiledSystem
{
    public CropPlotGrowthSystem()
    {
        super(Family.all(UUIDComp.class, Position.class, CropPlot.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        CropPlot cropPlot = Components.CROP_PLOT.get(entity);

        if (!cropPlot.hasSeed)
            return;

        cropPlot.growth = Math.min(1.0, cropPlot.growth + cropPlot.nextGrowth.get() / 60d);
    }
}
