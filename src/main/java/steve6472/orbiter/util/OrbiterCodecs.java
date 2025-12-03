package steve6472.orbiter.util;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNativeHandle;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.core.registry.Key;
import steve6472.core.util.ExtraCodecs;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.api.User;
import steve6472.orbiter.network.impl.dedicated.DedicatedUser;
import steve6472.orlang.OrlangValue;

import java.util.List;

/**
 * Created by steve6472
 * Date: 10/9/2024
 * Project: Orbiter <br>
 */
public class OrbiterCodecs
{
    /// @deprecated WIP
    @Deprecated
    public static final Codec<SteamID> STEAM_USER = RecordCodecBuilder.create(instance -> instance.group(
        Codec.LONG.fieldOf("native").forGetter(SteamNativeHandle::getNativeHandle)
    ).apply(instance, SteamID::createFromNativeHandle));

    public static final Codec<DedicatedUser> DEDICATED_USER = RecordCodecBuilder.create(instance -> instance.group(
        ExtraCodecs.UUID.fieldOf("uuid").forGetter(User::uuid),
        Codec.STRING.fieldOf("username").forGetter(User::username)
    ).apply(instance, DedicatedUser::new));

    /// TODO: add Steam User
    public static final Codec<User> USER = DEDICATED_USER.xmap(u -> u, u -> (DedicatedUser) u);

    public static final Codec<List<String>> STRING_LIST_OR_SINGLE = Codec.withAlternative(Codec.STRING.listOf(), Codec.STRING, List::of);
    public static final Codec<List<StringSource>> STRING_SOURCE_LIST_OR_SINGLE = Codec.withAlternative(StringSource.CODEC.listOf(), StringSource.CODEC, List::of);
    public static final Codec<List<Key>> KEY_LIST_OR_SINGLE = Codec.withAlternative(Constants.KEY_CODEC.listOf(), Constants.KEY_CODEC, List::of);

    public static final Codec<OrlangValue> ORLANG_VALUE = Codec.of(new Encoder<>()
    {
        @Override
        public <T> DataResult<T> encode(OrlangValue orlangValue, DynamicOps<T> dynamicOps, T t)
        {
            return switch (orlangValue)
            {
                case OrlangValue.Bool bool -> DataResult.success(dynamicOps.createBoolean(bool.value()));
                case OrlangValue.Number num -> DataResult.success(dynamicOps.createDouble(num.value()));
                case OrlangValue.StringVal str -> DataResult.success(dynamicOps.createString(str.value()));
                default -> throw new IllegalStateException("Unexpected value: " + orlangValue);
            };
        }
    }, new Decoder<>()
    {
        @Override
        public <T> DataResult<Pair<OrlangValue, T>> decode(DynamicOps<T> dynamicOps, T t)
        {
            DataResult<Boolean> booleanValue = dynamicOps.getBooleanValue(t);
            if (booleanValue.isSuccess())
                return DataResult.success(Pair.of(OrlangValue.bool(booleanValue.getOrThrow()), t));

            DataResult<Number> numberValue = dynamicOps.getNumberValue(t);
            if (numberValue.isSuccess())
                return DataResult.success(Pair.of(OrlangValue.num(numberValue.getOrThrow().doubleValue()), t));

            DataResult<String> stringValue = dynamicOps.getStringValue(t);
            if (stringValue.isSuccess())
                return DataResult.success(Pair.of(OrlangValue.string(stringValue.getOrThrow()), t));

            throw new IllegalStateException("Unexpected value: " + t);
        }
    }, "OrlangValue");

    public static final Codec<Integer> HEX = Codec.withAlternative(Codec.INT, Codec.STRING, str -> {
        if (str.startsWith("0x"))
            return Integer.parseInt(str.substring(2), 16);
        throw new RuntimeException("Not hex format");
    });
}
