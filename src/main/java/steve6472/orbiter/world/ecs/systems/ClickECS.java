package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.github.stephengold.joltjni.Body;
import com.github.stephengold.joltjni.CompoundShape;
import com.github.stephengold.joltjni.RayCastResult;
import com.github.stephengold.joltjni.readonly.ConstShape;
import steve6472.orbiter.Client;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.Registries;
import steve6472.orbiter.settings.Keybinds;
import steve6472.orbiter.world.collision.OrbiterCollisionShape;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.Tag;
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orbiter.world.ecs.components.event.Click;
import steve6472.orbiter.world.ecs.components.physics.Collision;
import steve6472.orbiter.world.ecs.components.physics.Position;

import java.util.*;

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

        RayCastResult lookAtObject = client.getRayTrace().getLookAtObject();
        if (lookAtObject == null)
            return;

        UUID uuid = client.getWorld().bodyMap().getUUIDById(lookAtObject.getBodyId());
        if (uuid == null)
            return;

        Body body = client.getWorld().bodyMap().getBodyById(lookAtObject.getBodyId());
        if (body == null)
            return;

        Entity entity = findEntity(client, uuid);
        if (entity == null)
            return;

        ConstShape shape = body.getShape();
        if (shape instanceof CompoundShape compoundShape)
        {
            Collision collision = Components.COLLISION.get(entity);
            OrbiterCollisionShape orbiterCollisionShape = Registries.COLLISION.get(collision.collisionKey());

            entity.add(new Click(orbiterCollisionShape.ids()[fixSubShapeId(lookAtObject.getSubShapeId2(), compoundShape.getNumSubShapes())]));
        } else
        {
            entity.add(new Click(-1));
        }
    }

    public static int fixSubShapeId(int id, int maxShapes)
    {
        String binaryString = Integer.toBinaryString(maxShapes);
        int length = binaryString.length();
        int ones = 0;
        for (int i = 0; i < length; i++)
            ones |= (1 << i);
        return id & ones;
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
