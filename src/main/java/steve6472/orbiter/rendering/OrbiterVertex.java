package steve6472.orbiter.rendering;

import steve6472.flare.struct.type.StructVertex;

import static steve6472.flare.struct.def.MemberType.*;
import static steve6472.flare.struct.Struct.builder;

/**
 * Created by steve6472
 * Date: 9/1/2025
 * Project: Orbiter <br>
 */
public interface OrbiterVertex
{
    StructVertex POS3F_COL4F_UV = builder()
        .addMember(VEC_3F)  // position
        .addMember(VEC_4F)  // color
        .addMember(UV)      // uv
        .build(StructVertex::new);

    StructVertex POS3F_NORMAL_UV = builder()
        .addMember(VEC_3F)  // position
        .addMember(NORMAL)  // normal
        .addMember(UV)      // uv
        .build(StructVertex::new);

    StructVertex POS3F_COL3F_NOR3F_UV = builder()
        .addMember(VEC_3F)  // position
        .addMember(VEC_3F)  // color
        .addMember(NORMAL)  // normal
        .addMember(UV)      // uv
        .build(StructVertex::new);
}
