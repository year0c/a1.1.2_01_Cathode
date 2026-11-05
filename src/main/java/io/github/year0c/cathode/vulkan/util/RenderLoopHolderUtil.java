package io.github.year0c.cathode.vulkan.util;

import io.github.year0c.cathode.vulkan.RenderLoop;

public class RenderLoopHolderUtil {
    private static RenderLoop instance;

    public static void set(RenderLoop rl) { instance = rl; }
    public static RenderLoop get() { return instance; }
}