package steve6472.orbiter.world.ecs.core;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.core.registry.Serializable;

/**
 * Created by steve6472
 * Date: 5/3/2024
 * Project: Domin <br>
 */
public class Component<T> implements Keyable, Serializable<T>
{
    private final Key key;
    private final Codec<T> persistentCodec;
    private final BufferCodec<ByteBuf, T> networkCodec;
    // TODO: private final Supplier<T> blueprint
    private final Class<T> clazz;

    private Component(Key key, Class<T> clazz, Codec<T> persistentCodec, BufferCodec<ByteBuf, T> networkCodec)
    {
        this.key = key;
        this.clazz = clazz;
        this.persistentCodec = persistentCodec;
        this.networkCodec = networkCodec;
    }

    public static <T> Builder<T> builder()
    {
        return new Builder<>();
    }

    @Override
    public String toString()
    {
        return "Component{" + "key='" + key + '\'' + '}';
    }

    @Override
    public Key key()
    {
        return key;
    }

    public Class<T> componentClass()
    {
        return clazz;
    }

    @Override
    public Codec<T> codec()
    {
        return persistentCodec;
    }

    public BufferCodec<ByteBuf, T> getNetworkCodec()
    {
        return networkCodec;
    }

    public static class Builder<T>
    {
        private Codec<T> persistentCodec;
        private BufferCodec<ByteBuf, T> networkCodec;
        private Key key;
        private Class<T> clazz;

        private Builder()
        {

        }

        public Builder<T> persistent(Codec<T> persistentCodec)
        {
            this.persistentCodec = persistentCodec;
            return this;
        }

        public Builder<T> network(BufferCodec<ByteBuf, T> networkCodec)
        {
            this.networkCodec = networkCodec;
            return this;
        }

        public Builder<T> clazz(Class<T> clazz)
        {
            this.clazz = clazz;
            return this;
        }

        public Builder<T> _key(Key key)
        {
            this.key = key;
            return this;
        }

        public Component<T> build()
        {
            return new Component<>(key, clazz, persistentCodec, networkCodec);
        }
    }
}
