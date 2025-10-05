package steve6472.orbiter.world.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.BufferCodecs;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class PCCharacter implements Component
{
    public static final Codec<PCCharacter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.FLOAT.fieldOf("jump_speed").forGetter(PCCharacter::jumpSpeed)
    ).apply(instance, PCCharacter::new));

    public static final BufferCodec<ByteBuf, PCCharacter> BUFFER_CODEC = BufferCodec.of(
        BufferCodecs.FLOAT, PCCharacter::jumpSpeed,
        PCCharacter::new);

    private float jumpSpeed;

    public PCCharacter(float jumpSpeed)
    {
        this.jumpSpeed = jumpSpeed;
    }

    public PCCharacter()
    {
        this(0);
    }

    public void set(float jumpSpeed)
    {
        this.jumpSpeed = jumpSpeed;
    }

    public float jumpSpeed()
    {
        return jumpSpeed;
    }

   /* public ModifyState modifyComponent(PhysicsCharacter body)
    {
        float bodyJumpSpeed = body.getJumpSpeed();

        if (bodyJumpSpeed == jumpSpeed)
            return ModifyState.noModification();

        set(bodyJumpSpeed);
        return ModifyState.modifiedComponent();
    }

    public void modifyBody(PhysicsCharacter body)
    {
//        if (!OrbiterApp.getInstance().getSteam().isHost())
//            return;

        body.setJumpSpeed(jumpSpeed);
    }*/

    @Override
    public String toString()
    {
        return "PCCharacter{" + "jumpSpeed=" + jumpSpeed + '}';
    }
}
