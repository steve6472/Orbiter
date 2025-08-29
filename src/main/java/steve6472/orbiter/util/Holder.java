package steve6472.orbiter.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import steve6472.core.registry.*;
import steve6472.orbiter.Constants;

import java.util.Objects;

/**
 * Created by steve6472
 * Date: 8/29/2025
 * Project: Orbiter <br>
 */
public interface Holder<T extends Keyable>
{
    T get();

    Key key();

    static <T extends Keyable> Holder<T> fromValue(T value)
    {
        return new ValueHolder<>(value);
    }

    static <T extends Keyable> Codec<Holder<T>> create(ObjectRegistry<T> registry)
    {
        return Constants.KEY_CODEC.flatXmap(key -> DataResult.success(new ObjectRegistryHolder<>(key, registry)), holder -> DataResult.success(holder.key()));
    }

    static <T extends Keyable & Serializable<T>> Codec<Holder<T>> create(Registry<T> registry)
    {
        return Constants.KEY_CODEC.flatXmap(key -> DataResult.success(new RegistryHolder<>(key, registry)), holder -> DataResult.success(holder.key()));
    }

    final class ValueHolder<T extends Keyable> implements Holder<T>
    {
        private final T value;

        public ValueHolder(T value)
        {
            Objects.requireNonNull(value, "ValueHolder cannot be created with a null value");
            this.value = value;
        }

        @Override
        public T get()
        {
            return value;
        }

        @Override
        public Key key()
        {
            return value.key();
        }
    }

    final class ObjectRegistryHolder<T extends Keyable> implements Holder<T>
    {
        private final Key key;
        private final ObjectRegistry<T> registry;
        private T value; // lazily initialized

        public ObjectRegistryHolder(Key key, ObjectRegistry<T> registry)
        {
            Objects.requireNonNull(key, "Key must not be null");
            Objects.requireNonNull(registry, "Registry must not be null");
            this.key = key;
            this.registry = registry;
        }

        @Override
        public T get()
        {
            if (value == null)
            {
                value = registry.get(key);
                if (value == null)
                {
                    throw new NullPointerException("Entry for '%s' in registry '%s' not found".formatted(key, registry.getRegistryKey()));
                }
            }
            return value;
        }

        @Override
        public Key key()
        {
            return key;
        }
    }

    final class RegistryHolder<T extends Keyable & Serializable<T>> implements Holder<T>
    {
        private final Key key;
        private final Registry<T> registry;
        private T value; // lazily initialized

        public RegistryHolder(Key key, Registry<T> registry)
        {
            Objects.requireNonNull(key, "Key must not be null");
            Objects.requireNonNull(registry, "Registry must not be null");
            this.key = key;
            this.registry = registry;
        }

        @Override
        public T get()
        {
            if (value == null)
            {
                value = registry.get(key);
                if (value == null)
                {
                    throw new NullPointerException("Entry for '%s' in registry '%s' not found".formatted(key, registry.getRegistryKey()));
                }
            }
            return value;
        }

        @Override
        public Key key()
        {
            return key;
        }
    }
}