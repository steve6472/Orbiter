package steve6472.orbiter.rendering.snapshot.snapshots;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.github.stephengold.joltjni.BodyInterface;
import com.github.stephengold.joltjni.Quat;
import com.github.stephengold.joltjni.RVec3;
import org.joml.Quaternionf;
import steve6472.flare.assets.model.Model;
import steve6472.orbiter.world.ecs.Components;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 11/4/2025
 * Project: Orbiter <br>
 */
public class StaticModelSnapshot implements Pool.Poolable, UUIDSnapshot
{
    private static final RVec3 STORE_POSITION = new RVec3();
    private static final Quat STORE_ROTATION = new Quat();

    public float x, y, z;
    public final Quaternionf rotation = new Quaternionf();
    public Model model;
    public UUID uuid;

    @Override
    public void reset()
    {
        model = null;
        uuid = null;
    }

    public void fromEntity(Entity entity, UUID uuid, BodyInterface bodyInterface, int bodyId)
    {
        this.uuid = uuid;
        this.model = Components.MODEL.get(entity).model();

        if (bodyInterface.isAdded(bodyId))
        {
            bodyInterface.getPositionAndRotation(bodyId, STORE_POSITION, STORE_ROTATION);
            x = STORE_POSITION.x();
            y = STORE_POSITION.y();
            z = STORE_POSITION.z();

            rotation.x = STORE_ROTATION.getX();
            rotation.y = STORE_ROTATION.getY();
            rotation.z = STORE_ROTATION.getZ();
            rotation.w = STORE_ROTATION.getW();
        } else
        {
            Components.POSITION.ifPresent(entity, position -> {
                x = position.x();
                y = position.y();
                z = position.z();
            });

            Components.ROTATION.ifPresent(entity, rotation -> this.rotation.set(rotation.x(), rotation.y(), rotation.z(), rotation.w()));
        }
    }

    @Override
    public UUID uuid()
    {
        return uuid;
    }
}
