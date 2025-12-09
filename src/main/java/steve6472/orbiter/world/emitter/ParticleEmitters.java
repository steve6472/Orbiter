package steve6472.orbiter.world.emitter;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import steve6472.orbiter.util.ComponentCodec;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public class ParticleEmitters implements Component
{
    public List<ParticleEmitter> emitters;

    public static final Codec<ParticleEmitters> CODEC = ComponentCodec.xmap(ParticleEmitter.CODEC.listOf(), list -> () -> new ParticleEmitters(list), ParticleEmitters::emitters);

    public ParticleEmitters(List<ParticleEmitter> emitters)
    {
        this.emitters = new ArrayList<>(emitters);
    }

    public List<ParticleEmitter> emitters()
    {
        return emitters;
    }
}
