package steve6472.orbiter.world.ecs.components;

import com.badlogic.ashley.core.Component;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.jetbrains.annotations.NotNull;
import steve6472.core.log.Log;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.Registries;
import steve6472.orbiter.network.ExtraBufferCodecs;
import steve6472.orbiter.properties.*;
import steve6472.orbiter.util.ComponentCodec;
import steve6472.orbiter.util.ValueSource;
import steve6472.orlang.Orlang;
import steve6472.orlang.codec.OrCode;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Created by steve6472
 * Date: 11/24/2025
 * Project: Orbiter <br>
 */
public class Properties implements Component
{
    private static final Logger LOGGER = Log.getLogger(Properties.class);

    // CODEC at bottom, it uggy

    public static final BufferCodec<ByteBuf, Properties> BUFFER_CODEC = BufferCodec.of((buffer, properties) -> {
        ExtraBufferCodecs.VAR_INT.encode(buffer, properties.map.size());
        properties.map.forEach((key, pair) -> {
            Property property = pair.getFirst();
            BufferCodecs.KEY.encode(buffer, key);
            property.encodeBuffer(buffer, pair.getSecond());
        });
    }, buffer -> {
        int propertyCount = ExtraBufferCodecs.VAR_INT.decode(buffer);
        Map<Key, Pair<Property, Object>> map = new HashMap<>(propertyCount);
        for (int i = 0; i < propertyCount; i++)
        {
            Key key = BufferCodecs.KEY.decode(buffer);
            Property property = Registries.PROPERTY.get(key);
            Object value = property.decodeBuffer(buffer);
            map.put(key, Pair.of(property, value));
        }
        return new Properties(map);
    });

    private final Map<Key, Pair<Property, Object>> map;

    public Properties()
    {
        this.map = new HashMap<>();
    }

    protected Properties(Map<Key, Pair<Property, Object>> map)
    {
        this.map = new HashMap<>(map);
    }

    @Override
    public String toString()
    {
        return "Properties{" + "map=" + map + '}';
    }

    public Collection<Key> keys()
    {
        return map.keySet();
    }

    public boolean contains(Key property)
    {
        return map.containsKey(property);
    }

    public boolean contains(Property property)
    {
        return contains(property.key());
    }

    public boolean containsAnyProperties(Collection<Property> properties)
    {
        for (Property property : properties)
        {
            if (contains(property))
                return true;
        }
        return false;
    }

    public boolean containsAllProperties(Collection<Property> properties)
    {
        for (Property property : properties)
        {
            if (!contains(property))
                return false;
        }
        return true;
    }

    public Object get(Key property)
    {
        Pair<Property, Object> pair = map.get(property);
        Objects.requireNonNull(pair, () -> "Property '%s' is not present on this entity".formatted(property));
        return pair.getSecond();
    }

    public Object get(Property property)
    {
        return get(property.key());
    }

    public void set(@NotNull Property property, @NotNull Object value)
    {
        Objects.requireNonNull(property, "Property can not be null");
        Objects.requireNonNull(value, "Value can not be null");
        switch (property)
        {
            case PropertyInt _ -> setInt(property.key(), ((Number) value).intValue());
            case PropertyDouble _ -> setDouble(property.key(), ((Number) value).doubleValue());
            case PropertyEnum _ -> setEnum(property.key(), (String) value);
            case PropertyString _ -> setString(property.key(), (String) value);
            default -> throw new IllegalStateException("Unexpected value: " + property);
        }
    }

    public void remove(Key property)
    {
        map.remove(property);
    }

    public void remove(Property property)
    {
        remove(property.getType().key());
    }

    /*
     *
     */

    public int getInt(Key property)
    {
        Pair<Property, Object> pair = map.get(property);
        if (pair.getFirst() instanceof PropertyInt)
        {
            return (int) pair.getSecond();
        }
        Log.warningOnce(LOGGER, "Property '%s' is of type '%s' not '%s'".formatted(property, pair.getFirst().getType().key(), PropertyType.INT.key()));
        return 0;
    }

