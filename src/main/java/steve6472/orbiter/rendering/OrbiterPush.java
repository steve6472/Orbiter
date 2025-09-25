package steve6472.orbiter.rendering;

import steve6472.flare.struct.type.StructPush;

import static steve6472.flare.struct.Struct.builder;
import static steve6472.flare.struct.def.MemberType.*;

/**
 * Created by steve6472
 * Date: 9/7/2025
 * Project: Orbiter <br>
 */
public interface OrbiterPush
{
    StructPush FLIPBOOK_ANIM_DATA = builder()
        // Dimensions in UV coordinates of atlas
        .addMember(VEC_4F)
        // Size of single sprite
        .addMember(VEC_2F)
        // indexFrom
        .addMember(INT)
        // indexTo
        .addMember(INT)
        // transition
        .addMember(FLOAT)
        // flags
        .addMember(INT)
        // pixelScale
        .addMember(VEC_2F)
        .build(StructPush::new);
}
