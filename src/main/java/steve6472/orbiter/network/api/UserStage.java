package steve6472.orbiter.network.api;

import steve6472.core.network.PacketListener;
import steve6472.orbiter.network.packets.configuration.ConfigurationClientboundListener;
import steve6472.orbiter.network.packets.configuration.ConfigurationHostboundListener;
import steve6472.orbiter.network.packets.game.GameListener;
import steve6472.orbiter.network.packets.login.LoginClientboundListener;
import steve6472.orbiter.network.packets.login.LoginHostboundListener;

/**
 * Created by steve6472
 * Date: 8/19/2025
 * Project: Orbiter <br>
 */
public enum UserStage
{
    UNSET(null),

    LOGIN_CLIENTBOUND(LoginClientboundListener.class),
    LOGIN_HOSTBOUND(LoginHostboundListener.class),

    CONFIGURATION_CLIENTBOUND(ConfigurationClientboundListener.class),
    CONFIGURATION_HOSTBOUND(ConfigurationHostboundListener.class),

    PLAY_CLIENTBOUND(GameListener.class),
    PLAY_HOSTBOUND(GameListener.class);

    public final Class<? extends PacketListener> listener;

    UserStage(Class<? extends PacketListener> listener)
    {
        this.listener = listener;
    }
}
