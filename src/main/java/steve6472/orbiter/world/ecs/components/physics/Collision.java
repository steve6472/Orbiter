package steve6472.orbiter.world.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;
import steve6472.core.registry.Key;
import steve6472.orbiter.Registries;

/**
 * Created by steve6472
 * Date: 10/3/2024
 * Project: Orbiter <br>
 */
public record Collision(Key collisionKey, CollisionShape shape) implements PhysicsProperty, Component
{
    public static final Codec<Collision> CODEC = Key.CODEC.xmap(Collision::new, Collision::collisionKey);

    public static final BufferCodec<ByteBuf, Collision> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.KEY, Collision::collisionKey,
        Collision::new);

    public Collision(Key collisionKey)
    {
        this(collisionKey, Registries.COLLISION.get(collisionKey).collisionShape());
    }

    @Override
    public ModifyState modifyComponent(PhysicsRigidBody body)
    {
        CollisionShape shape = Registries.COLLISION.get(collisionKey()).collisionShape();
        if (body.getCollisionShape() != shape)
        {
            body.setCollisionShape(shape);
            return ModifyState.modifiedComponent();
        }
        return ModifyState.noModification();
    }

    @Override
    public void modifyBody(PhysicsRigidBody body)
    {

    }
}
