package steve6472.orbiter.world.ecs.components;

import com.badlogic.ashley.core.Component;
import steve6472.flare.ui.font.render.TextLine;

/**
 * Created by steve6472
 * Date: 11/27/2024
 * Project: Orbiter <br>
 */
public record Nametag(TextLine name) implements Component
{
}
