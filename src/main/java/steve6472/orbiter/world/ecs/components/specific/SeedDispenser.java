package steve6472.orbiter.world.ecs.components.specific;

import com.badlogic.ashley.core.Component;

/**
 * Created by steve6472
 * Date: 11/22/2025
 * Project: Orbiter <br>
 */
public class SeedDispenser implements Component
{
    public int maxLevel;
    public int currentLevel;
    public int cooldown;
    public int maxCooldown;

    public SeedDispenser()
    {
        this.maxLevel = 10;
        this.currentLevel = 0;
        this.cooldown = 120;
        this.maxCooldown = 120;
    }
}
