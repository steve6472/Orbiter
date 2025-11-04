package steve6472.orbiter.rendering;

import steve6472.flare.struct.StructDef;
import steve6472.orbiter.world.World;

import static steve6472.flare.struct.Struct.builder;
import static steve6472.flare.struct.def.MemberType.*;

/**
 * Created by steve6472
 * Date: 9/1/2025
 * Project: Orbiter <br>
 */
public interface OrbiterSBO
{
    StructDef MODEL_TINT_ENTRY = builder()
        .addMember(MAT_4F)
        .addMember(VEC_4F)
        .build();

    StructDef MODEL_TINT_ENTRIES = builder()
        .addStructArray(MODEL_TINT_ENTRY, 32768)
        .build();

    StructDef PARTICLE_FLIPBOOK_ENTRY = builder()
        .addMember(VEC_4F) // dimensions
        .addMember(VEC_2F) // single size
        .addMember(INT) // from
        .addMember(INT) // to
        .addMember(FLOAT) // transition
        .addMember(INT) // flags
        .addMember(VEC_2F) // pixel scale

        .build();

    StructDef PARTICLE_FLIPBOOK_ENTRIES = builder()
        .addStructArray(PARTICLE_FLIPBOOK_ENTRY, World.MAX_PARTICLES)
        .build();


    StructDef PARTICLE_FLIPBOOK_TINTED_ENTRY = builder()
        .addMember(VEC_4F) // dimensions
        .addMember(VEC_2F) // single size
        .addMember(INT) // from
        .addMember(INT) // to
        .addMember(FLOAT) // transition
        .addMember(INT) // flags
        .addMember(VEC_2F) // pixel scale
        .addMember(VEC_4F) // color

        .build();

    StructDef PARTICLE_FLIPBOOK_TINTED_ENTRIES = builder()
        .addStructArray(PARTICLE_FLIPBOOK_TINTED_ENTRY, World.MAX_PARTICLES)
        .build();
}
