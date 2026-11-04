package io.github.year0c.cathode.vulkan;

public class RenderLoopHolder {
    private static RenderLoop instance;

    public static void set(RenderLoop rl) { instance = rl; }
    public static RenderLoop get() { return instance; }
}