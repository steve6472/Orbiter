package steve6472.orbiter.rendering.snapshot.snapshots.group;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.Array;
import com.github.stephengold.joltjni.BodyInterface;
import com.github.stephengold.joltjni.PhysicsSystem;
import steve6472.orbiter.rendering.snapshot.SnapshotPools;
import steve6472.orbiter.rendering.snapshot.snapshots.AnimatedModelSnapshot;
import steve6472.orbiter.rendering.snapshot.snapshots.StaticModelSnapshot;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.components.AnimatedModel;
import steve6472.orbiter.world.ecs.components.IndexModel;
import steve6472.orbiter.world.ecs.components.UUIDComp;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 11/4/2025
 * Project: Orbiter <br>
 */
public class ModelSnapshots
{
    public static final Family FAMILY_STATIC = Family.all(IndexModel.class, UUIDComp.class).get();
    public static final Family FAMILY_ANIMATED = Family.all(AnimatedModel.class, UUIDComp.class).get();

    public final Array<StaticModelSnapshot> staticEntities = new Array<>(false, 16);
    public final Array<AnimatedModelSnapshot> animatedEntities = new Array<>(false, 16);

    public void createSnapshot(SnapshotPools pools, Engine ecsEngine, World world, UUID clientUUID)
    {
        PhysicsSystem physics = world.physics();
        BodyInterface bodyInterface = physics.getBodyInterface();

        for (Entity entity : ecsEngine.getEntitiesFor(FAMILY_STATIC))
        {
            UUID uuid = Components.UUID.get(entity).uuid();

            // Disable rendering of client model
            if (uuid.equals(clientUUID))
                continue;

            int bodyId = world.bodyMap().getIdByUUID(uuid);

            StaticModelSnapshot snapshot = pools.staticModelPool.obtain();
            snapshot.fromEntity(entity, uuid, bodyInterface, bodyId);
            staticEntities.add(snapshot);
        }

        for (Entity entity : ecsEngine.getEntitiesFor(FAMILY_ANIMATED))
        {
            UUID uuid = Components.UUID.get(entity).uuid();

            // Disable rendering of client model
            if (uuid.equals(clientUUID))
                continue;

            AnimatedModelSnapshot snapshot = pools.animatedModelPool.obtain();
            snapshot.fromEntity(entity, uuid);
            animatedEntities.add(snapshot);
        }
    }
}
