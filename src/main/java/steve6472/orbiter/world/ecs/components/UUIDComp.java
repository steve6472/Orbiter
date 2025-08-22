package steve6472.orbiter.world.ecs.components;

import com.badlogic.ashley.core.Component;
import com.mojang.serialization.Codec;
import steve6472.core.util.ExtraCodecs;

import java.util.UUID;

/**
 * Created by steve6472
 * Date: 8/22/2025
 * Project: Orbiter <br>
 */
public record UUIDComp(UUID uuid) implements Component
{
    public static final Codec<UUIDComp> CODEC = ExtraCodecs.UUID.xmap(UUIDComp::new, UUIDComp::uuid);
}
