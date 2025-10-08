package steve6472.orbiter.world;

import com.github.stephengold.joltjni.Body;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import steve6472.orbiter.util.FastInt2ObjBiMap;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/7/2025
 * Project: Orbiter <br>
 */
public class JoltBodies
{
    // TODO: split to client & host ?
    private final FastInt2ObjBiMap<UUID> idUUIDmap = new FastInt2ObjBiMap<>();
    private final Int2ObjectMap<Body> idBodyMap = new Int2ObjectOpenHashMap<>();

    public void addBody(UUID uuid, Body body)
    {
        int id = body.getId();
        idUUIDmap.put(id, uuid);
        idBodyMap.put(id, body);
    }

    /// Doesn't remove from physics world!
    public void removeBody(int id)
    {
        idUUIDmap.removeByInt(id);
        idBodyMap.remove(id);
    }

    /// Doesn't remove from physics world!
    public void removeBody(UUID uuid)
    {
        int id = idUUIDmap.getByObj(uuid);
        idUUIDmap.removeByObj(uuid);
        idBodyMap.remove(id);
    }

    public Body getBodyById(int id)
    {
        return idBodyMap.get(id);
    }

    public Body getBodyByUUID(UUID uuid)
    {
        return idBodyMap.get(getIdByUUID(uuid));
    }

    public UUID getUUIDById(int id)
    {
        return idUUIDmap.getByInt(id);
    }

    public int getIdByUUID(UUID uuid)
    {
        return idUUIDmap.getByObj(uuid);
    }
}
