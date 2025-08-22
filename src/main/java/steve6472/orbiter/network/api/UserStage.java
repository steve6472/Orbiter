package steve6472.orbiter.network.api;

import steve6472.core.network.PacketListener;
import steve6472.orbiter.network.packets.configuration.ConfigurationClientboundListener;
import steve6472.orbiter.network.packets.configuration.ConfigurationHostboundListener;
import steve6472.orbiter.network.packets.login.LoginClientboundListener;
import steve6472.orbiter.network.packets.login.LoginHostboundListener;
import steve6472.orbiter.network.packets.play.GameClientboundListener;
import steve6472.orbiter.network.packets.play.GameHostboundListener;

/**
 * Created by steve6472
 * Date: 8/19/2025
 * Project: Orbiter <br>
 */
public enum UserStage
{
    UNSET(null, null),
    LOGIN(LoginClientboundListener.class, LoginHostboundListener.class),
    CONFIGURATION(ConfigurationClientboundListener.class, ConfigurationHostboundListener.class),
    PLAY(GameClientboundListener.class, GameHostboundListener.class);

    private final Class<? extends PacketListener> clientboundListener, hostboundListener;

    UserStage(Class<? extends PacketListener> clientboundListener, Class<? extends PacketListener> hostboundListener)
    {
        this.clientboundListener = clientboundListener;
        this.hostboundListener = hostboundListener;
    }

    public Class<? extends PacketListener> pickListener(boolean isHost)
    {
        return isHost ? hostboundListener : clientboundListener;
    }
}
