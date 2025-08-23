package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import org.joml.Vector3f;
import steve6472.core.log.Log;
import steve6472.orbiter.Client;
import steve6472.orbiter.OrbiterApp;
import steve6472.orbiter.network.api.NetworkMain;
import steve6472.orbiter.network.packets.play.hostbound.PlayerMove;
import steve6472.orbiter.player.Player;
import steve6472.orbiter.world.ecs.Components;

import java.util.logging.Logger;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class UpdateClientPosition extends EntitySystem
{
    public UpdateClientPosition()
    {
    }

    private final Vector3f lastPosition = new Vector3f(Float.NaN);

    @Override
    public void update(float deltaTime)
    {
        OrbiterApp orbiter = OrbiterApp.getInstance();
        Client client = orbiter.getClient();
        Player player = client.player();
        Entity playerEntity = player.ecsEntity();
        Vector3f centerPos = player.getCenterPos();
        if (lastPosition.equals(centerPos))
            return;

        lastPosition.set(centerPos);
        Components.POSITION.get(playerEntity).set(centerPos.x, centerPos.y, centerPos.z);
        client.getWorld().markModified(playerEntity, Components.POSITION.componentClass());

        NetworkMain network = orbiter.getNetwork();
        if (network.lobby().isLobbyOpen() && !network.lobby().isHost())
        {
            network.connections().broadcastPacket(new PlayerMove(centerPos));
        }
    }
}
