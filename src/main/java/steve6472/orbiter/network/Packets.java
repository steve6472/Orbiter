package steve6472.orbiter.network;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Registries;
import steve6472.orbiter.network.packets.DummyPacket;
import steve6472.orbiter.network.packets.game.HelloGame;
import steve6472.orbiter.network.packets.game.TeleportToPosition;
import steve6472.orbiter.network.packets.lobby.KickUser;
import steve6472.orbiter.network.packets.lobby.LobbyClosing;

/**
 * Created by steve6472
 * Date: 10/8/2024
 * Project: Orbiter <br>
 */
public class Packets
{
    public static Object init()
    {
        registerPacket(DummyPacket.KEY, DummyPacket.BUFFER_CODEC);

        registerPacket(LobbyClosing.KEY, LobbyClosing.BUFFER_CODEC);
        registerPacket(KickUser.KEY, KickUser.BUFFER_CODEC);

        registerPacket(HelloGame.KEY, HelloGame.BUFFER_CODEC);
        registerPacket(TeleportToPosition.KEY, TeleportToPosition.BUFFER_CODEC);

        return new Object();
    }

    private static void registerPacket(Key key, BufferCodec<ByteBuf, ? extends Packet<?, ?>> codec)
    {
        //noinspection unchecked
        Registries.PACKET.register(key, (BufferCodec<ByteBuf, Packet<?, ?>>) codec);
    }
}
