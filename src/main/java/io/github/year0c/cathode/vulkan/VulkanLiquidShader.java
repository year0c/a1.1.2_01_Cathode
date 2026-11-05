package io.github.year0c.cathode.vulkan;

import net.minecraft.world.World;
import net.minecraft.world.LightType;

public class VulkanLiquidShader {

    public static final float[] AO_LEVELS = {0.5f, 0.625f, 0.8125f, 1.0f};

    public static void push(World w, float x, float y, float z,
                            float u, float v, int color, float alpha, SectionMesh mesh) {
        int bx = (int) Math.floor(x);
        int by = (int) Math.floor(y);
        int bz = (int) Math.floor(z);

        int sky = w.getLight(LightType.SKY, bx, by, bz);
        int blockLight = w.getLight(LightType.BLOCK, bx, by, bz);
        float lu = (sky + 0.5f) / 16.0f;
        float lv = (blockLight + 0.5f) / 16.0f;

        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        mesh.pushLiquid(x, y, z, r, g, b, alpha, u, v, lu, lv);
    }

    /**
     * Push a sub-quad (6 verts, 2 triangles) with per-vertex AO.
     *
     * @param ao0–ao5  AO multiplier per vertex (0.0–1.0, use AO_LEVELS values)
     */
    public static void pushQuad(World w,
                                float x0, float y0, float z0,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float x3, float y3, float z3,
                                float x4, float y4, float z4,
                                float x5, float y5, float z5,
                                float u0, float v0,
                                float u1, float v1,
                                float u2, float v2,
                                float u3, float v3,
                                float u4, float v4,
                                float u5, float v5,
                                int color,
                                float ao0, float ao1, float ao2,
                                float ao3, float ao4, float ao5,
                                float alpha, SectionMesh mesh) {
        int bx = (int) Math.floor(x0);
        int by = (int) Math.floor(y0);
        int bz = (int) Math.floor(z0);

        int sky = w.getLight(LightType.SKY, bx, by, bz);
        int blockLight = w.getLight(LightType.BLOCK, bx, by, bz);
        float lu = (sky + 0.5f) / 16.0f;
        float lv = (blockLight + 0.5f) / 16.0f;

        float cr = ((color >> 16) & 0xFF) / 255.0f;
        float cg = ((color >> 8) & 0xFF) / 255.0f;
        float cb = (color & 0xFF) / 255.0f;

        mesh.pushLiquid(x0, y0, z0, cr * ao0, cg * ao0, cb * ao0, alpha, u0, v0, lu, lv);
        mesh.pushLiquid(x1, y1, z1, cr * ao1, cg * ao1, cb * ao1, alpha, u1, v1, lu, lv);
        mesh.pushLiquid(x2, y2, z2, cr * ao2, cg * ao2, cb * ao2, alpha, u2, v2, lu, lv);
        mesh.pushLiquid(x3, y3, z3, cr * ao3, cg * ao3, cb * ao3, alpha, u3, v3, lu, lv);
        mesh.pushLiquid(x4, y4, z4, cr * ao4, cg * ao4, cb * ao4, alpha, u4, v4, lu, lv);
        mesh.pushLiquid(x5, y5, z5, cr * ao5, cg * ao5, cb * ao5, alpha, u5, v5, lu, lv);
    }

    public static void pushWater(World w, float x, float y, float z,
                                  float u, float v, int color, SectionMesh mesh) {
        push(w, x, y, z, u, v, color, 0.7f, mesh);
    }

    public static void pushLava(World w, float x, float y, float z,
                                 float u, float v, int color, SectionMesh mesh) {
        push(w, x, y, z, u, v, color, 0.5f, mesh);
    }
}   