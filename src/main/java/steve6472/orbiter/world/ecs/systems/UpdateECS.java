package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.jme3.bullet.objects.PhysicsRigidBody;
import steve6472.core.log.Log;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orbiter.world.ecs.components.physics.ModifyState;
import steve6472.orbiter.world.ecs.components.physics.PhysicsProperty;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class UpdateECS extends IteratingSystem
{
    private static final Logger LOGGER = Log.getLogger(UpdateECS.class);

    private final World world;

    public UpdateECS(World world)
    {
        super(Family.all(UUIDComp.class, Tag.Physics.class).get());
        this.world = world;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        UUID uuid = Components.UUID.get(entity).uuid();

        PhysicsRigidBody body = world.bodyMap().get(uuid);

        if (body == null)
        {
            LOGGER.warning("Body does not exist for entity " + uuid);
            return;
        }

        for (Class<? extends PhysicsProperty> physicsComponent : PhysicsProperty.PHYSICS_COMPONENTS)
        {
            var componentByClass = Components.getComponentByClass(physicsComponent);
            if (componentByClass.isEmpty())
                continue;

            PhysicsProperty physicsProperty = componentByClass.get().get(entity);
            if (physicsProperty == null)
                continue;

            ModifyState modified = physicsProperty.modifyComponent(body);

            if (modified.hasNewComponent())
            {
                entity.add(modified.getComponent());
                world.markModified(entity, physicsComponent);
            } else if (modified.hasModifiedComponent())
            {
                world.markModified(entity, physicsComponent);
            }
        }
    }
}
