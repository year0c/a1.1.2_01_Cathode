#version 450

layout(location=0) in vec3 inPos;
layout(location=1) in vec4 inColor;
layout(location=2) in vec2 inUV;
layout(location=3) in vec2 inLightUV;

layout(set=0, binding=2) uniform U {
    mat4 viewProjection;
    vec3 fogColor;
    float fogDensity;
    vec3 camPos;
    float time;
} u;

layout(location=0) out vec4 vColor;
layout(location=1) out vec2 vUV;
layout(location=2) out vec2 vLightUV;
layout(location=3) out float vFogDist;

void main() {
    vColor = inColor;
    vUV = inUV;
    vLightUV = inLightUV;
    vFogDist = distance(inPos, u.camPos);
    gl_Position = u.viewProjection * vec4(inPos, 1.0);
}   