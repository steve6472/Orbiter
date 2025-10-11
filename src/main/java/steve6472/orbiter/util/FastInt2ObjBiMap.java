package steve6472.orbiter.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * Created by steve6472
 * Date: 10/4/2025
 * Project: Orbiter <br>
 */
public class FastInt2ObjBiMap<T>
{
    private final Int2ObjectMap<T> intToObj = new Int2ObjectOpenHashMap<>();
    private final Object2IntMap<T> objToInt = new Object2IntOpenHashMap<>();

    public void put(int key, T value)
    {
        // Optionally, you may want to check for collisions / existing entries
        // Remove old mapping from reverse if replacing, etc.
        T old = intToObj.put(key, value);
        if (old != null)
        {
            objToInt.removeInt(old);
        }
        objToInt.put(value, key);
    }

    public T getByInt(int key)
    {
        return intToObj.get(key);
    }

    public int getByObj(T obj)
    {
        // FastUtil’s Object2IntMap returns a default if not present (might be 0 or configured default)
        return objToInt.getInt(obj);
    }

    public void removeByInt(int key)
    {
        T value = intToObj.remove(key);
        if (value != null)
        {
            objToInt.removeInt(value);
        }
    }

    public void removeByObj(T obj)
    {
        int key = objToInt.removeInt(obj);
        if (key != objToInt.defaultReturnValue())
        {
            intToObj.remove(key);
        }
    }

    public void clear()
    {
        objToInt.clear();
        intToObj.clear();
    }
}
