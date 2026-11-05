package io.github.year0c.cathode.vulkan;

import io.github.year0c.cathode.vulkan.util.VulkanUtil;

public class VulkanSkyShader {

    public static float[] zenithColor = {0.45f, 0.75f, 1.0f};
    public static float[] horizonColor = {0.65f, 0.85f, 1.0f};
    public static float[] sunDir = {0f, 1f, 0f};
    public static float starAlpha = 0f;

    // Order: sunrise(0) → noon(0.25) → sunset(0.5) → midnight(0.75) → sunrise(1.0)
    private static final float[][] KEYFRAMES = {
        {0.30f, 0.40f, 0.80f, 0.95f, 0.55f, 0.30f},  // sunrise
        {0.45f, 0.75f, 1.00f, 0.65f, 0.85f, 1.00f},  // noon
        {0.30f, 0.40f, 0.80f, 0.95f, 0.55f, 0.30f},  // sunset
        {0.02f, 0.03f, 0.08f, 0.04f, 0.05f, 0.12f},  // midnight
        {0.30f, 0.40f, 0.80f, 0.95f, 0.55f, 0.30f},  // sunrise (wrap)
    };

    public static void updateSky(float timeOfDay) {
        float t = timeOfDay * 4.0f;
        int idx = (int) t % 4;
        float frac = t - (int) t;

        float[] from = KEYFRAMES[idx];
        float[] to = KEYFRAMES[idx + 1];

        for (int i = 0; i < 3; i++) {
            zenithColor[i] = from[i] + (to[i] - from[i]) * frac;
            horizonColor[i] = from[i + 3] + (to[i + 3] - from[i + 3]) * frac;
        }

        VulkanUtil.fogColor[0] = horizonColor[0];
        VulkanUtil.fogColor[1] = horizonColor[1];
        VulkanUtil.fogColor[2] = horizonColor[2];

        float angle = timeOfDay * 2.0f * (float) Math.PI;
        sunDir[0] = (float) Math.cos(angle);
        sunDir[1] = (float) Math.sin(angle);
        sunDir[2] = 0.0f;

        float fadeInStart = 0.482f;
        float fadeInEnd = 0.551f;
        float fadeOutStart = 0.949f;
        float fadeOutEnd = 0.018f;
        float fadeOutDuration = 1.0f - fadeOutStart + fadeOutEnd;

        float star;
        if (timeOfDay >= fadeInStart && timeOfDay < fadeInEnd) {
            star = (timeOfDay - fadeInStart) / (fadeInEnd - fadeInStart);
        } else if (timeOfDay >= fadeInEnd && timeOfDay < fadeOutStart) {
            star = 1.0f;
        } else if (timeOfDay >= fadeOutStart) {
            star = 1.0f - (timeOfDay - fadeOutStart) / fadeOutDuration;
        } else if (timeOfDay < fadeOutEnd) {
            star = 1.0f - (1.0f - fadeOutStart + timeOfDay) / fadeOutDuration;
        } else {
            star = 0.0f;
        }
        starAlpha = Math.max(0.0f, Math.min(1.0f, star));
    }
}   