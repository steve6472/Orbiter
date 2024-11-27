package steve6472.orbiter.world.ecs.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.registry.Key;
import steve6472.flare.assets.model.Model;
import steve6472.flare.registry.FlareRegistries;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class IndexModel
{
    public static final Codec<IndexModel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Key.CODEC.fieldOf("key").forGetter(e -> e.model().key())
    ).apply(instance, key -> new IndexModel(FlareRegistries.STATIC_MODEL.get(key))));

    public static final BufferCodec<ByteBuf, IndexModel> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.KEY, e -> e.model().key(),
        key -> new IndexModel(FlareRegistries.STATIC_MODEL.get(key)));

    private final Model model;
    private int modelIndex = -1;

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
}
