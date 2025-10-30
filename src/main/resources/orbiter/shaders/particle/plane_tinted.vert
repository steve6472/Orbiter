#version 450
#extension GL_ARB_separate_shader_objects: enable

layout (location = 0) in vec3 inPosition;
layout (location = 1) in vec4 inColor;
layout (location = 2) in vec2 inUv;

layout (location = 0) out vec4 tint;
layout (location = 1) out vec2 uv;

layout(set = 0, binding = 0) uniform CameraUBO {
    mat4 projection;
    mat4 view;
} camera;

void main() {
    gl_Position = camera.projection * camera.view * vec4(inPosition, 1.0);

    tint = inColor;
    uv = inUv;
}