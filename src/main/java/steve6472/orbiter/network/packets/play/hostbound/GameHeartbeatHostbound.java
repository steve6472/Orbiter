package steve6472.orbiter.network.packets.play.hostbound;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.packets.configuration.ConfigurationHostboundListener;
import steve6472.orbiter.network.packets.play.GameHostboundListener;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public final class GameHeartbeatHostbound implements Packet<GameHeartbeatHostbound, GameHostboundListener>
{
    private static final GameHeartbeatHostbound INSTANCE = new GameHeartbeatHostbound();
    public static final Key KEY = Constants.key("game/hb/heartbeat");
    public static final BufferCodec<ByteBuf, GameHeartbeatHostbound> BUFFER_CODEC = BufferCodec.unit(INSTANCE);

    private GameHeartbeatHostbound() {}

    public static GameHeartbeatHostbound instance()
    {
        return INSTANCE;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, GameHeartbeatHostbound> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameHostboundListener listener)
    {
        listener.heartbeat();
    }

    @Override
    public String toString()
    {
        return "GameHeartbeatHostbound{}";
    }
}
