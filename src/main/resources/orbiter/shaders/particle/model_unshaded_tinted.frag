#version 450
#extension GL_ARB_separate_shader_objects: enable

layout (location = 0) in vec2 uv;
layout (location = 1) in vec4 tint;

layout (location = 0) out vec4 outColor;

layout(set = 0, binding = 1) uniform sampler2D texSampler;


void main() {
    vec4 textureColor = texture(texSampler, uv);
    if (textureColor.a == 0) discard;

    outColor = textureColor * tint;
}