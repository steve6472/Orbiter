package steve6472.orbiter.world;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import org.joml.Vector3f;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.orbiter.Convert;
import steve6472.orbiter.Registries;
import steve6472.volkaniums.assets.model.blockbench.Element;
import steve6472.volkaniums.assets.model.blockbench.LoadedModel;
import steve6472.volkaniums.assets.model.blockbench.element.CubeElement;
import steve6472.volkaniums.assets.model.blockbench.element.MeshElement;
import steve6472.volkaniums.assets.model.blockbench.element.NullObjectElement;
import steve6472.volkaniums.registry.VolkaniumsRegistries;

import java.util.*;

/**
 * Created by steve6472
 * Date: 10/11/2024
 * Project: Orbiter <br>
 */
public record OrbiterCollisionShape(Key key, CollisionShape collisionShape) implements Keyable
{
    public static OrbiterCollisionShape load()
    {
        for (Key key : VolkaniumsRegistries.STATIC_LOADED_MODEL.keys())
        {
            LoadedModel model = VolkaniumsRegistries.STATIC_LOADED_MODEL.get(key);

            List<CollisionShape> shapes = new ArrayList<>();

            model.elements().stream().filter(e -> e.name().startsWith("collision")).forEach(e ->
            {
                CollisionShape shape = createCollisionShape(e);
                if (shape != null)
                    shapes.add(shape);
            });

            CollisionShape compound = compound(shapes);
            if (compound != null)
            {
                Registries.COLLISION.register(new OrbiterCollisionShape(key, compound));
            }
        }
        return new OrbiterCollisionShape(null, null);
    }

    private static CollisionShape createCollisionShape(Element element)
    {
        if (element instanceof CubeElement elm)
        {
            BoxCollisionShape shape = new BoxCollisionShape(Convert.jomlToPhys(elm.to().sub(elm.from(), new Vector3f()).mul(0.5f).absolute()));
            // Offsets, rotations, scaling..., also this from outliner ........
            return shape;
        } else if (element instanceof MeshElement elm)
        {
            return null;
        } else if (element instanceof NullObjectElement)
        {
            String name = element.name();
            Map<String, String> map = extractProperties(name);
            if (map.containsKey("type"))
            {
                String s = map.get("type");
                if (s.contains("("))
                    s = s.substring(0, s.indexOf("("));

                if (s.equals("capsule"))
                {
                    String type = map.get("type");
                    String properties = type.substring(s.length() + 1, type.lastIndexOf(")"));
                    String[] split = properties.split(",");
                    float radius = Float.parseFloat(split[0]);
                    float height = Float.parseFloat(split[1]);

                    return new CapsuleCollisionShape(radius * 0.5f, height * 0.5f);
                }

                if (s.equals("sphere"))
                {
                    String type = map.get("type");
                    String properties = type.substring(s.length() + 1, type.lastIndexOf(")"));
                    String[] split = properties.split(",");
                    float radius = Float.parseFloat(split[0]);

                    return new SphereCollisionShape(radius * 0.5f);
                }
            }

            return null;
        }

        return null;
    }

    private static Map<String, String> extractProperties(String text)
    {
        Map<String, String> map = new HashMap<>();
        String[] split = text.split(";");
        for (String s : split)
        {
            if (s.contains(":"))
            {
                String[] split1 = s.split(":");
                map.put(split1[0].trim(), split1[1].trim());
            }
        }

        return map;
    }

    private static CollisionShape compound(List<CollisionShape> shapes)
    {
        if (shapes.isEmpty())
            return null;
        else if (shapes.size() == 1)
            return shapes.getFirst();

        // compound
        throw new RuntimeException("Compound shape not yet supported!");
    }
}
