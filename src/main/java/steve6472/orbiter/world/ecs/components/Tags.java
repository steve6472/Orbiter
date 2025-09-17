package steve6472.orbiter.world.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.world.ecs.Components;

import java.util.*;
import java.util.stream.LongStream;

/**
 * Created by steve6472
 * Date: 9/17/2025
 * Project: Orbiter <br>
 */
public class Tags implements Component
{
    /*
     * Codec stuff
     */
    public static final Codec<Tags> CODEC = RecordCodecBuilder.create(instance -> instance
        .group(Codec.LONG_STREAM.fieldOf("bits").forGetter(o -> Arrays.stream(o.bitSet.toLongArray())))
        .apply(instance, Tags::new));

    public static final BufferCodec<ByteBuf, Tags> BUFFER_CODEC = BufferCodec.of(BufferCodecs.LONG_ARRAY, o -> o.bitSet.toLongArray(), a -> new Tags(LongStream.of(a)));

    /*
     * Tags
     */
    private static int BIT_COUNTER = 0;
    private static final List<Tag> TAG_BY_INDEX = new ArrayList<>();
    private static final Map<Key, Tag> TAG_BY_KEY = new HashMap<>();

    public static final Tag PHYSICS = new Tag("physics");
    public static final Tag CLIENT_CHARACTER = new Tag("client_character");

    private static int nextBitIndex()
    {
        return BIT_COUNTER++;
    }

    public record Tag(int index, Key key)
    {
        public Tag
        {
            TAG_BY_INDEX.add(this);
            TAG_BY_KEY.put(key, this);
        }

        /// Creates Tag with default Orbiter namespace
        public Tag(String id)
        {
            this(nextBitIndex(), Constants.key(id));
        }

        public Tag(Key key)
        {
            this(nextBitIndex(), key);
        }

        /// Used for internal tags that can not be defined in blueprints
        public Tag()
        {
            this(nextBitIndex(), null);
        }
    }

    public static Tag getTagByKey(Key key)
    {
        return TAG_BY_KEY.get(key);
    }

    public static Tag getTagByIndex(int index)
    {
        return TAG_BY_INDEX.get(index);
    }

    /*
     * Tags component
     */

    private final BitSet bitSet;

    private Tags(LongStream longStream)
    {
        bitSet = BitSet.valueOf(longStream.toArray());
    }

    private Tags()
    {
        bitSet = new BitSet(BIT_COUNTER);
    }

    public boolean hasTag(Tag tag)
    {
        return bitSet.get(tag.index);
    }

    public Tags addTag(Tag tag)
    {
        bitSet.set(tag.index, true);
        return this;
    }

    public Tags removeTag(Tag tag)
    {
        bitSet.set(tag.index, false);
        return this;
    }

    /*
     * Static
     */

    public static Tags createEmpty()
    {
        return new Tags();
    }

    public static Tags createWithTags(Tag... tags)
    {
        Tags tagsObj = new Tags();
        for (Tag tag : tags)
        {
            tagsObj.addTag(tag);
        }
        return tagsObj;
    }

    public static boolean has(Entity entity, Tag tag)
    {/*
        Tags tagsComp = Components.TAGS.get(entity);
        if (tagsComp == null)
            return false;
        return tagsComp.hasTag(tag);*/
        return false;
    }

    public boolean hasAll(Entity entity, Tag... tags)
    {/*
        Tags tagsComp = Components.TAGS.get(entity);
        if (tagsComp == null)
            return false;

        for (Tag tag : tags)
        {
            if (!tagsComp.hasTag(tag))
                return false;
        }

        return true;*/
        return false;
    }

    public boolean hasAny(Entity entity, Tag... tags)
    {/*
        Tags tagsComp = Components.TAGS.get(entity);
        if (tagsComp == null)
            return false;

        for (Tag tag : tags)
        {
            if (tagsComp.hasTag(tag))
                return true;
        }

        return false;*/
        return false;
    }
}
