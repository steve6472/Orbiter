package steve6472.orbiter.rendering;

import steve6472.flare.ShaderSPIRVUtils;
import steve6472.flare.pipeline.builder.PipelineBuilder;
import steve6472.flare.pipeline.builder.PipelineConstructor;
import steve6472.flare.struct.def.Push;
import steve6472.flare.struct.def.Vertex;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Created by steve6472
 * Date: 9/1/2025
 * Project: Orbiter <br>
 */
public interface OrbiterPipelines
{
    PipelineConstructor BLOCKBENCH_STATIC_ADDITIVE = (device, extent, renderPass, setLayouts) -> PipelineBuilder
        .create(device)
        .shaders()
            .addShader(ShaderSPIRVUtils.ShaderKind.VERTEX_SHADER, "flare/shaders/blockbench_static.vert", VK_SHADER_STAGE_VERTEX_BIT)
            .addShader(ShaderSPIRVUtils.ShaderKind.FRAGMENT_SHADER, "flare/shaders/blockbench_static.frag", VK_SHADER_STAGE_FRAGMENT_BIT)
            .done()
        .vertexInputInfo(Vertex.POS3F_NORMAL_UV)
        .inputAssembly(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST, false)
        .viewport()
            .viewportBounds(0.0f, extent.height(), extent.width(), -extent.height())
            .viewportDepths(0.0f, 1.0f)
            .scissorOffset(0, 0)
            .scissorExtent(extent)
            .done()
        .rasterization()
            .flags(false, false, false)
            .polygonInfo(VK_POLYGON_MODE_FILL, VK_CULL_MODE_BACK_BIT, VK_FRONT_FACE_COUNTER_CLOCKWISE)
            .done()
        .multisampling()
            .sampleShading(false)
            .rasterizationSamples(VK_SAMPLE_COUNT_1_BIT)
            .done()
        .depthStencil()
            .depthEnableFlags(true, true)
            .depthCompareOp(VK_COMPARE_OP_LESS)
            .bounds(0.0f, 1.0f, false)
            .stencilTestEnable(false)
            .done()
        .colorBlend(true, VK_LOGIC_OP_COPY, 0f, 0f, 0f, 0f)
            .additive(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT)
            .done()
        .pushConstants()
            .constant(VK_SHADER_STAGE_VERTEX_BIT, 0, Push.STATIC_TRANSFORM_OFFSET.sizeof())
            .done()
        .build(renderPass, setLayouts);

    PipelineConstructor MODEL_UNSHADED_TINTED = (device, extent, renderPass, setLayouts) -> PipelineBuilder
        .create(device)
        .shaders()
            .addShader(ShaderSPIRVUtils.ShaderKind.VERTEX_SHADER, "orbiter/shaders/particle/model_unshaded_tinted.vert", VK_SHADER_STAGE_VERTEX_BIT)
            .addShader(ShaderSPIRVUtils.ShaderKind.FRAGMENT_SHADER, "orbiter/shaders/particle/model_unshaded_tinted.frag", VK_SHADER_STAGE_FRAGMENT_BIT)
            .done()
        .vertexInputInfo(OrbiterVertex.POS3F_NORMAL_UV)
        .inputAssembly(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST, false)
        .viewport()
            .viewportBounds(0.0f, extent.height(), extent.width(), -extent.height())
            .viewportDepths(0.0f, 1.0f)
            .scissorOffset(0, 0)
            .scissorExtent(extent)
            .done()
        .rasterization()
            .flags(false, false, false)
            .polygonInfo(VK_POLYGON_MODE_FILL, VK_CULL_MODE_BACK_BIT, VK_FRONT_FACE_COUNTER_CLOCKWISE)
            .done()
        .multisampling()
            .sampleShading(false)
            .rasterizationSamples(VK_SAMPLE_COUNT_1_BIT)
            .done()
        .depthStencil()
            .depthEnableFlags(true, true)
            .depthCompareOp(VK_COMPARE_OP_LESS)
            .bounds(0.0f, 1.0f, false)
            .stencilTestEnable(false)
            .done()
        .colorBlend(true, VK_LOGIC_OP_COPY, 0f, 0f, 0f, 0f)
            .attachment(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT, true)
            .done()
        .pushConstants()
            .constant(VK_SHADER_STAGE_VERTEX_BIT, 0, Push.STATIC_TRANSFORM_OFFSET.sizeof())
            .done()
        .build(renderPass, setLayouts);

    PipelineConstructor MODEL_UNSHADED_TINTED_ADDITIVE = (device, extent, renderPass, setLayouts) -> PipelineBuilder
        .create(device)
        .shaders()
            .addShader(ShaderSPIRVUtils.ShaderKind.VERTEX_SHADER, "orbiter/shaders/particle/model_unshaded_tinted.vert", VK_SHADER_STAGE_VERTEX_BIT)
            .addShader(ShaderSPIRVUtils.ShaderKind.FRAGMENT_SHADER, "orbiter/shaders/particle/model_unshaded_tinted.frag", VK_SHADER_STAGE_FRAGMENT_BIT)
            .done()
        .vertexInputInfo(OrbiterVertex.POS3F_NORMAL_UV)
        .inputAssembly(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST, false)
        .viewport()
            .viewportBounds(0.0f, extent.height(), extent.width(), -extent.height())
            .viewportDepths(0.0f, 1.0f)
            .scissorOffset(0, 0)
            .scissorExtent(extent)
            .done()
        .rasterization()
            .flags(false, false, false)
            .polygonInfo(VK_POLYGON_MODE_FILL, VK_CULL_MODE_BACK_BIT, VK_FRONT_FACE_COUNTER_CLOCKWISE)
            .done()
        .multisampling()
            .sampleShading(false)
            .rasterizationSamples(VK_SAMPLE_COUNT_1_BIT)
            .done()
        .depthStencil()
            .depthEnableFlags(true, true)
            .depthCompareOp(VK_COMPARE_OP_LESS)
            .bounds(0.0f, 1.0f, false)
            .stencilTestEnable(false)
            .done()
        .colorBlend(true, VK_LOGIC_OP_COPY, 0f, 0f, 0f, 0f)
            .additive(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT)
            .done()
        .pushConstants()
            .constant(VK_SHADER_STAGE_VERTEX_BIT, 0, Push.STATIC_TRANSFORM_OFFSET.sizeof())
            .done()
        .build(renderPass, setLayouts);
}
