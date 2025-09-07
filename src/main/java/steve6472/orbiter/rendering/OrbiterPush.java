package steve6472.orbiter.rendering;

import steve6472.flare.struct.type.StructPush;

import static steve6472.flare.struct.Struct.builder;
import static steve6472.flare.struct.def.MemberType.INT;

/**
 * Created by steve6472
 * Date: 9/7/2025
 * Project: Orbiter <br>
 */
public interface OrbiterPush
{
    StructPush SKIN = builder()
        .addMember(INT) // stride
        .addMember(INT) // offset
        .build(StructPush::new);
}
