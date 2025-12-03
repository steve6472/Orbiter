package steve6472.orbiter.actions.filter;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector3f;
import steve6472.core.util.ExtraCodecs;
import steve6472.orbiter.util.Intersections;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.physics.Position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by steve6472
 * Date: 11/28/2025
 * Project: Orbiter <br>
 */
public record ShapeSphere(int max, Sort sort, Filter initial, float radius, Vector3f offset, boolean offsetLocal, boolean includeSelf) implements Filter
{
    public static final Codec<ShapeSphere> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Filter.maxCodec(),
        Filter.sortCodec(),
        Filter.initialCodec(),
        Codec.FLOAT.fieldOf("radius").forGetter(ShapeSphere::radius),
        ExtraCodecs.VEC_3F.optionalFieldOf("offset", new Vector3f()).forGetter(ShapeSphere::offset),
        Codec.BOOL.optionalFieldOf("offset_local", false).forGetter(ShapeSphere::offsetLocal),
        Codec.BOOL.optionalFieldOf("include_self", false).forGetter(ShapeSphere::includeSelf)
    ).apply(instance, ShapeSphere::new));

    @Override
    public Collection<Entity> initialSelection(World world, Entity caller)
    {
        Vector3f pos = new Vector3f();
        Components.POSITION.ifPresent(caller, position -> pos.set(position.toVec3f()));
        offsetPosition(pos, caller);
        List<Entity> filtered = new ArrayList<>();

        // TODO: replace with octree accelerated structure
        ImmutableArray<Entity> entitiesFor = world.ecsEngine().getEntitiesFor(Family.all(Position.class).get());

        for (Entity entity : entitiesFor)
        {
            if (!includeSelf && entity == caller)
                continue;

            if (Intersections.testSphereEntity(pos, radius, entity))
            {
                filtered.add(entity);
            }
        }
        return filtered;
    }

    @Override
    public Collection<Entity> filterEntities(Entity caller, Collection<Entity> input)
    {
        Vector3f pos = new Vector3f();
        Components.POSITION.ifPresent(caller, position -> pos.set(position.toVec3f()));
        offsetPosition(pos, caller);
        List<Entity> filtered = new ArrayList<>(input.size());

        for (Entity entity : input)
        {
            if (Intersections.testSphereEntity(pos, radius, entity))
                filtered.add(entity);
        }

        return filtered;
    }

    /// Modifies position
    private void offsetPosition(Vector3f position, Entity caller)
    {
        if (offsetLocal)
        {
            Components.ROTATION.ifPresent(caller, rotation -> {
                Vector3f offset = new Vector3f(this.offset).rotate(rotation.toQuat());
                position.add(offset);
            });
        } else
        {
            position.add(offset);
        }
    }

    @Override
    public FilterType<?> getType()
    {
        return FilterType.SHAPE_SPHERE;
    }
}
