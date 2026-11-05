package io.github.year0c.cathode.vulkan;

import net.minecraft.world.World;
import net.minecraft.world.LightType;
import io.github.year0c.cathode.vulkan.util.VulkanLightContextUtil;
import net.minecraft.block.Block;

public class VulkanShader {

    // --- Sky state (updated per tick) ---
    public static float[] zenithColor = {0.45f, 0.75f, 1.0f};
    public static float[] horizonColor = {0.65f, 0.85f, 1.0f};
    public static float[] sunDir = {0f, 1f, 0f};
    public static float starAlpha = 0f;

    // --- Fog state ---
    public static float[] fogColor = {0.65f, 0.85f, 1.0f};
    public static float fogDensity = 0.004f;

    private static final float[][] KEYFRAMES = {
        {0.45f, 0.75f, 1.00f, 0.65f, 0.85f, 1.00f},  // noon
        {0.30f, 0.40f, 0.80f, 0.95f, 0.55f, 0.30f},  // sunset
        {0.02f, 0.03f, 0.08f, 0.04f, 0.05f, 0.12f},  // midnight (dark blue)
        {0.30f, 0.40f, 0.80f, 0.95f, 0.55f, 0.30f},  // sunrise
        {0.45f, 0.75f, 1.00f, 0.65f, 0.85f, 1.00f},  // noon
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

        fogColor[0] = horizonColor[0];
        fogColor[1] = horizonColor[1];
        fogColor[2] = horizonColor[2];

        float angle = timeOfDay * 2.0f * (float) Math.PI;
        sunDir[0] = (float) Math.cos(angle);
        sunDir[1] = (float) Math.sin(angle);
        sunDir[2] = 0.0f;

        // Stars: MC ticks 11576→13219 (fade in), 22782→425 (fade out)
        // Normalized: 0.482→0.551, 0.949→0.018
        float fadeInStart = 0.482f;
        float fadeInEnd = 0.551f;
        float fadeOutStart = 0.949f;
        float fadeOutEnd = 0.018f;

        float star;
        if (timeOfDay >= fadeInStart && timeOfDay < fadeInEnd) {
            star = (timeOfDay - fadeInStart) / (fadeInEnd - fadeInStart);
        } else if (timeOfDay >= fadeInEnd && timeOfDay < fadeOutStart) {
            star = 1.0f;
        } else if (timeOfDay >= fadeOutStart) {
            star = 1.0f - (timeOfDay - fadeOutStart) / (1.0f - fadeOutStart + fadeOutEnd);
        } else {
            star = 0.0f;
        }
        starAlpha = Math.max(0, Math.min(1, star));
    }   

    // --- Per-vertex block lighting ---
    public static void compute(World w, int x, int y, int z) {
        int sky = computeSkyLight(w, x, y, z);
        int blockLight = w.getLight(LightType.BLOCK, x, y, z);
        VulkanLightContextUtil.setLight(
            (sky + 0.5f) / 16.0f,
            (blockLight + 0.5f) / 16.0f
        );
    }

    public static int computeSkyLight(World w, int x, int y, int z) {
        int totalOpacity = 0;
        for (int sy = y; sy < 128; sy++) {
            int block = w.getBlock(x, sy, z);
            if (block < 0 || block >= Block.OPACITIES.length) break;
            int opacity = Block.OPACITIES[block];
            if (opacity >= 15) return 0;
            totalOpacity += opacity;
            if (totalOpacity >= 15) return 0;
        }
        return 15 - totalOpacity;
    }
}   