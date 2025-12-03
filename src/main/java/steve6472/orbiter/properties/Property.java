package steve6472.orbiter.properties;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.flare.core.Flare;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterParts;
import steve6472.orbiter.Registries;
import steve6472.orbiter.util.SettableObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 11/24/2025
 * Project: Orbiter <br>
 */
public interface Property extends Keyable
{
    Codec<Property> CODEC = Registries.PROPERTY_TYPE.byKeyCodec().dispatch("type", Property::getType, PropertyType::mapCodec);
    Codec<Property> ENTRY_CODEC = Constants.KEY_CODEC.xmap(Registries.PROPERTY::get, Property::key);
    Codec<List<Property>> ENTRY_CODEC_SINGLE_OR_LIST = Codec.withAlternative(ENTRY_CODEC.listOf(), ENTRY_CODEC, List::of);

    static void bootstrap()
    {
        List<Property> properties = new ArrayList<>();
        Flare.getModuleManager().loadParts(OrbiterParts.PROPERTY, CODEC, (property, key) -> {
            property.propertyKey().set(key);
            properties.add(property);
        });

        properties.forEach(Registries.PROPERTY::register);
    }

    SettableObject<Key> propertyKey();

    default Key key()
    {
        return propertyKey().get();
    }

    Object getDefaultValue();

    <T> T encodeDynamic(DynamicOps<T> ops, Object value);
    <T> Object decode(DynamicOps<T> ops, T value);

    void encodeBuffer(ByteBuf buffer, Object value);
    Object decodeBuffer(ByteBuf buffer);

    PropertyType<?> getType();
}
