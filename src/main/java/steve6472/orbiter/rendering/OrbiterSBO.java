package steve6472.orbiter.rendering;

import steve6472.flare.struct.StructDef;
import steve6472.flare.struct.type.StructVertex;

import static steve6472.flare.struct.Struct.builder;
import static steve6472.flare.struct.def.MemberType.*;

/**
 * Created by steve6472
 * Date: 9/1/2025
 * Project: Orbiter <br>
 */
public interface OrbiterSBO
{
    StructDef ENTRY = builder()
        .addMember(MAT_4F)
        .addMember(VEC_4F)
        .build();

    StructDef MODEL_TINT_ENTRIES = builder()
        .addStructArray(ENTRY, 32768)
        .build();
}
