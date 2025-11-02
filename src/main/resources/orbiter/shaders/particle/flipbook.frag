#version 450
#extension GL_ARB_separate_shader_objects: enable

layout (location = 0) in vec2 uv;

layout (location = 0) out vec4 outColor;

layout(set = 0, binding = 1) uniform sampler2D texSampler;

layout(push_constant) uniform Push {
    // Dimensions in UV coordinates of atlas
    vec4 dimensions; // 4 * 4

    // Size of single sprite
    vec2 singleSize; // 4 * 2
    int indexFrom;   // 4
    int indexTo;     // 4

    float transition;// 4
    int flags;       // 4
    vec2 pixelScale; // 4 * 2
} push;

bool isInterpolation(int flags) { return (flags & 0x01) == 1; }
bool isOkLab(int flags)  { return ((flags >> 1) & 0x01) == 1; }

float map(float value, float inMin, float inMax, float outMin, float outMax)
{
    return outMin + (outMax - outMin) * (value - inMin) / (inMax - inMin);
}

// https://www.shadertoy.com/view/ttcyRS
vec3 oklab_mix( vec3 colA, vec3 colB, float h )
{
    // https://bottosson.github.io/posts/oklab
    const mat3 kCONEtoLMS = mat3(
    0.4122214708,  0.2119034982,  0.0883024619,
    0.5363325363,  0.6806995451,  0.2817188376,
    0.0514459929,  0.1073969566,  0.6299787005);
    const mat3 kLMStoCONE = mat3(
     4.0767416621,  -1.2684380046, -0.0041960863,
    -3.3077115913,   2.6097574011, -0.7034186147,
     0.2309699292,  -0.3413193965,  1.7076147010);

    // rgb to cone (arg of pow can't be negative)
    vec3 lmsA = pow(kCONEtoLMS * colA, vec3(1.0 / 3.0));
    vec3 lmsB = pow(kCONEtoLMS * colB, vec3(1.0 / 3.0));
    // lerp
    vec3 lms = mix(lmsA, lmsB, h);
    // gain in the middle (no oaklab anymore, but looks better?)
    // lms *= 1.0+0.2*h*(1.0-h);
    // cone to rgb
    return kLMStoCONE * (lms * lms * lms);
}

void main()
{
    vec2 scale = push.pixelScale / push.singleSize;

    if (isInterpolation(push.flags))
    {
        vec2 indiciesFrom = vec2(push.indexFrom % int(scale.x), push.indexFrom / int(scale.x));
        vec2 indiciesTo = vec2(push.indexTo % int(scale.x), push.indexTo / int(scale.x));

        vec2 uvFrom = vec2(uv);
        uvFrom.x = map(uvFrom.x, -indiciesFrom.x, scale.x - indiciesFrom.x, push.dimensions.x, push.dimensions.z);
        uvFrom.y = map(uvFrom.y, -indiciesFrom.y, scale.y - indiciesFrom.y, push.dimensions.y, push.dimensions.w);

        vec2 uvTo = vec2(uv);
        uvTo.x = map(uvTo.x, -indiciesTo.x, scale.x - indiciesTo.x, push.dimensions.x, push.dimensions.z);
        uvTo.y = map(uvTo.y, -indiciesTo.y, scale.y - indiciesTo.y, push.dimensions.y, push.dimensions.w);

        vec4 colFrom = texture(texSampler, uvFrom);
        vec4 colTo = texture(texSampler, uvTo);

        if (isOkLab(push.flags))
        {
            outColor = vec4(oklab_mix(colFrom.rgb, colTo.rgb, push.transition), mix(colFrom.a, colTo.a, push.transition));
        }
        else
        {
            outColor = mix(colFrom, colTo, push.transition);
        }
        if (outColor.a == 0) discard;
    } else
    {
        vec2 indicies = vec2(push.indexFrom % int(scale.x), push.indexFrom / int(scale.x));

        vec2 theUv = vec2(uv);
        theUv.x = map(theUv.x, -indicies.x, scale.x - indicies.x, push.dimensions.x, push.dimensions.z);
        theUv.y = map(theUv.y, -indicies.y, scale.y - indicies.y, push.dimensions.y, push.dimensions.w);

        outColor = texture(texSampler, theUv);
        if (outColor.a == 0) discard;
    }
}