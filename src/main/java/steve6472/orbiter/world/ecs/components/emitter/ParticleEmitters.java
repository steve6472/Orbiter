package steve6472.orbiter.world.ecs.components.emitter;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;

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

    public static final Codec<ParticleEmitters> CODEC = ParticleEmitter.CODEC.listOf().xmap(ParticleEmitters::new, ParticleEmitters::emitters);

    public ParticleEmitters(List<ParticleEmitter> emitters)
    {
        this.emitters = new ArrayList<>(emitters);
    }

    public List<ParticleEmitter> emitters()
    {
        return emitters;
    }
}
