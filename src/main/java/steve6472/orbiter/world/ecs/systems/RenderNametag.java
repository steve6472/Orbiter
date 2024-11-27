package steve6472.orbiter.world.ecs.systems;

import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;
import org.joml.Matrix4f;
import steve6472.core.registry.Key;
import steve6472.flare.MasterRenderer;
import steve6472.flare.ui.font.render.Billboard;
import steve6472.flare.ui.font.render.TextLine;
import steve6472.orbiter.world.World;
import steve6472.orbiter.world.ecs.components.Nametag;
import steve6472.orbiter.world.ecs.components.physics.Collision;
import steve6472.orbiter.world.ecs.components.physics.Position;
import steve6472.orbiter.world.ecs.core.ComponentRenderSystem;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 10/2/2024
 * Project: Orbiter <br>
 */
public class RenderNametag implements ComponentRenderSystem
{
    @Override
    public void tick(MasterRenderer renderer, Dominion dominion, World world)
    {
//        var found = dominion.findEntitiesWith(Position.class, Nametag.class);
        var found = dominion.findEntitiesWith(Position.class, UUID.class);

        for (var entityComps : found)
        {
            Entity entity = entityComps.entity();
            Position position = entityComps.comp1();
//            TextLine text = entityComps.comp2().name();
            TextLine text = TextLine.fromText(entityComps.comp2().toString(), 1f / 4f, Billboard.FACE_CENTER);

            float yOffset = 0;
            if (entity.has(Collision.class))
            {
                Collision collision = entity.get(Collision.class);
                yOffset = collision.shape().maxRadius();
            }

            renderer.textRender().line(text, new Matrix4f().translate(position.x(), position.y() + yOffset, position.z()));
        }
    }
}