    public int getInt(Property property)
    {
        return getInt(property.key());
    }

    public void setInt(Key property, int value)
    {
        Property propertyVal = Registries.PROPERTY.get(property);
        Objects.requireNonNull(propertyVal, () -> "Property '%s' does not exist".formatted(property));
        if (!(propertyVal instanceof PropertyInt propertyInt))
        {
            Log.warningOnce(LOGGER, "Property '%s' is of type '%s' not '%s'".formatted(property, propertyVal.getType().key(), PropertyType.INT.key()));
            return;
        }
        if (!propertyInt.range().fitsInInterval(value))
            throw new IllegalStateException("Value '%s' does not fit within range %s".formatted(value, propertyInt.range()));
        map.put(property, Pair.of(propertyVal, value));
    }

    public String getEnum(Key property)
    {
        Pair<Property, Object> pair = map.get(property);
        if (pair.getFirst() instanceof PropertyEnum)
        {
            return (String) pair.getSecond();
        }
        Log.warningOnce(LOGGER, "Property '%s' is of type '%s' not '%s'".formatted(property, pair.getFirst().getType().key(), PropertyType.ENUM.key()));
        return "";
    }

    public String getEnum(Property property)
    {
        return getEnum(property.key());
    }

    public void setEnum(Key property, String value)
    {
        Property propertyVal = Registries.PROPERTY.get(property);
        Objects.requireNonNull(propertyVal, () -> "Property '%s' does not exist".formatted(property));
        if (!(propertyVal instanceof PropertyEnum propertyEnum))
        {
            Log.warningOnce(LOGGER, "Property '%s' is of type '%s' not '%s'".formatted(property, propertyVal.getType().key(), PropertyType.ENUM.key()));
            return;
        }
        if (!propertyEnum.range().contains(value))
            throw new IllegalStateException("Value '%s' does not fit within range %s".formatted(value, propertyEnum.range()));
        map.put(property, Pair.of(propertyVal, value));
    }

    public double getDouble(Key property)
    {
        Pair<Property, Object> pair = map.get(property);
        if (pair.getFirst() instanceof PropertyDouble)
        {
            return (double) pair.getSecond();
        }
        Log.warningOnce(LOGGER, "Property '%s' is of type '%s' not '%s'".formatted(property, pair.getFirst().getType().key(), PropertyType.DOUBLE.key()));
        return 0.0;
    }

    public double getDouble(Property property)
    {
        return getDouble(property.key());
    }

    public void setDouble(Key property, double value)
    {
        Property propertyVal = Registries.PROPERTY.get(property);
        Objects.requireNonNull(propertyVal, () -> "Property '%s' does not exist".formatted(property));
        if (!(propertyVal instanceof PropertyDouble propertyDouble))
        {
            Log.warningOnce(LOGGER, "Property '%s' is of type '%s' not '%s'".formatted(property, propertyVal.getType().key(), PropertyType.DOUBLE.key()));
            return;
        }
        if (!propertyDouble.range().fitsInInterval(value))
            throw new IllegalStateException("Value '%s' does not fit within range %s".formatted(value, propertyDouble.range()));
        map.put(property, Pair.of(propertyVal, value));
    }

    public String getString(Key property)
    {
        Pair<Property, Object> pair = map.get(property);
        if (pair.getFirst() instanceof PropertyString)
        {
            return (String) pair.getSecond();
        }
        Log.warningOnce(LOGGER, "Property '%s' is of type '%s' not '%s'".formatted(property, pair.getFirst().getType().key(), PropertyType.STRING.key()));
        return "";
    }

    public String getString(Property property)
    {
        return getString(property.key());
    }

