package steve6472.orbiter.world.particle.core;

import com.badlogic.ashley.core.PooledEngine;
import steve6472.core.registry.Keyable;
import steve6472.core.registry.Serializable;
import steve6472.orbiter.orlang.OrlangEnvironment;

/**
 * Created by steve6472
 * Date: 8/28/2025
 * Project: Orbiter <br>
 */
public interface PCBlueprint<SELF> extends Serializable<SELF>, Keyable
{
    ParticleComponent create(PooledEngine particleEngine, OrlangEnvironment environment);
}
