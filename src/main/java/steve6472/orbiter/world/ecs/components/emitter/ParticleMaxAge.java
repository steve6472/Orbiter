package steve6472.orbiter.world.ecs.components.emitter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import steve6472.orbiter.orlang.codec.OrNumValue;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record ParticleMaxAge(OrNumValue maxAge)
{
    public static final Codec<ParticleMaxAge> CODEC = OrNumValue.CODEC.xmap(ParticleMaxAge::new, ParticleMaxAge::maxAge);
}
