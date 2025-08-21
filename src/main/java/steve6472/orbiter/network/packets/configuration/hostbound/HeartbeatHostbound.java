package steve6472.orbiter.network.packets.configuration.hostbound;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.network.packets.configuration.ConfigurationClientboundListener;
import steve6472.orbiter.network.packets.configuration.ConfigurationHostboundListener;

/**
 * Created by steve6472
 * Date: 10/7/2024
 * Project: Orbiter <br>
 */
public class HeartbeatHostbound implements Packet<HeartbeatHostbound, ConfigurationHostboundListener>
{
    private static final HeartbeatHostbound INSTANCE = new HeartbeatHostbound();
    public static final Key KEY = Constants.key("configuration/hostbound/heartbeat");
    public static final BufferCodec<ByteBuf, HeartbeatHostbound> BUFFER_CODEC = BufferCodec.unit(INSTANCE);

    private HeartbeatHostbound() {}

    public static HeartbeatHostbound instance()
    {
        return INSTANCE;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, HeartbeatHostbound> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(ConfigurationHostboundListener listener)
    {
        listener.heartbeat();
    }
}