    public void setString(Key property, String value)
    {
        Property propertyVal = Registries.PROPERTY.get(property);
        Objects.requireNonNull(propertyVal, () -> "Property '%s' does not exist".formatted(property));
        if (!(propertyVal instanceof PropertyString))
        {
            Log.warningOnce(LOGGER, "Property '%s' is of type '%s' not '%s'".formatted(property, propertyVal.getType().key(), PropertyType.STRING.key()));
            return;
        }
        map.put(property, Pair.of(propertyVal, value));
    }
















































    // Oh god please kill me, this is so disgusting, never again, this was such a bad idea. No type safety, none. We abuse Objects and casting here
    private static final Codec<Pair<Key, Pair<Property, Object>>> ELEMENT_CODEC = Codec.of(new Encoder<>()
    {
        @Override
        public <T> DataResult<T> encode(Pair<Key, Pair<Property, Object>> keyPairPair, DynamicOps<T> dynamicOps, T t)
        {
            return DataResult.success(keyPairPair.getSecond().getFirst().encodeDynamic(dynamicOps, keyPairPair.getSecond().getSecond()));
        }
    }, new Decoder<>()
    {
        @SuppressWarnings("unchecked")
        @Override
        public <T> DataResult<Pair<Pair<Key, Pair<Property, Object>>, T>> decode(DynamicOps<T> dynamicOps, T t)
        {
            Pair<Object, Object> pair = (Pair<Object, Object>) t;
            DataResult<Pair<Key, T>> decode = Constants.KEY_CODEC.decode(dynamicOps, (T) pair.getFirst());
            Key key = decode.getOrThrow().getFirst();
            Property property = Registries.PROPERTY.get(key);
            Object decodedValue = property.decode(dynamicOps, (T) pair.getSecond());
            return DataResult.success(Pair.of(Pair.of(key, Pair.of(property, decodedValue)), t));
        }
    });

    private static final Codec<Pair<Key, Pair<Property, ValueSource>>> EXPRESSION_ELEMENT_CODEC = Codec.of(new Encoder<>()
    {
        @Override
        public <T> DataResult<T> encode(Pair<Key, Pair<Property, ValueSource>> keyPairPair, DynamicOps<T> dynamicOps, T t)
        {
            Property property = keyPairPair.getSecond().getFirst();
            ValueSource either = keyPairPair.getSecond().getSecond();
            if (either.isLiteral())
            {
                return DataResult.success(property.encodeDynamic(dynamicOps, either.getRaw()));
            } else if (either.isScript())
            {
                OrCode orCode = ((OrCode) either.getRaw());
                return DataResult.success(dynamicOps.createString(Constants.ORCODE_PREFIX + orCode.codeStr()));
            } else
            {
                throw new RuntimeException("what ? How is there neither left nor right");
            }
        }
    }, new Decoder<>()
    {
        @SuppressWarnings("unchecked")
        @Override
        public <T> DataResult<Pair<Pair<Key, Pair<Property, ValueSource>>, T>> decode(DynamicOps<T> dynamicOps, T t)
        {
            Pair<Object, Object> pair = (Pair<Object, Object>) t;
            DataResult<Pair<Key, T>> decode = Constants.KEY_CODEC.decode(dynamicOps, (T) pair.getFirst());
            Key key = decode.getOrThrow().getFirst();
            Property property = Registries.PROPERTY.get(key);

            if (property == null)
                return DataResult.error(() -> "Unknown property for key '%s'".formatted(key));

            ValueSource either = null;

            DataResult<String> stringValue = dynamicOps.getStringValue((T) pair.getSecond());
            if (stringValue.isSuccess())
            {
                String stringVal = stringValue.getOrThrow();
                if (stringVal.startsWith(Constants.ORCODE_PREFIX))
                {
                    try
                    {
                        either = ValueSource.script(Orlang.parser.parse(stringVal.substring(1)));
                    } catch (Exception exception)
                    {
                        return DataResult.error(() -> "Can not parse '%s', exception: %s".formatted(stringVal.substring(1), exception.getMessage()));
                    }
                }
            }

            if (either == null)
            {
                Object decodedValue = property.decode(dynamicOps, (T) pair.getSecond());
                either = ValueSource.literal(decodedValue);
            }
            return DataResult.success(Pair.of(Pair.of(key, Pair.of(property, either)), t));
        }
    });

