package io.github.year0c.cathode.vulkan.util;

public class VulkanLightContextUtil {
    private static final ThreadLocal<Float> BRIGHTNESS = ThreadLocal.withInitial(() -> 1.0f);
    private static final ThreadLocal<Float> LIGHT_U = ThreadLocal.withInitial(() -> 0.9375f);
    private static final ThreadLocal<Float> LIGHT_V = ThreadLocal.withInitial(() -> 0.9375f);
    private static final ThreadLocal<Boolean> IS_LIQUID = ThreadLocal.withInitial(() -> false);

    public static void setBrightness(float v) { BRIGHTNESS.set(v); }
    public static float getBrightness() { return BRIGHTNESS.get(); }

    public static void setLight(float u, float v) { LIGHT_U.set(u); LIGHT_V.set(v); }
    public static float getLightU() { return LIGHT_U.get(); }
    public static float getLightV() { return LIGHT_V.get(); }

    public static void setLiquid(boolean v) { IS_LIQUID.set(v); }
    public static boolean isLiquid() { return IS_LIQUID.get(); }   
}   