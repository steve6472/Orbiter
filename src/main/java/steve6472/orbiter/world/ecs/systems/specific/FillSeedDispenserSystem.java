package steve6472.orbiter.world.ecs.systems.specific;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import org.joml.Vector3f;
import steve6472.core.util.ColorUtil;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.rendering.gizmo.Gizmos;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.components.specific.SeedBag;
import steve6472.orbiter.world.ecs.components.specific.SeedDispenser;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 11/22/2025
 * Project: Orbiter <br>
 */
public class FillSeedDispenserSystem extends IteratingProfiledSystem
{
    private final Family SEED_BAG_FAMILY = Family.all(UUIDComp.class, Position.class, SeedBag.class).get();

    public FillSeedDispenserSystem()
    {
        super(Family.all(UUIDComp.class, Position.class, SeedDispenser.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        Position position = Components.POSITION.get(entity);
        SeedDispenser dispenserLevel = Components.SEED_DISPENSER.get(entity);

        Vector3f succPos = position.toVec3f().add(0, 0.3f, 0);
        float succRange = 0.45f;
        Gizmos.lineCuboid(new Vector3f(succPos).add(0, succRange / 2f, 0), succRange, succRange / 2f, succRange, 0xff30a080);

        ImmutableArray<Entity> seedBags = getEngine().getEntitiesFor(SEED_BAG_FAMILY);
        for (Entity seedBagEntity : seedBags)
        {
            UUID uuid = Components.UUID.get(seedBagEntity).uuid();
            Position bagPosition = Components.POSITION.get(seedBagEntity);
            SeedBag seedBag = Components.SEED_BAG.get(seedBagEntity);

            float distance = bagPosition.toVec3f().distance(succPos);
            if (distance <= 2 + succRange)
            {
                Gizmos.lineCuboid(bagPosition.toVec3f(), 0.2f, ColorUtil.blend(0xffffffff, 0xffff0000, distance - succRange));
                Gizmos.point(bagPosition.toVec3f(), 0xff00fff0).alwaysOnTop();

                if (distance <= succRange && bagPosition.y() >= succPos.y)
                {
                    OrbiterApp.getInstance().getClient().getWorld().removeEntity(uuid, true);
                    dispenserLevel.currentLevel = Math.min(dispenserLevel.maxLevel, dispenserLevel.currentLevel + seedBag.seedCount);
                }
            }
        }
    }
}
