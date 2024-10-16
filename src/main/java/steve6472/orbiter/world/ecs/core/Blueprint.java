package steve6472.orbiter.world.ecs.core;

import steve6472.core.registry.Keyable;
import steve6472.core.registry.Serializable;

import java.util.List;

/**
 * Created by steve6472
 * Date: 10/10/2024
 * Project: Orbiter <br>
 */
public interface Blueprint<SELF> extends Serializable<SELF>, Keyable
{
    List<?> createComponents();
}
