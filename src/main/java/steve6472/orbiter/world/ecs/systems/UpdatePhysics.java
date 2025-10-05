package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.github.stephengold.joltjni.BodyInterface;
import steve6472.core.log.Log;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orbiter.world.ecs.components.physics.PhysicsProperty;
import steve6472.orbiter.world.ecs.core.IteratingProfiledSystem;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class UpdatePhysics extends IteratingProfiledSystem
{
    private static final Logger LOGGER = Log.getLogger(UpdatePhysics.class);

    private final World world;
    private BodyInterface bi;

    public UpdatePhysics(World world)
    {
        super(Family.all(UUIDComp.class, Tag.Physics.class).get());
        this.world = world;
    }

    @Override
    public void update(float deltaTime)
    {
        bi = world.physics().getBodyInterface();
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        UUID uuid = Components.UUID.get(entity).uuid();
        if (OrbiterApp.getInstance().getClient().getClientUUID().equals(uuid))
            return;

        int bodyId = world.bodyMap().getByObj(uuid);
        if (!bi.isAdded(bodyId))
        {
            LOGGER.warning("Body does not exist for entity " + uuid);
            return;
        }

        for (Class<? extends PhysicsProperty> physicsComponent : PhysicsProperty.PHYSICS_COMPONENTS)
        {
            PhysicsProperty physicsProperty = ComponentMapper.getFor(physicsComponent).get(entity);
            if (physicsProperty == null)
                continue;

            physicsProperty.modifyBody(bi, bodyId);
        }
    }
}
