#version 450

layout(location = 0) in vec3 inPos;
layout(location = 1) in vec4 inColor;
layout(location = 2) in vec2 inUV;
layout(location = 3) in vec2 inLightUV;

layout(location = 0) out vec4 vColor;
layout(location = 1) out vec2 vUV;
layout(location = 2) out vec2 vLightUV;
layout(location = 3) out float vDepth;

void main() {
    vColor = inColor;
    vUV = inUV;
    vLightUV = inLightUV;
    gl_Position = vec4(inPos, 1.0);
    vDepth = gl_Position.w; // view-space depth
}   