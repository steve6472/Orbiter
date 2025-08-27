package steve6472.orbiter.world.ecs.components.emitter.lifetime;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.ecs.components.emitter.ParticleEmitter;

public abstract class EmitterLifetime implements Component
{
    public static final Codec<EmitterLifetime> CODEC = Registries.EMITTER_LIFETIME.byKeyCodec().dispatch("lifetime_type", EmitterLifetime::getType, EmitterLifetimeType::mapCodec);

    public abstract boolean isAlive(ParticleEmitter emitter, int ticksAlive);
    public abstract boolean shouldEmit(ParticleEmitter emitter, int ticksAlive);

    protected abstract EmitterLifetimeType<? extends EmitterLifetime> getType();
}
