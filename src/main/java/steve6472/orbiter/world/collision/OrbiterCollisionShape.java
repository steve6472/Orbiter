package steve6472.orbiter.world.collision;

import com.jme3.bullet.collision.shapes.*;
import org.joml.Vector3f;
import steve6472.core.log.Log;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.flare.assets.model.blockbench.Element;
import steve6472.flare.assets.model.blockbench.LoadedModel;
import steve6472.flare.assets.model.blockbench.element.CubeElement;
import steve6472.flare.assets.model.blockbench.element.MeshElement;
import steve6472.flare.assets.model.blockbench.element.NullObjectElement;
import steve6472.flare.registry.FlareRegistries;
import steve6472.orbiter.Convert;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.collision.expression.CollisionExp;
import steve6472.orbiter.world.collision.expression.GroupExp;
import steve6472.orbiter.world.collision.expression.ObjectExp;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/11/2024
 * Project: Orbiter <br>
 */
public record OrbiterCollisionShape(Key key, CollisionShape collisionShape) implements Keyable
{
    private static final Logger LOGGER = Log.getLogger(OrbiterCollisionShape.class);

    private static final ShapeParser SHAPE_PARSER = new ShapeParser();
    public static final Map<String, Function<ObjectExp, CollisionShape>> SHAPE_CONSTRUCTORS = new HashMap<>();
    public static final Map<String, BiFunction<ObjectExp, CollisionTransform, CollisionTransform>> PROPERTIES = new HashMap<>();

    static
    {
        /*
         * Shapes
         */

        // sphere(radius)
        SHAPE_CONSTRUCTORS.put("sphere", obj -> new SphereCollisionShape(obj.params()[0] * 0.5f));
        // capsule(radius, height)
        SHAPE_CONSTRUCTORS.put("capsule", obj -> new CapsuleCollisionShape(obj.params()[0] * 0.5f, obj.params()[1] * 0.5f));
        // box(x, y, z)
        SHAPE_CONSTRUCTORS.put("box", obj -> new BoxCollisionShape(obj.params()[0] * 0.5f, obj.params()[1] * 0.5f, obj.params()[2] * 0.5f));
        // cone(radius, height, axis)   cone(radius, height, [1])
        SHAPE_CONSTRUCTORS.put("cone", obj -> new ConeCollisionShape(obj.params()[0] * 0.5f, obj.params()[1] * 0.5f, obj.params().length == 3 ? (int) obj.params()[2] : 1));

        /*
         * Properties
         */

        // offset(x, y, z)
        PROPERTIES.put("offset", (obj, transform) -> transform.offset(obj.params()[0], obj.params()[1], obj.params()[2]));
    }

    public static void load()
    {
        for (Key key : FlareRegistries.STATIC_LOADED_MODEL.keys())
        {
            LoadedModel model = FlareRegistries.STATIC_LOADED_MODEL.get(key);

            CollisionShape compound = loadCollisionShape(model);
            if (compound != null)
                Registries.COLLISION.register(new OrbiterCollisionShape(key, compound));
        }
    }

    private static CollisionShape loadCollisionShape(LoadedModel model)
    {
        List<CollisionTransform> shapes = new ArrayList<>();

        model
            .elements()
            .stream()
            .filter(e -> e.name().startsWith("collision"))
            .forEach(e ->
            {
                Collection<CollisionTransform> shape = createCollisionShape(e);
                if (shape != null)
                    shapes.addAll(shape);
            });

        shapes.removeIf(p -> !p.hasShape());

        return compound(shapes);
    }

    private static Collection<CollisionTransform> createCollisionShape(Element element)
    {
        if (element instanceof CubeElement elm)
        {
            BoxCollisionShape shape = new BoxCollisionShape(Convert.jomlToPhys(elm.to().sub(elm.from(), new Vector3f()).mul(0.5f).absolute()));
            // Offsets, rotations, scaling..., also this from outliner ........
            Vector3f div = new Vector3f(elm.from()).add(elm.to()).div(2f);
            return List.of(new CollisionTransform(shape).offset(div.x, div.y, div.z));
        } else if (element instanceof MeshElement _)
        {
            return null;
        } else if (element instanceof NullObjectElement nullElm)
        {
            return loadFromNull(element.name(), nullElm.position());
        }

        return null;
    }

    private static Collection<CollisionTransform> loadFromNull(String text, Vector3f position)
    {
        CollisionExp collisionExp = SHAPE_PARSER.parse(text);
        GroupExp group = collisionExp.exp();

        List<CollisionTransform> transforms = new ArrayList<>(loadGroup(group, new CollisionTransform().offset(position.x, position.y, position.z)));

        if (transforms.isEmpty())
            LOGGER.warning("Returning no shape from null object, this is an incomplete collision shape");
        return transforms;
    }

    private static Collection<CollisionTransform> loadGroup(GroupExp group, CollisionTransform transform)
    {
        if (group.isEmpty())
            return List.of();

        List<CollisionTransform> shapes = new ArrayList<>();

        for (ObjectExp objectExp : group.getAll(PROPERTIES.keySet()))
        {
            transform = PROPERTIES.get(objectExp.type()).apply(objectExp, transform);
        }

        if (group.hasGroups())
        {
            for (GroupExp groupExp : group.getGroups())
            {
                shapes.addAll(loadGroup(groupExp, transform));
            }
        }

        Collection<ObjectExp> shapeList = group.getAll(SHAPE_CONSTRUCTORS.keySet());
        if (shapeList.isEmpty())
            return shapes;

        for (ObjectExp objectExp : shapeList)
        {
            CollisionShape shape = SHAPE_CONSTRUCTORS.get(objectExp.type()).apply(objectExp);
            transform = transform.shape(shape);

            shapes.add(transform);
        }

        return shapes;
    }

    private static CollisionShape compound(List<CollisionTransform> shapes)
    {
        if (shapes.isEmpty())
        {
            return null;
        } else if (shapes.size() == 1)
        {
            CollisionTransform col = shapes.getFirst();
            if (!col.hasShape())
                return null;
            if (col.isPrimitive())
                return col.shape();

            CompoundCollisionShape shape = new CompoundCollisionShape();
            Vector3f offset = col.offset();
            shape.addChildShape(col.shape(), offset.x, offset.y, offset.z);
            return shape;
        }

        CompoundCollisionShape shape = new CompoundCollisionShape();

        for (CollisionTransform collisionTransform : shapes)
        {
            if (!collisionTransform.hasShape())
                continue;

            Vector3f offset = collisionTransform.offset();
            shape.addChildShape(collisionTransform.shape(), offset.x, offset.y, offset.z);
        }

        return shape;
    }
}
