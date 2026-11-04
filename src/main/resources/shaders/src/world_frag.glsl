#version 450

layout(location = 0) in vec4 vColor;
layout(location = 1) in vec2 vUV;
layout(location = 2) in vec2 vLightUV;

layout(set = 0, binding = 0) uniform sampler2D blockIcon;
layout(set = 0, binding = 1) uniform sampler2D lightmap;

layout(location = 0) out vec4 fragColor;

void main() {
    vec4 icon  = texture(blockIcon, vUV);
    vec3 light = texture(lightmap, vLightUV).rgb;
    fragColor = vec4(vColor.rgb * icon.rgb * light, vColor.a * icon.a);
}   