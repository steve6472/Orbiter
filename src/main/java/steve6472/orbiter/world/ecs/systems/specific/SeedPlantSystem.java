package steve6472.orbiter.world.ecs.systems.specific;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.rendering.gizmo.Gizmos;
import steve6472.orbiter.util.AABB;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.components.specific.CropPlot;
import steve6472.orbiter.world.ecs.components.specific.Seed;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;

/**
 * Created by steve6472
 * Date: 11/22/2025
 * Project: Orbiter <br>
 */
public class SeedPlantSystem extends IteratingProfiledSystem
{
    public SeedPlantSystem()
    {
        super(Family.all(Position.class, Seed.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        final Family CROP_PLOT_FAMILY = Family.all(UUIDComp.class, Position.class, CropPlot.class).get();
        final AABB cropPlotBox = AABB.fromSize(0.8f, 0.2f, 0.8f);

        Position seedPosition = Components.POSITION.get(entity);

        ImmutableArray<Entity> cropPlots = getEngine().getEntitiesFor(CROP_PLOT_FAMILY);
        for (Entity cropPlotEntity : cropPlots)
        {
            Position plotPosition = Components.POSITION.get(cropPlotEntity);
            CropPlot cropPlot = Components.CROP_PLOT.get(cropPlotEntity);

            if (seedPosition.toVec3f().distance(plotPosition.toVec3f()) <= 2f)
            {
                Gizmos.filledLineCuboid(cropPlotBox.translate(plotPosition.toVec3f()), 0x40cc3080);
            }

            if (!cropPlotBox.containsPoint(plotPosition.toVec3f(), seedPosition.toVec3f()))
                continue;

            if (!cropPlot.hasSeed)
            {
                cropPlot.hasSeed = true;
                cropPlot.growth = 0f;
            }

            Components.UUID.ifPresentOrElse(entity, uuid -> {
                OrbiterApp.getInstance().getClient().getWorld().removeEntity(uuid.uuid(), true);
            }, () -> {
                getEngine().removeEntity(entity);
            });

            // TODO: spawn plant emitter
        }
    }
}
