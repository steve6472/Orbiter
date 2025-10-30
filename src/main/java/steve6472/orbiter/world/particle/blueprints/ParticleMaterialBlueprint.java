package steve6472.orbiter.world.particle.blueprints;

import com.badlogic.ashley.core.PooledEngine;
import com.mojang.serialization.Codec;
import steve6472.core.registry.Key;
import steve6472.orbiter.Constants;
import steve6472.orbiter.rendering.ParticleMaterial;
import steve6472.orbiter.world.particle.components.RenderMaterial;
import steve6472.orbiter.world.particle.core.PCBlueprint;
import steve6472.orbiter.world.particle.core.ParticleComponent;
import steve6472.orlang.OrlangEnvironment;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleMaterialBlueprint(ParticleMaterial.Settings value) implements PCBlueprint<ParticleMaterialBlueprint>
{
    public static final Key KEY = Constants.key("material");
    public static final Codec<ParticleMaterialBlueprint> CODEC = ParticleMaterial.Settings.CODEC.xmap(ParticleMaterialBlueprint::new, ParticleMaterialBlueprint::value);

    @Override
    public ParticleComponent create(PooledEngine particleEngine, OrlangEnvironment environment)
    {
        RenderMaterial component = particleEngine.createComponent(RenderMaterial.class);
        component.value = ParticleMaterial.fromSettings(value);
        if (component.value == null)
            throw new RuntimeException("No material found for settings: " + value);
        return component;
    }

    @Override
    public Key key()
    {
        return KEY;
    }

    @Override
    public Codec<ParticleMaterialBlueprint> codec()
    {
        return CODEC;
    }
}
