#version 450

layout(location = 0) in vec2 vUV;

layout(set = 0, binding = 0) uniform SkyUniforms {
    vec3 zenithColor;    // 0
    vec3 horizonColor;   // 12
    vec3 sunDir;         // 24
    float starAlpha;     // 36
    vec3 cameraPos;      // 40
    float fov;           // 52  (half-angle, radians)
    float aspect;        // 56
    float yaw;           // 60  (radians, MC convention: 0=south, positive=west)
    float pitch;         // 64  (radians, 0=horizon, positive=up)
} sky;

layout(location = 0) out vec4 fragColor;

void main() {
    // Reconstruct view direction in camera-space
    vec2 ndc = vUV * 2.0 - 1.0;
    ndc.x *= sky.aspect;
    float tanHalfFov = tan(sky.fov);
    vec3 dir = normalize(vec3(ndc * tanHalfFov, 1.0));

    // Camera-space -> World-space
    float cp = cos(sky.pitch), sp = sin(sky.pitch);
    float ny = dir.y * cp + dir.z * sp;
    float nz = -dir.y * sp + dir.z * cp;
    dir.y = ny;
    dir.z = nz;

    float cy = cos(sky.yaw), sy = sin(sky.yaw);
    float nx = dir.x * cy - dir.z * sy;
    nz = dir.x * sy + dir.z * cy;
    dir.x = nx;
    dir.z = nz;

    dir = normalize(dir);

    // Banded gradient (retro look)
    float h = clamp(dir.y, 0.0, 1.0);
    h = pow(h, 0.8);
    h = floor(h * 12.0) / 12.0;
    vec3 skyColor = mix(sky.horizonColor, sky.zenithColor, h);

    vec3 sun = normalize(sky.sunDir);

    // Sun-local basis
    vec3 sunRef = abs(sun.y) > 0.99 ? vec3(1,0,0) : vec3(0,1,0);
    vec3 sunRight = normalize(cross(sunRef, sun));
    vec3 sunUp = cross(sun, sunRight);

    float sX = dot(dir, sunRight);
    float sY = dot(dir, sunUp);

    // Sun: flat square
    float sunHalf = 0.10;
    float sunSDF = max(abs(sX), abs(sY)) - sunHalf;
    float sunMask = 1.0 - smoothstep(-0.015, 0.0, sunSDF);

    float sunGlow = exp(-max(abs(sX), abs(sY)) * 15.0) * 0.25;
    float glowMask = 1.0 - smoothstep(0.1, 0.5, sun.y);
    vec3 sunGlowColor = vec3(1.0, 0.6, 0.3) * sunGlow * glowMask;

    vec3 sunColor = vec3(1.0, 0.95, 0.8);
    skyColor = mix(skyColor, sunColor, sunMask);
    skyColor += sunGlowColor;

    // Moon: flat square, opposite sun
    vec3 moonDir = -sun;
    vec3 moonRef = abs(moonDir.y) > 0.99 ? vec3(1,0,0) : vec3(0,1,0);
    vec3 moonRight = normalize(cross(moonRef, moonDir));
    vec3 moonUp = cross(moonDir, moonRight);
    float mX = dot(dir, moonRight);
    float mY = dot(dir, moonUp);

    float moonHalf = 0.08;
    float moonSDF = max(abs(mX), abs(mY)) - moonHalf;
    float moonMask = 1.0 - smoothstep(-0.01, 0.0, moonSDF);
    float moonVisibility = sky.starAlpha;
    vec3 moonColor = vec3(0.85, 0.85, 0.8);
    skyColor = mix(skyColor, moonColor, moonMask * moonVisibility);

    // Stars: small rotated squares
    if (sky.starAlpha > 0.001 && dir.y > 0.0) {
        vec2 starUV = dir.xz / (dir.y + 0.1) * 100.0;
        vec2 cell = floor(starUV);
        float h1 = fract(sin(dot(cell, vec2(12.9898, 78.233))) * 43758.5453);
        float h2 = fract(sin(dot(cell, vec2(78.233, 12.9898))) * 43758.5453);
        float h3 = fract(sin(dot(cell, vec2(39.348, 11.135))) * 43758.5453);

        if (h1 > 0.97) {
            vec2 center = vec2(h2, h3) * 0.6 + 0.2;
            vec2 local = fract(starUV) - center;
            float angle = h1 * 6.2832;
            float c = cos(angle), s = sin(angle);
            local = mat2(c, -s, s, c) * local;
            float size = 0.02 + h2 * 0.015;
            float starSDF = max(abs(local.x), abs(local.y)) - size;
            float star = 1.0 - smoothstep(-0.005, 0.0, starSDF);
            float brightness = 0.5 + 0.5 * h3;
            skyColor += vec3(star) * sky.starAlpha * brightness;
        }
    }

    fragColor = vec4(skyColor, 1.0);
}   