package io.github.year0c.cathode.vulkan;

public class VulkanLightContext {
    private static final ThreadLocal<Float> BRIGHTNESS = ThreadLocal.withInitial(() -> 1.0f);
    private static final ThreadLocal<Float> LIGHT_U = ThreadLocal.withInitial(() -> 0.9375f);
    private static final ThreadLocal<Float> LIGHT_V = ThreadLocal.withInitial(() -> 0.9375f);

    public static void setBrightness(float v) { BRIGHTNESS.set(v); }
    public static float getBrightness() { return BRIGHTNESS.get(); }

    public static void setLight(float u, float v) { LIGHT_U.set(u); LIGHT_V.set(v); }
    public static float getLightU() { return LIGHT_U.get(); }
    public static float getLightV() { return LIGHT_V.get(); }
}   