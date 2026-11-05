#version 450

layout(location = 0) in vec4 vColor;
layout(location = 1) in vec2 vUV;
layout(location = 2) in vec2 vLightUV;
layout(location = 3) in float vDepth;

layout(set = 0, binding = 0) uniform sampler2D blockIcon;
layout(set = 0, binding = 1) uniform sampler2D lightmap;
layout(set = 0, binding = 2) uniform FogUniforms {
    vec3 fogColor;
    float fogDensity;
} fog;

layout(location = 0) out vec4 fragColor;

void main() {
    vec4 icon  = texture(blockIcon, vUV);
    vec3 light = texture(lightmap, vLightUV).rgb;
    vec3 color = vColor.rgb * icon.rgb * light;

    // Linear fog: 0 at near, 1 at far
    float fogFactor = clamp(vDepth * fog.fogDensity, 0.0, 1.0);
    color = mix(color, fog.fogColor, fogFactor);

    fragColor = vec4(color, vColor.a * icon.a);
}   