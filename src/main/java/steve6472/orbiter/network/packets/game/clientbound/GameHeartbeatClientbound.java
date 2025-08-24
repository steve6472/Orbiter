package steve6472.orbiter.network.packets.game.clientbound;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.packets.game.GameClientboundListener;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public final class GameHeartbeatClientbound implements Packet<GameHeartbeatClientbound, GameClientboundListener>
{
    private static final GameHeartbeatClientbound INSTANCE = new GameHeartbeatClientbound();
    public static final Key KEY = Constants.key("game/cb/heartbeat");
    public static final BufferCodec<ByteBuf, GameHeartbeatClientbound> BUFFER_CODEC = BufferCodec.unit(INSTANCE);

    private GameHeartbeatClientbound() {}

    public static GameHeartbeatClientbound instance()
    {
        return INSTANCE;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, GameHeartbeatClientbound> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(GameClientboundListener listener)
    {
        listener.heartbeat();
    }

    @Override
    public String toString()
    {
        return "GameHeartbeatClientbound{}";
    }
}
