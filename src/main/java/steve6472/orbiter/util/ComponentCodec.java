package steve6472.orbiter.util;

import com.google.gson.JsonElement;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import steve6472.orlang.OrlangValue;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by steve6472
 * Date: 11/23/2025
 * Project: Orbiter <br>
 */
public class ComponentCodec
{
    public record Count(int value, boolean creativeInfinity) {}

    static final Codec<Count> CODEC = create(instance -> instance.group(
        Codec.INT.fieldOf("value").forGetter(e -> e.value),
        Codec.BOOL.fieldOf("creative_infinity").forGetter(e -> e.creativeInfinity)
    ).apply(instance, (value, inf) -> () -> new Count(value, inf)));

    public static <O> Codec<O> create(Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, Supplier<O>>> builder)
    {
        //noinspection unchecked,rawtypes
        return RecordCodecBuilder.build((App) builder.apply(RecordCodecBuilder.instance())).codec();
    }

    public static <S, A> Codec<S> xmap(Codec<A> codec, Function<? super A, Supplier<? extends S>> to, Function<? super S, ? extends A> from) {
        return Codec.of(codec.comap(from), (Decoder<S>) codec.map(a -> to.apply(a)), codec + "[xmapped]");
    }

    public static <S> Codec<S> unit(Supplier<S> value)
    {
        return (Codec<S>) Codec.of(Encoder.empty(), Decoder.unit(() -> value)).codec();
    }

    abstract class MultiMapCodec<A, B> implements MapEncoder<A>, MapDecoder<B>
    {
        public MultiMapCodec<A, B> withLifecycle(final Lifecycle lifecycle) {
            return new MultiMapCodec<A, B>() {
                public <T> Stream<T> keys(DynamicOps<T> ops) {
                    return MultiMapCodec.this.keys(ops);
                }

                public <T> DataResult<B> decode(DynamicOps<T> ops, MapLike<T> input) {
                    return MultiMapCodec.this.decode(ops, input).setLifecycle(lifecycle);
                }

                public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                    return MultiMapCodec.this.encode(input, ops, prefix).setLifecycle(lifecycle);
                }

                public String toString() {
                    return MultiMapCodec.this.toString();
                }
            };
        }

        @Override
        public <T> DataResult<B> decode(DynamicOps<T> dynamicOps, MapLike<T> mapLike)
        {
            return null;
        }

        @Override
        public <T> RecordBuilder<T> encode(A a, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder)
        {
            return null;
        }

        @Override
        public <T> KeyCompressor<T> compressor(DynamicOps<T> dynamicOps)
        {
            return null;
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> dynamicOps)
        {
            return Stream.empty();
        }
    }

    interface CmopCodec<A, B> extends Encoder<A>, Decoder<B>
    {
        @Override
        default <T> DataResult<Pair<B, T>> decode(DynamicOps<T> dynamicOps, T t)
        {
            return null;
        }

        @Override
        default <T> DataResult<T> encode(A a, DynamicOps<T> dynamicOps, T t)
        {
            return null;
        }

        @Override
        default MultiMapCodec<A, B> fieldOf(String name)
        {
            return null;
//            return MultiMapCodec.of(Encoder.super.fieldOf(name), Decoder.super.fieldOf(name), () -> "Field[" + name + ": " + this + "]");
        }

        default CmopCodec<A, B> withLifecycle(final Lifecycle lifecycle)
        {
            return new CmopCodec<>()
            {
                public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
                    return CmopCodec.this.encode(input, ops, prefix).setLifecycle(lifecycle);
                }

                public <T> DataResult<Pair<B, T>> decode(DynamicOps<T> ops, T input) {
                    return CmopCodec.this.decode(ops, input).setLifecycle(lifecycle);
                }

                public String toString() {
                    return CmopCodec.this.toString();
                }
            };
        }
    }

    public static void main(String[] args)
    {
        Count count = new Count(7, false);

        DataResult<JsonElement> jsonElementDataResult = CODEC.encodeStart(JsonOps.INSTANCE, count);
        JsonElement json = jsonElementDataResult.getOrThrow();
        System.out.println(json);

        DataResult<Pair<Count, JsonElement>> decode = CODEC.decode(JsonOps.INSTANCE, json);
//        decode.map(pair -> Pair.of((Supplier) (Object) pair.getFirst(), pair.getSecond()));
        Supplier<Count> countGet = ((Supplier<Count>) (Object) decode.getOrThrow().getFirst());
        System.out.println(countGet.get());

        DataResult<JsonElement> jsonElementDataResult1 = OrbiterCodecs.ORLANG_VALUE.encodeStart(JsonOps.INSTANCE, OrlangValue.num(7.77));
        JsonElement orThrow = jsonElementDataResult1.getOrThrow();
        System.out.println(orThrow);

        DataResult<Pair<OrlangValue, JsonElement>> decode1 = OrbiterCodecs.ORLANG_VALUE.decode(JsonOps.INSTANCE, orThrow);
        System.out.println(decode1.getOrThrow());
    }
}