    public static final Codec<Map<Key, Pair<Property, Object>>> RAW_MAP_CODEC = new UnboundedKeyAwareMapCodec<>(Constants.KEY_CODEC, ELEMENT_CODEC);
    public static final Codec<Map<Key, Pair<Property, ValueSource>>> RAW_MAP_EXPRESSION_CODEC = new UnboundedKeyAwareMapCodec<>(Constants.KEY_CODEC, EXPRESSION_ELEMENT_CODEC);
//    public static final Codec<Properties> CODEC = RAW_MAP_CODEC.xmap(Properties::new, p -> p.map);
    public static final Codec<Properties> CODEC = ComponentCodec.xmap(RAW_MAP_CODEC, map -> () -> new Properties(map), p -> p.map);


    // For the love of all science, never make something like this ever again, this is a sin
    private record UnboundedKeyAwareMapCodec<K, V>(Codec<K> keyCodec, Codec<Pair<K, V>> elementCodec) implements Codec<Map<K, V>>
    {
        public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> ops, T input)
        {
            return ops.getMap(input).setLifecycle(Lifecycle.stable()).flatMap((map) -> decode(ops, map)).map((r) -> Pair.of(r, input));
        }

        public <T> DataResult<T> encode(Map<K, V> input, DynamicOps<T> ops, T prefix)
        {
            return encode(input, ops, ops.mapBuilder()).build(prefix);
        }

        private <T> DataResult<Map<K, V>> decode(final DynamicOps<T> ops, final MapLike<T> input)
        {
            final Object2ObjectMap<K, V> read = new Object2ObjectArrayMap<>();
            final Stream.Builder<Pair<T, T>> failed = Stream.builder();

            final DataResult<Unit> result = input
                .entries()
                .reduce(DataResult.success(Unit.INSTANCE, Lifecycle.stable()), (r, pair) ->
                {
                    final DataResult<K> key = keyCodec().parse(ops, pair.getFirst());
                    //noinspection unchecked
                    final DataResult<V> value = elementCodec().parse(ops, (T) pair).map(Pair::getSecond);

                    final DataResult<Pair<K, V>> entryResult = key.apply2stable(Pair::of, value);
                    final Optional<Pair<K, V>> entry = entryResult.resultOrPartial();
                    if (entry.isPresent())
                    {
                        final V existingValue = read.putIfAbsent(entry.get().getFirst(), entry.get().getSecond());
                        if (existingValue != null)
                        {
                            failed.add(pair);
                            return r.apply2stable((u, _) -> u, DataResult.error(() -> "Duplicate entry for key: '" + entry
                                .get()
                                .getFirst() + "'"));
                        }
                    }
                    if (entryResult.isError())
                    {
                        failed.add(pair);
                    }

                    return r.apply2stable((u, _) -> u, entryResult);
                }, (r1, r2) -> r1.apply2stable((u1, _) -> u1, r2));

            final Map<K, V> elements = ImmutableMap.copyOf(read);
            final T errors = ops.createMap(failed.build());

            return result.map(_ -> elements).setPartial(elements).mapError(e -> e + " missed input: " + errors);
        }

        private <T> RecordBuilder<T> encode(final Map<K, V> input, final DynamicOps<T> ops, final RecordBuilder<T> prefix)
        {
            for (final Map.Entry<K, V> entry : input.entrySet())
            {
                prefix.add(keyCodec().encodeStart(ops, entry.getKey()), elementCodec().encodeStart(ops, Pair.of(entry.getKey(), entry.getValue())));
            }
            return prefix;
        }
    }
}
