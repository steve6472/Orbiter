package steve6472.orbiter.world.ecs.components.specific;

import com.badlogic.ashley.core.Component;
import steve6472.core.util.RandomUtil;

import java.util.function.Supplier;

/**
 * Created by steve6472
 * Date: 11/22/2025
 * Project: Orbiter <br>
 */
public class CropPlot implements Component
{
    public double growth;
    public Supplier<Double> nextGrowth;
    public boolean hasSeed;

    public CropPlot()
    {
        this.growth = 0;
        nextGrowth = () -> RandomUtil.randomDouble(0.01, 0.05);
        hasSeed = false;
    }
}
