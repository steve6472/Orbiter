package steve6472.orbiter.world.ecs.components.emitter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Created by steve6472
 * Date: 8/25/2025
 * Project: Orbiter <br>
 */
public record LocalSpaceEmitter(boolean position, boolean rotation, boolean velocity)
{
    public static final Codec<LocalSpaceEmitter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("position", false).forGetter(LocalSpaceEmitter::position),
        Codec.BOOL.optionalFieldOf("rotation", false).forGetter(LocalSpaceEmitter::position),
        Codec.BOOL.optionalFieldOf("velocity", false).forGetter(LocalSpaceEmitter::position)
    ).apply(instance, LocalSpaceEmitter::new));

    public static final LocalSpaceEmitter DEFAULT = new LocalSpaceEmitter(false, false, false);
}
