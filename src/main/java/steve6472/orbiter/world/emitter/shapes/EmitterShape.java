package steve6472.orbiter.world.emitter.shapes;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import org.joml.Vector3f;
import steve6472.orbiter.Registries;
import steve6472.orbiter.world.emitter.ParticleEmitter;

/**
 * Generate particle position </br>
 * The emitters position will be added to the result
 */
public abstract class EmitterShape implements Component
{
    public static final Codec<EmitterShape> CODEC = Registries.EMITTER_SHAPE.byKeyCodec().dispatch("shape_type", EmitterShape::getType, EmitterShapeType::mapCodec);

    public abstract Vector3f createPosition(ParticleEmitter emitter);

    protected abstract EmitterShapeType<?> getType();
}