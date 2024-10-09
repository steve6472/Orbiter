package steve6472.orbiter.world.ecs.components;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public final class Tag
{
    private static final List<TagClass<?>> CLASSES = new ArrayList<>();
    private static int COUNTER = 0;

    public static final Physics PHYSICS = new Physics();
    public static final FireflyAI FIREFLY_AI = new FireflyAI();

    public static final class Physics extends TagClass<Physics> { private Physics() {super(nextId());} }
    public static final class FireflyAI extends TagClass<FireflyAI> { private FireflyAI() {super(nextId());} }

    /*
     * Serialization stuff
     */

    private static int nextId()
    {
        // Make sure we start from 0 to match the list
        COUNTER++;
        return COUNTER - 1;
    }

    private static abstract class TagClass<T extends TagClass<?>>
    {
        int id;
        private final Codec<T> codec;
        private final BufferCodec<ByteBuf, T> networkCodec;

        protected TagClass(int id)
        {
            this.id = id;
            CLASSES.add(this);
            codec = Codec.INT.xmap(i -> (T) CLASSES.get(i), (T c) -> c.id);
            networkCodec = BufferCodec.of(BufferCodecs.INT, a -> a.id, i -> (T) CLASSES.get(i));
        }

        public Codec<T> codec()
        {
            return codec;
        }

        public BufferCodec<ByteBuf, T> networkCodec()
        {
            return networkCodec;
        }
    }
}
