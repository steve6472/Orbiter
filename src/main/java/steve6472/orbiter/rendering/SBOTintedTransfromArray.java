package steve6472.orbiter.rendering;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import steve6472.flare.assets.model.VkModel;
import steve6472.flare.struct.Struct;
import steve6472.flare.struct.StructDef;

import java.util.*;
import java.util.function.ToIntFunction;

public class SBOTintedTransfromArray<T extends VkModel>
{
    public static final int MAX_TRANSFORMS = 32768;

    private static class TintedTransform
    {
        Matrix4f transform = new Matrix4f();
        Vector4f tint = new Vector4f(1, 1, 1, 1);

        public Struct toStruct()
        {
            return OrbiterSBO.ENTRY.create(transform, tint);
        }
    }

    private final Struct[] structs = new Struct[MAX_TRANSFORMS];
    private final TintedTransform[] transforms = new TintedTransform[MAX_TRANSFORMS];
    private final Area rootArea;
    private final LinkedHashMap<T, Area> areas = new LinkedHashMap<>(16);
    private int totalIndex;

    /// @param rootModel Use error model
    public SBOTintedTransfromArray(T rootModel)
    {
        for (int i = 0; i < transforms.length; i++)
        {
            transforms[i] = new TintedTransform();
            structs[i] = transforms[i].toStruct();
        }

        rootArea = new Area(rootModel);
    }

    public void start()
    {
        getAreas().forEach(Area::start);
        totalIndex = 0;
    }

    public Object getTransformsArray()
    {
        return structs;
    }

    public Collection<Area> getAreas()
    {
        return areas.values();
    }

    public Area addArea(T type)
    {
        if (isMapped(type))
            return getAreaByType(type);
//        Preconditions.checkTrue(isMapped(type), "Area already exists");
        Area lastArea = getLastArea(rootArea);
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

    public boolean isMapped(T type)
    {
        return getAreaByType(type) != null;
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

        public Matrix4f getTransform()
        {
            return transforms[totalIndex].transform.identity();
        }

        public Vector4f getTint()
        {
            return transforms[totalIndex].tint.set(1, 1, 1, 1);
        }

        public void update()
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
