package steve6472.orbiter.world.emitter.rate;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.emitter.ParticleEmitter;

public abstract class EmitterRate implements Component
{
    public static final Codec<EmitterRate> CODEC = Registries.EMITTER_RATE.byKeyCodec().dispatch("rate_type", EmitterRate::getType, EmitterRateType::mapCodec);

    public abstract int spawnCount(ParticleEmitter emitter);

    protected abstract EmitterRateType<? extends EmitterRate> getType();
}
