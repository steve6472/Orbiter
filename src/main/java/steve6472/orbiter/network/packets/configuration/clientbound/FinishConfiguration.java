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
public class FinishConfiguration implements Packet<FinishConfiguration, ConfigurationClientboundListener>
{
    private static final FinishConfiguration INSTANCE = new FinishConfiguration();
    public static final Key KEY = Constants.key("configuration/cb/finish");
    public static final BufferCodec<ByteBuf, FinishConfiguration> BUFFER_CODEC = BufferCodec.unit(INSTANCE);

    private FinishConfiguration() {}

    public static FinishConfiguration instance()
    {
        return INSTANCE;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public BufferCodec<ByteBuf, FinishConfiguration> codec()
    {
        return BUFFER_CODEC;
    }

    @Override
    public void handlePacket(ConfigurationClientboundListener listener)
    {
        listener.finishConfig();
    }

    @Override
    public String toString()
    {
        return "FinishConfiguration{}";
    }
}
