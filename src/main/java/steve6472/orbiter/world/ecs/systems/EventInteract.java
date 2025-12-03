package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.github.stephengold.joltjni.Body;
import com.github.stephengold.joltjni.CompoundShape;
import com.github.stephengold.joltjni.RayCastResult;
import com.github.stephengold.joltjni.readonly.ConstShape;
import steve6472.orbiter.Client;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.Registries;
import steve6472.orbiter.actions.Action;
import steve6472.orbiter.settings.Keybinds;
import steve6472.orbiter.world.collision.OrbiterCollisionShape;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.BlueprintReference;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.ecs.components.physics.Collision;
import steve6472.orbiter.world.ecs.core.EntityBlueprint;
import steve6472.orlang.OrlangValue;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class EventInteract extends EntitySystem
{
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

        Entity entity = ClickECS.findEntity(client, uuid);
        if (entity == null)
            return;

        String id = "";
        ConstShape shape = body.getShape();
        if (shape instanceof CompoundShape)
        {
            Collision collision = Components.COLLISION.get(entity);
            OrbiterCollisionShape orbiterCollisionShape = Registries.COLLISION.get(collision.collisionKey());

            id = orbiterCollisionShape.ids()[client.getRayTrace().getLookAtSubshapeOrdinal()];
        }

        BlueprintReference blueprintReference = Components.BLUEPRINT_REFERENCE.get(entity);
        EntityBlueprint blueprint = Registries.ENTITY_BLUEPRINT.get(blueprintReference.key());
        Optional<Action> event = blueprint.getEvent(Constants.Events.ON_INTERACTION);
        if (event.isEmpty())
            return;

        Action action = event.get();
        Action.startAction(action, client.getWorld(), entity, Map.of("id", OrlangValue.string(id)), UnaryOperator.identity());
    }
}
