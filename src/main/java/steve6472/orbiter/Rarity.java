package steve6472.orbiter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector4f;
import steve6472.core.registry.Key;
import steve6472.core.registry.Keyable;
import steve6472.core.registry.Serializable;
import steve6472.core.util.ExtraCodecs;

public record Rarity(Key key, String name, Vector4f color) implements Keyable, Serializable<Rarity>
{
    private static final Codec<Rarity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Key.CODEC.fieldOf("key").forGetter(o -> o.key),
        Codec.STRING.fieldOf("name").forGetter(o -> o.name),
        ExtraCodecs.VEC_4F.fieldOf("color").forGetter(o -> o.color)
    ).apply(instance, Rarity::new));

    public Rarity(String id, String name, Vector4f color)
    {
        this(Key.withNamespace(Constants.NAMESPACE, id), name, color);
    }

    @Override
    public Codec<Rarity> codec()
    {
        return CODEC;
    }
}
