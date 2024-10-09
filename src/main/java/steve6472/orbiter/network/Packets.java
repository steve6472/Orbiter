package steve6472.orbiter.network;

import io.netty.buffer.ByteBuf;
import steve6472.core.network.BufferCodec;
import steve6472.core.network.Packet;
import steve6472.core.registry.Key;
import steve6472.orbiter.Registries;
import steve6472.orbiter.network.packets.game.*;
import steve6472.orbiter.network.packets.lobby.*;

/**
 * Created by steve6472
 * Date: 10/8/2024
 * Project: Orbiter <br>
 */
public class Packets
{
    public static Object init()
    {
        // Lobby packets
        registerPacket(LobbyClosing.KEY, LobbyClosing.BUFFER_CODEC);
        registerPacket(LobbyKickUser.KEY, LobbyKickUser.BUFFER_CODEC);
        registerPacket(LobbyChatMessage.KEY, LobbyChatMessage.BUFFER_CODEC);

        // Game packets
        registerPacket(HelloGame.KEY, HelloGame.BUFFER_CODEC);
        registerPacket(Heartbeat.KEY, Heartbeat.BUFFER_CODEC);

        registerPacket(AcceptedPeerConnection.KEY, AcceptedPeerConnection.BUFFER_CODEC);
        registerPacket(SpawnPlayerCharacter.KEY, SpawnPlayerCharacter.BUFFER_CODEC); // TODO: change this packet
        registerPacket(PlayerDisconnected.KEY, PlayerDisconnected.BUFFER_CODEC);
        registerPacket(TeleportToPosition.KEY, TeleportToPosition.BUFFER_CODEC); // TODO: change this packet
        registerPacket(UpdateEntityComponents.KEY, UpdateEntityComponents.BUFFER_CODEC);

        return new Object();
    }

    private static void registerPacket(Key key, BufferCodec<ByteBuf, ? extends Packet<?, ?>> codec)
    {
        //noinspection unchecked
        Registries.PACKET.register(key, (BufferCodec<ByteBuf, Packet<?, ?>>) codec);
    }
}
