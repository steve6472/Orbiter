package steve6472.orbiter.world.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.flare.assets.model.Model;
import steve6472.flare.registry.FlareRegistries;
import steve6472.orbiter.Constants;
import steve6472.orbiter.util.ComponentCodec;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class IndexModel implements Component, Pool.Poolable
{
    public static final Codec<IndexModel> CODEC = ComponentCodec.xmap(Constants.KEY_CODEC, key -> () -> new IndexModel(FlareRegistries.STATIC_MODEL.get(key)), model -> model.model.key());

    public static final BufferCodec<ByteBuf, IndexModel> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.KEY, e -> e.model().key(),
        key -> new IndexModel(FlareRegistries.STATIC_MODEL.get(key)));

    public static final int UNSET_MODEL_INDEX = -1;

    public Model model;
    private int modelIndex = UNSET_MODEL_INDEX;

    public IndexModel() {}

    public IndexModel(Model model)
    {
        this.model = model;
    }

    public Model model()
    {
        return model;
    }

    public int modelIndex()
    {
        return modelIndex;
    }

    public void setModelIndex(int newIndex)
    {
        if (modelIndex != -1)
            throw new RuntimeException("Tried to change model index!");
        this.modelIndex = newIndex;
    }

    @Override
    public String toString()
    {
        return "IndexModel{" + "model=" + model.key() + ", modelIndex=" + modelIndex + '}';
    }

    @Override
    public void reset()
    {
        model = null;
        modelIndex = UNSET_MODEL_INDEX;
    }
}
