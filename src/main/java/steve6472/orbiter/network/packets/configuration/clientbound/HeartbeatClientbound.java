package steve6472.orbiter.network.packets.configuration.clientbound;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.packets.configuration.ConfigurationClientboundListener;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public class HeartbeatClientbound implements Packet<HeartbeatClientbound, ConfigurationClientboundListener>
{
    private static final HeartbeatClientbound INSTANCE = new HeartbeatClientbound();
    public static final Key KEY = Constants.key("configuration/cb/heartbeat");
    public static final BufferCodec<ByteBuf, HeartbeatClientbound> BUFFER_CODEC = BufferCodec.unit(INSTANCE);

    private HeartbeatClientbound() {}

    public static HeartbeatClientbound instance()
    {
        return INSTANCE;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, HeartbeatClientbound> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(ConfigurationClientboundListener listener)
    {
        listener.heartbeat();
    }

    @Override
    public String toString()
    {
        return "HeartbeatClientbound{}";
    }
}
