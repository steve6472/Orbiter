package steve6472.orbiter.world.ecs.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.core.registry.Serializable;

import java.util.function.Consumer;

/**
 * Created by steve6472
 * Date: 5/3/2024
 * Project: Domin <br>
 */
public class ComponentEntry<T extends Component> implements Keyable, Serializable<T>
{
    private static int networkCounter = 0;

    private final Key key;
    private final Codec<T> persistentCodec;
    private final BufferCodec<ByteBuf, T> networkCodec;
    // TODO: private final Supplier<T> blueprint
    private final Class<T> clazz;
    private final ComponentMapper<T> mapper;
    private final int networkID;

    private ComponentEntry(Key key, int networkID, Class<T> clazz, Codec<T> persistentCodec, BufferCodec<ByteBuf, T> networkCodec)
    {
        this.key = key;
        this.networkID = networkID;
        this.clazz = clazz;
        this.persistentCodec = persistentCodec;
        this.networkCodec = networkCodec;
        this.mapper = ComponentMapper.getFor(clazz);
    }

    /*
     * Mapper delegates
     */
    public T get(Entity entity)
    {
        return mapper.get(entity);
    }

    public boolean has(Entity entity)
    {
        return mapper.has(entity);
    }

    public void ifPresent(Entity entity, Consumer<T> func)
    {
        T t = get(entity);
        if (t != null)
            func.accept(t);
    }

    public void ifPresentOrElse(Entity entity, Consumer<T> func, Runnable emptyAction)
    {
        T t = get(entity);
        if (t != null)
            func.accept(t);
        else
            emptyAction.run();
    }

    public T create(PooledEngine engine)
    {
        return engine.createComponent(clazz);
    }

    public static <T extends Component> Builder<T> builder()
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

    public int networkID()
    {
        return networkID;
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

    public static class Builder<T extends Component>
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

        public ComponentEntry<T> build()
        {
            int networkId = -1;
            if (networkCodec != null)
                networkId = networkCounter++;

            return new ComponentEntry<>(key, networkId, clazz, persistentCodec, networkCodec);
        }
    }
}
