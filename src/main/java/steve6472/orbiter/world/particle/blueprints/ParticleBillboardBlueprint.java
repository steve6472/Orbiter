package steve6472.orbiter.world.particle.blueprints;

import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.orlang.OrlangEnvironment;
import steve6472.orbiter.rendering.Billboard;
import steve6472.orbiter.world.particle.components.ParticleBillboard;
import steve6472.orbiter.world.particle.core.PCBlueprint;
import steve6472.orbiter.world.particle.core.ParticleComponent;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleBillboardBlueprint(Billboard type) implements PCBlueprint<ParticleBillboardBlueprint>
{
    // TODO: direction & motion
    public static final Key KEY = Constants.key("billboard");
    public static final Codec<ParticleBillboardBlueprint> CODEC = Billboard.CODEC.xmap(ParticleBillboardBlueprint::new, ParticleBillboardBlueprint::type);

    @Override
    public ParticleComponent create(PooledEngine particleEngine, OrlangEnvironment environment)
    {
        ParticleBillboard component = particleEngine.createComponent(ParticleBillboard.class);
        component.billboard = type;
        return component;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<ParticleBillboardBlueprint> codec()
    {
        return CODEC;
    }
}
