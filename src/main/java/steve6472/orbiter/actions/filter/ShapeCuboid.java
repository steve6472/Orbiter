package steve6472.orbiter.actions.filter;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import steve6472.orbiter.util.AABB;
import steve6472.orbiter.util.Intersections;
import steve6472.orbiter.util.OBB;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.OrlangEnv;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.components.physics.Rotation;
import steve6472.orlang.codec.OrVec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by steve6472
 * Date: 11/30/2025
 * Project: Orbiter <br>
 */
public record ShapeCuboid(int max, Sort sort, Filter initial, OrVec3 halfSizes, OrVec3 offset, boolean offsetLocal, boolean rotateLocal, boolean includeSelf) implements Filter
{
    public static final Codec<ShapeCuboid> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Filter.maxCodec(),
        Filter.sortCodec(),
        Filter.initialCodec(),
        OrVec3.CODEC.fieldOf("half_sizes").forGetter(ShapeCuboid::halfSizes),
        OrVec3.CODEC.optionalFieldOf("offset", new OrVec3()).forGetter(ShapeCuboid::offset),
        Codec.BOOL.optionalFieldOf("offset_local", false).forGetter(ShapeCuboid::offsetLocal),
        Codec.BOOL.optionalFieldOf("rotate_local", false).forGetter(ShapeCuboid::rotateLocal),
        Codec.BOOL.optionalFieldOf("include_self", false).forGetter(ShapeCuboid::includeSelf)
    ).apply(instance, ShapeCuboid::new));

    @Override
    public Collection<Entity> initialSelection(World world, Entity caller)
    {
        OrlangEnv orlangEnv = Components.ENVIRONMENT.get(caller);
        if (orlangEnv == null)
            return List.of();

        Vector3f pos = new Vector3f();
        Components.POSITION.ifPresent(caller, position -> pos.set(position.toVec3f()));
        offsetPosition(pos, caller, orlangEnv);
        List<Entity> filtered = new ArrayList<>();
        Vector3f sizes = new Vector3f(halfSizes.evaluateAndGet(orlangEnv.env));

        // TODO: replace with octree accelerated structure
        ImmutableArray<Entity> entitiesFor = world.ecsEngine().getEntitiesFor(Family.all(Position.class).get());

        Rotation rotation = Components.ROTATION.get(caller);
        Quaternionf quat = rotation != null ? rotation.toQuat() : null;

        for (Entity entity : entitiesFor)
        {
            if (!includeSelf && entity == caller)
                continue;

            boolean test;

            if (rotateLocal && quat != null)
            {
                test = Intersections.testObbEntity(OBB.fromAabbAndRotation(AABB.fromCenterHalfSize(pos, sizes), quat), entity);
            } else
            {
                test = Intersections.testAabbEntity(AABB.fromCenterHalfSize(pos, sizes), entity);
            }

            if (test)
                filtered.add(entity);
        }
        return filtered;
    }

    @Override
    public Collection<Entity> filterEntities(Entity caller, Collection<Entity> input)
    {
        OrlangEnv orlangEnv = Components.ENVIRONMENT.get(caller);
        if (orlangEnv == null)
            return List.of();

        Vector3f pos = new Vector3f();
        Components.POSITION.ifPresent(caller, position -> pos.set(position.toVec3f()));
        offsetPosition(pos, caller, orlangEnv);
        List<Entity> filtered = new ArrayList<>(input.size());
        Vector3f sizes = new Vector3f(halfSizes.evaluateAndGet(orlangEnv.env));

        Rotation rotation = Components.ROTATION.get(caller);
        Quaternionf quat = rotation != null ? rotation.toQuat() : null;

        for (Entity entity : input)
        {
            boolean test;

            if (rotateLocal && quat != null)
            {
                test = Intersections.testObbEntity(OBB.fromAabbAndRotation(AABB.fromCenterHalfSize(pos, sizes), quat), entity);
            } else
            {
                test = Intersections.testAabbEntity(AABB.fromCenterHalfSize(pos, sizes), entity);
            }

            if (test)
                filtered.add(entity);
        }

        return filtered;
    }

    @Override
    public FilterType<?> getType()
    {
        return FilterType.SHAPE_CUBOID;
    }

    /// Modifies position
    private void offsetPosition(Vector3f position, Entity caller, OrlangEnv env)
    {
        if (env == null)
            return;

        if (offsetLocal)
        {
            Components.ROTATION.ifPresent(caller, rotation -> {
                Vector3f offset = new Vector3f(this.offset.evaluateAndGet(env.env)).rotate(rotation.toQuat());
                position.add(offset);
            });
        } else
        {
            position.add(new Vector3f(offset.evaluateAndGet(env.env)));
        }
    }
}
