#version 450

layout(location = 0) in vec2 vUV;

layout(set = 0, binding = 0) uniform SkyUniforms {
    vec3 zenithColor;
    vec3 horizonColor;
    vec3 sunDir;
    float starAlpha;
    vec3 cameraPos;
    float fov;
    float aspect;
    float _pad;
} sky;

layout(location = 0) out vec4 fragColor;

void main() {
    // Reconstruct view direction
    vec2 ndc = vUV * 2.0 - 1.0;
    ndc.x *= sky.aspect;
    float tanHalfFov = tan(radians(sky.fov) * 0.5);
    vec3 dir = normalize(vec3(ndc * tanHalfFov, 1.0));

    // Gradient
    float h = clamp(dir.y, 0.0, 1.0);
    h = pow(h, 0.8);
    vec3 skyColor = mix(sky.horizonColor, sky.zenithColor, h);

    vec3 sun = normalize(sky.sunDir);

    // Build sun-local basis
    vec3 sunRef = abs(sun.y) > 0.99 ? vec3(1,0,0) : vec3(0,1,0);
    vec3 sunRight = normalize(cross(sunRef, sun));
    vec3 sunUp = cross(sun, sunRight);

    // Project view dir into sun-local plane
    float sX = dot(dir, sunRight);
    float sY = dot(dir, sunUp);

    // === SUN: flat square ===
    float sunHalf = 0.10; // ~6 degrees half-angle
    float sunSDF = max(abs(sX), abs(sY)) - sunHalf;
    float sunMask = 1.0 - smoothstep(-0.015, 0.0, sunSDF);

    // Soft glow around sun
    float sunGlow = exp(-max(abs(sX), abs(sY)) * 15.0) * 0.25;
    // Only glow when sun is near horizon
    float glowMask = 1.0 - smoothstep(0.1, 0.5, sun.y);
    vec3 sunGlowColor = vec3(1.0, 0.6, 0.3) * sunGlow * glowMask;

    // Sun disc color (warm white)
    vec3 sunColor = vec3(1.0, 0.95, 0.8);
    skyColor = mix(skyColor, sunColor, sunMask);
    skyColor += sunGlowColor;

    // === MOON: flat square, opposite sun ===
    vec3 moonDir = -sun;
    vec3 moonRef = abs(moonDir.y) > 0.99 ? vec3(1,0,0) : vec3(0,1,0);
    vec3 moonRight = normalize(cross(moonRef, moonDir));
    vec3 moonUp = cross(moonDir, moonRight);
    float mX = dot(dir, moonRight);
    float mY = dot(dir, moonUp);

    float moonHalf = 0.08; // slightly smaller than sun
    float moonSDF = max(abs(mX), abs(mY)) - moonHalf;
    float moonMask = 1.0 - smoothstep(-0.01, 0.0, moonSDF);
    // Moon only visible at night
    float moonVisibility = sky.starAlpha;
    vec3 moonColor = vec3(0.85, 0.85, 0.8);
    skyColor = mix(skyColor, moonColor, moonMask * moonVisibility);

    // === STARS: small rotated squares ===
    if (sky.starAlpha > 0.001 && dir.y > 0.0) {
        vec2 starUV = dir.xz / (dir.y + 0.1) * 100.0;
        vec2 cell = floor(starUV);
        float h1 = fract(sin(dot(cell, vec2(12.9898, 78.233))) * 43758.5453);
        float h2 = fract(sin(dot(cell, vec2(78.233, 12.9898))) * 43758.5453);
        float h3 = fract(sin(dot(cell, vec2(39.348, 11.135))) * 43758.5453);

        // ~3% of cells have a star
        if (h1 > 0.97) {
            vec2 center = vec2(h2, h3) * 0.6 + 0.2;
            vec2 local = fract(starUV) - center;
            // Random rotation
            float angle = h1 * 6.2832;
            float c = cos(angle), s = sin(angle);
            local = mat2(c, -s, s, c) * local;
            // Size: 0.3-0.5 unit equivalent
            float size = 0.02 + h2 * 0.015;
            float starSDF = max(abs(local.x), abs(local.y)) - size;
            float star = 1.0 - smoothstep(-0.005, 0.0, starSDF);
            // Slight brightness variation
            float brightness = 0.5 + 0.5 * h3;
            skyColor += vec3(star) * sky.starAlpha * brightness;
        }
    }

    fragColor = vec4(skyColor, 1.0);
}   