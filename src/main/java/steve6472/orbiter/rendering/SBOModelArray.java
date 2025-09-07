package steve6472.orbiter.rendering;

import steve6472.flare.assets.model.VkModel;

import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class SBOModelArray<T extends VkModel, E extends SBOModelArray.Entry>
{
    public static final int MAX_TRANSFORMS = 32768;

    public interface Entry
    {
        void reset();
        Object toStruct();
    }

    private final Object[] structs;
    @SuppressWarnings("unchecked")
    private final E[] entries = (E[]) new SBOModelArray.Entry[MAX_TRANSFORMS];
    private final Area rootArea;
    private final LinkedHashMap<T, Area> areas = new LinkedHashMap<>(16);
    private int totalIndex;

    /// @param rootModel Use error model
    public SBOModelArray(T rootModel, Supplier<E> constructor, IntFunction<Object[]> entryArray)
    {
        structs = entryArray.apply(MAX_TRANSFORMS);
        for (int i = 0; i < entries.length; i++)
        {
            entries[i] = constructor.get();
            structs[i] = entries[i].toStruct();
        }

        rootArea = new Area(rootModel);
    }

    public void start()
    {
        getAreas().forEach(Area::start);
        totalIndex = 0;
    }

    public Object getEntriesArray()
    {
        return structs;
    }

    public Collection<Area> getAreas()
    {
        return areas.values();
    }

    public Area addArea(T type)
    {
        Area lastArea = getAreaByType(type);
        if (lastArea != null)
            return lastArea;

        lastArea = getLastArea(rootArea);
        Area newArea = new Area(type);
        newArea.index = lastArea.index + 1;
        lastArea.rightArea = newArea;
        areas.put(type, newArea);
        return newArea;
    }

    public <A> void sort(List<A> objs, ToIntFunction<A> keyExtractor)
    {
        objs.sort(Comparator.comparingInt(keyExtractor));
    }

    private Area getLastArea(Area area)
    {
        if (area.rightArea != null)
            return getLastArea(area.rightArea);
        return area;
    }

    public Area getAreaByType(T type)
    {
        return areas.get(type);
    }

    public Area getAreaByIndex(int index)
    {
        Optional<Area> first = getAreas().stream().filter(a -> a.index == index).findFirst();
        return first.orElse(null);
    }

    public class Area
    {
        T modelType;
        Area rightArea;
        int index;
        int toRender;

        private Area(T modelType)
        {
            this.modelType = modelType;
        }

        public E getEntry()
        {
            entries[totalIndex].reset();
            return entries[totalIndex];
        }

        public void moveIndex()
        {
            toRender++;
            totalIndex++;
        }

        private void start()
        {
            toRender = 0;
        }

        public int index()
        {
            return index;
        }

        public T modelType()
        {
            return modelType;
        }

        public int toRender()
        {
            return toRender;
        }
    }
}
