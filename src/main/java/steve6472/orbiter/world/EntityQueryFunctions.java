package steve6472.orbiter.world;

import com.badlogic.ashley.core.Entity;
import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.joml.Vector3f;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.flare.assets.model.blockbench.animation.controller.AnimationQuery;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.Registries;
import steve6472.orbiter.actions.EntityReference;
import steve6472.orbiter.actions.EntitySelection;
import steve6472.orbiter.properties.Property;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.Properties;
import steve6472.orbiter.world.ecs.components.physics.LinearVelocity;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.components.physics.Rotation;
import steve6472.orlang.OrlangValue;
import steve6472.orlang.QueryFunctionSet;

import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 9/8/2025
 * Project: Orbiter <br>
 */
public class EntityQueryFunctions extends QueryFunctionSet implements AnimationQuery
{
    private static final Logger LOGGER = Log.getLogger(EntityQueryFunctions.class);
    private double animTime;
    private boolean anyAnimFinished, allAnimsFinished;

    public final Stack<EntityReference> entityReferenceStack = new ObjectArrayList<>();
    public final Stack<Map<String, OrlangValue>> arguments = new ObjectArrayList<>();

    public EntityQueryFunctions(Entity entity)
    {
        Class<Double> D = Double.TYPE;
        Class<Boolean> B = Boolean.TYPE;
        Class<String> S = String.class;

        functions.put("to_north", OrlangValue.func(() ->
        {
            Rotation rotation = Components.ROTATION.get(entity);
            if (rotation == null)
                return 0;
            Vector3f rotate = new Vector3f(0, 0, -1).rotate(rotation.toQuat());

            double _2PI = 2 * Math.PI;
            double x = rotate.x();
            double z = rotate.z();
            double theta = Math.atan2(-x, z);
            return (float) -Math.toDegrees((theta) % _2PI + Math.PI);
        }));

        functions.put("rotation", OrlangValue.func(D, (axis) ->
        {
            Rotation rotation = Components.ROTATION.get(entity);
            if (rotation == null)
                return 0;
            Vector3f eulerAnglesXYZ = rotation.toQuat().getEulerAnglesXYZ(new Vector3f());
            if (axis == 0.0)
                return Math.toDegrees(eulerAnglesXYZ.x());
            else if (axis == 1.0)
                return Math.toDegrees(eulerAnglesXYZ.y());
            else if (axis == 2.0)
                return Math.toDegrees(eulerAnglesXYZ.z());
            return 0;
        }));

        functions.put("position", OrlangValue.func(D, (axis) ->
        {
            Position position = Components.POSITION.get(entity);
            if (position == null)
                return 0;
            if (axis == 0.0)
                return position.x();
            else if (axis == 1.0)
                return position.y();
            else if (axis == 2.0)
                return position.z();
            return 0;
        }));

        functions.put("total_linear_velocity", OrlangValue.func(() ->
        {
            LinearVelocity linearVelocity = Components.LINEAR_VELOCITY.get(entity);
            if (linearVelocity == null)
                return 0;
            return (double) Vector3f.length(linearVelocity.x(), linearVelocity.y(), linearVelocity.z());
        }));

        functions.put("hasArgument", OrlangValue.func(S, argName -> {
            if (arguments.isEmpty())
                return false;
            OrlangValue orlangValue = arguments.top().get(argName);
            return orlangValue != null;
        }));

        functions.put("argument", OrlangValue.func(S, argName -> {
            OrlangValue orlangValue = arguments.top().get(argName);
            if (orlangValue != null)
                return orlangValue;
            else
                LOGGER.severe("argument '%s' not found".formatted(argName));
            return 0;
        }));

        functions.put("property", OrlangValue.func(S, argName -> {
            Properties properties = Components.PROPERTIES.get(entity);
            return properties.get(Key.parse(Constants.NAMESPACE, argName));
        }));

        functions.put("hasProperty", OrlangValue.func(S, argName -> {
            Properties properties = Components.PROPERTIES.get(entity);
            return properties.contains(Key.parse(Constants.NAMESPACE, argName));
        }));

        functions.put("propertyDefault", OrlangValue.func(S, propertyKey -> {
            Key key;
            try
            {
                key = Key.parse(Constants.NAMESPACE, propertyKey);
            } catch (Exception ex)
            {
                //noinspection CallToPrintStackTrace
                ex.printStackTrace();
                return 0;
            }

            Property property = Registries.PROPERTY.get(key);
            if (property == null)
                return 0;
            return property.getDefaultValue();
        }));

        functions.put("argumentOrDefaultProperty", OrlangValue.func(S, S, (argument, propertyKey) -> {
            OrlangValue orlangValue = arguments.top().get(argument);
            if (orlangValue != null)
                return orlangValue;

            Key key;
            try
            {
                key = Key.parse(Constants.NAMESPACE, propertyKey);
            } catch (Exception ex)
            {
                //noinspection CallToPrintStackTrace
                ex.printStackTrace();
                return 0;
            }

            Property property = Registries.PROPERTY.get(key);
            if (property == null)
                return 0;
            return property.getDefaultValue();
        }));

        functions.put("propertyFrom", OrlangValue.func(S, S, (argName, selection) -> {
            EntitySelection entitySelection = EntitySelection.fromString(selection);
            Optional<Entity> selectedEntity = entityReferenceStack.top().get(entitySelection);
            if (selectedEntity.isPresent())
            {
                Properties properties = Components.PROPERTIES.get(selectedEntity.get());
                return properties.get(Key.parse(Constants.NAMESPACE, argName));
            } else
            {
                LOGGER.severe("entity reference not valid for " + selection);
                return 0;
            }
        }));

        functions.put("tickTime", OrlangValue.func(() -> (double) OrbiterApp.getInstance().getClient().getWorld().ticks));

        functions.put("anim_time", OrlangValue.func(() -> animTime));
        functions.put("any_animation_finished", OrlangValue.func(() -> anyAnimFinished));
        functions.put("all_animations_finished", OrlangValue.func(() -> allAnimsFinished));
    }

    public void pushEntityReference(EntitySelection selection, UnaryOperator<EntityReference> modifier)
    {
        if (entityReferenceStack.isEmpty())
        {
            EntityReference t = EntityReference.empty();
            EntityReference apply = modifier.apply(t);
            if (selection != EntitySelection.UNSELECTED)
                apply = apply.withDefaultSelection(selection);
            entityReferenceStack.push(apply);
        } else
        {
            EntityReference top = entityReferenceStack.top();
            EntityReference modified = modifier.apply(top);
            if (selection != EntitySelection.UNSELECTED)
                modified = modified.withDefaultSelection(selection);
            entityReferenceStack.push(modified);
        }
    }

    public void popEntityReference()
    {
        entityReferenceStack.pop();
    }

    public EntityReference getCurrentReference()
    {
        return entityReferenceStack.isEmpty() ? null : entityReferenceStack.top();
    }

    public void pushFrom(EntityQueryFunctions entityQuery)
    {
        entityReferenceStack.push(entityQuery.getCurrentReference());
        arguments.push(arguments.isEmpty() ? Map.of() : arguments.top());
    }

    public void pop()
    {
        popEntityReference();
        arguments.pop();
    }

    /*
     * Animation
     */

    @Override
    public void setAnimTime(double animTime)
    {
        this.animTime = animTime;
    }

    @Override
    public void setAnyAnimationFinished(boolean flag)
    {
        anyAnimFinished = flag;
    }

    @Override
    public void setAllAnimationsFinished(boolean flag)
    {
        allAnimsFinished = flag;
    }
}
