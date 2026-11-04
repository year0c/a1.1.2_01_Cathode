#version 450

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aUV;
layout(location = 3) in vec2 aLightUV;

layout(location = 0) out vec4 vColor;
layout(location = 1) out vec2 vUV;
layout(location = 2) out vec2 vLightUV;

void main() {
    vColor   = aColor;
    vUV      = aUV;
    vLightUV = aLightUV;
    gl_Position = vec4(aPosition, 1.0);
}   