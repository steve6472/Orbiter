package steve6472.orbiter.world.emitter;

import steve6472.flare.core.Flare;
import steve6472.orbiter.OrbiterParts;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.ecs.blueprints.ParticleEmittersBlueprint;

/**
 * Created by steve6472
 * Date: 9/10/2025
 * Project: Orbiter <br>
 */
public class EmitterBlueprints
{
    public static void load()
    {
        Flare.getModuleManager().loadParts(OrbiterParts.EMITTER_BLUEPRINT, ParticleEmittersBlueprint.Emitter.CODEC, (emitter, key) -> {
            ParticleEmittersBlueprint.EmitterEntry entry = new ParticleEmittersBlueprint.EmitterEntry();
            entry.emitterBlueprint = emitter;
            entry.key = key;
            Registries.EMITTER_BLUEPRINT.register(entry);
        });
    }
}
