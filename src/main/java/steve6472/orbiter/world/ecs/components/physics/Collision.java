package steve6472.orbiter.world.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import com.github.stephengold.joltjni.BodyInterface;
import com.github.stephengold.joltjni.Shape;
import com.github.stephengold.joltjni.enumerate.EActivation;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.Registries;

/**
 * Created by steve6472
 * Date: 10/3/2024
 * Project: Orbiter <br>
 */
public record Collision(Key collisionKey, Shape shape) implements Component
{
    public static final Codec<Collision> CODEC = Constants.KEY_CODEC.xmap(Collision::new, Collision::collisionKey);

    public static final BufferCodec<ByteBuf, Collision> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.KEY, Collision::collisionKey,
        Collision::new);

    public Collision(Key collisionKey)
    {
        this(collisionKey, Registries.COLLISION.get(collisionKey).collisionShape());
    }
}
