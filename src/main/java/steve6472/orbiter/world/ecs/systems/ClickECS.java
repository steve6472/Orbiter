package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import steve6472.flare.struct.Struct;
import steve6472.orbiter.Client;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.Registries;
import steve6472.orbiter.settings.Keybinds;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.collision.OrbiterCollisionShape;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orbiter.world.ecs.components.physics.Collision;
import steve6472.orbiter.world.ecs.components.physics.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class ClickECS extends EntitySystem
{
    private static final Family FAMILY = Family.all(UUIDComp.class, Position.class, Tag.Physics.class, Collision.class).get();

    @Override
    public void update(float deltaTime)
    {
        if (!Keybinds.INTERACT_OBJECT.isActive())
            return;

        Client client = OrbiterApp.getInstance().getClient();

        PhysicsCollisionObject lookAtObject = client.getRayTrace().getLookAtObject();
        if (lookAtObject == null)
            return;

        Object userObject = lookAtObject.getUserObject();

        if (!(userObject instanceof UUID uuid))
            return;

        Entity entity = findEntity(client, uuid);
        if (entity == null)
            return;

        Collision collision = Components.COLLISION.get(entity);
        OrbiterCollisionShape orbiterCollisionShape = Registries.COLLISION.get(collision.collisionKey());

        // TODO: add temp component with this clickId. Other Systems can use this. Removed automatically at the end of the tick.
        short clickId = orbiterCollisionShape.ids()[client.getRayTrace().getLookAtTriangleIndex()];
    }

    public static Entity findEntity(Client client, UUID uuid)
    {
        for (Entity ent : client.getWorld().ecsEngine().getEntitiesFor(FAMILY))
        {
            if (Components.UUID.get(ent).uuid().equals(uuid))
            {
                return ent;
            }
        }
        return null;
    }
}
