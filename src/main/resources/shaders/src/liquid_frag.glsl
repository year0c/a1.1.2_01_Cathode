#version 450

layout(location=0) in vec4 vColor;
layout(location=1) in vec2 vUV;
layout(location=2) in vec2 vLightUV;
layout(location=3) in float vFogDist;

layout(set=0, binding=0) uniform sampler2D blockTex;
layout(set=0, binding=1) uniform sampler2D lightmap;

layout(set=0, binding=2) uniform U {
    mat4 viewProjection;
    vec3 fogColor;
    float fogDensity;
    vec3 camPos;
    float time;
} u;

layout(location=0) out vec4 fragColor;

void main() {
    vec4 tex = texture(blockTex, vUV);
    vec4 light = texture(lightmap, vLightUV);

    vec3 color = vColor.rgb * tex.rgb * light.rgb;

    // Alpha: use vertex alpha (0.7 water, 0.5 lava)
    float alpha = vColor.a;

    // Fog
    const float LOG2 = 1.4426950408889634;
    float fogFactor = exp2(-u.fogDensity * u.fogDensity * vFogDist * vFogDist * LOG2);
    fogFactor = clamp(fogFactor, 0.0, 1.0);

    vec3 fogged = mix(u.fogColor, color, fogFactor);
    fragColor = vec4(fogged, alpha);
}   