#version 450
#extension GL_ARB_separate_shader_objects: enable

layout (location = 0) in vec3 inPosition;
layout (location = 1) in vec3 inNormal;
layout (location = 2) in vec2 inUv;

layout (location = 0) out vec2 uv;
layout (location = 1) out vec4 tint;

layout(set = 0, binding = 0) uniform CameraUBO {
    mat4 projection;
    mat4 view;
} camera;
// binding 1 is texture

struct Entry
{
    mat4 transformation;
    vec4 tint;
};

layout(std140, set = 0, binding = 2) readonly buffer SSBO {
    Entry entries[];
} ssbo;

layout(push_constant) uniform Push {
    int arrayIndex;
} push;

void main() {
    Entry entry = ssbo.entries[gl_InstanceIndex + push.arrayIndex];
    mat4 transform = entry.transformation;

    gl_Position = camera.projection * camera.view * transform * vec4(inPosition, 1.0);

    uv = inUv;
    tint = entry.tint;
}