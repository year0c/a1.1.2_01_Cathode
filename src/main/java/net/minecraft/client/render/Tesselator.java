package net.minecraft.client.render;

import io.github.year0c.cathode.vulkan.RenderLoop;

public class Tesselator {

    private static RenderLoop renderLoop;

    private float currentR, currentG, currentB, currentA;
    private float currentU, currentV;
    private float lightU = 0.9375f;
    private float lightV = 0.9375f;

    public static void setRenderLoop(RenderLoop rl) {
        renderLoop = rl;
    }

    public Tesselator() {}

    public void color(float r, float g, float b, float a) {
        this.currentR = r;
        this.currentG = g;
        this.currentB = b;
        this.currentA = a;
    }

    public void texture(float u, float v) {
        this.currentU = u;
        this.currentV = v;
    }

    public void setLight(float skyLevel, float blockLevel) {
        this.lightU = (skyLevel + 0.5f) / 16.0f;
        this.lightV = (blockLevel + 0.5f) / 16.0f;
    }

    public void vertex(double x, double y, double z) {
        renderLoop.pushVertex(
            (float) x, (float) y, (float) z,
            currentR, currentG, currentB, currentA,
            currentU, currentV,
            lightU, lightV
        );
    }
}