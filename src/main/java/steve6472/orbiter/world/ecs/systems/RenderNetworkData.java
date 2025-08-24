package steve6472.orbiter.world.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import org.joml.Matrix4f;
import steve6472.flare.MasterRenderer;
import steve6472.flare.ui.font.render.Billboard;
import steve6472.flare.ui.font.render.TextLine;
import steve6472.orbiter.world.ecs.Components;
import steve6472.orbiter.world.ecs.RenderECSSystem;
import steve6472.orbiter.world.ecs.components.NetworkAdd;
import steve6472.orbiter.world.ecs.components.NetworkRemove;
import steve6472.orbiter.world.ecs.components.NetworkUpdates;
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orbiter.world.ecs.components.physics.Position;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class RenderNetworkData extends IteratingSystem implements RenderECSSystem
{
    private final MasterRenderer renderer;

    public RenderNetworkData(MasterRenderer renderer)
    {
        super(Family.all(Position.class).one(NetworkAdd.class, NetworkRemove.class, NetworkUpdates.class).get());
        this.renderer = renderer;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        Position position = Components.POSITION.get(entity);
        NetworkAdd networkAdd = Components.NETWORK_ADD.get(entity);
        NetworkRemove networkRemove = Components.NETWORK_REMOVE.get(entity);
        NetworkUpdates networkUpdates = Components.NETWORK_UPDATES.get(entity);
        var pos = Components.POSITION.get(entity);

        TextLine text1 = TextLine.fromText(networkAdd + "", 1f / 6f, Billboard.FACE_CENTER);
        TextLine text2 = TextLine.fromText(networkRemove + "", 1f / 6f, Billboard.FACE_CENTER);
        TextLine text3 = TextLine.fromText(networkUpdates + "", 1f / 6f, Billboard.FACE_CENTER);
        TextLine posText = TextLine.fromText(pos + "", 1f / 6f, Billboard.FACE_CENTER);

        float yOffset = 0;
        if (Components.COLLISION.has(entity))
        {
            yOffset = Components.COLLISION.get(entity).shape().maxRadius();
        }

        renderer.textRender().line(posText, new Matrix4f().translate(position.x(), position.y() + yOffset + 1.2f, position.z()));
        renderer.textRender().line(text1, new Matrix4f().translate(position.x(), position.y() + yOffset + 1, position.z()));
        renderer.textRender().line(text2, new Matrix4f().translate(position.x(), position.y() + yOffset + 0.8f, position.z()));
        renderer.textRender().line(text3, new Matrix4f().translate(position.x(), position.y() + yOffset + 0.6f, position.z()));
    }
}
