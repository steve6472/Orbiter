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
import steve6472.orbiter.world.ecs.components.UUIDComp;
import steve6472.orbiter.world.ecs.components.physics.Position;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class RenderNametag extends IteratingSystem implements RenderECSSystem
{
    private final MasterRenderer renderer;

    public RenderNametag(MasterRenderer renderer)
    {
        super(Family.all(Position.class, UUIDComp.class).get());
        this.renderer = renderer;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime)
    {
        Position position = Components.POSITION.get(entity);
        UUID uuid = Components.UUID.get(entity).uuid();

        TextLine text = TextLine.fromText(uuid.toString(), 1f / 4f, Billboard.FACE_CENTER);

        float yOffset = 0;
        if (Components.COLLISION.has(entity))
        {
            yOffset = Components.COLLISION.get(entity).shape().maxRadius();
        }

        renderer.textRender().line(text, new Matrix4f().translate(position.x(), position.y() + yOffset, position.z()));
    }
}
