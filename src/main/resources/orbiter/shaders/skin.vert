#version 460
#extension GL_ARB_separate_shader_objects: enable

layout (location = 0) in vec3 inPosition;
layout (location = 1) in vec3 inNormal;
layout (location = 2) in vec2 inUv;
layout (location = 3) in int matrixIndex;

layout (location = 0) out vec2 uv;
layout (location = 1) out vec3 normal;

layout(set = 0, binding = 0) uniform GlobalUbo {
    mat4 projection;
    mat4 view;
} ubo;

layout(push_constant) uniform Push {
    int stride;
    int offset;
} push;

layout(std140, set = 0, binding = 1) readonly buffer Bones {
    mat4 transformation[32000];
} bones;

const mat4 IDENTITY = mat4(1.0);

void main() {
    mat4 transformMatrix = matrixIndex == 0 ? IDENTITY : bones.transformation[((matrixIndex - 1) % push.stride) + push.offset];

    gl_Position = ubo.projection * ubo.view * transformMatrix * vec4(inPosition, 1.0);
    uv = inUv;
    normal = normalize(mat3(transpose(inverse(transformMatrix))) * inNormal);
}