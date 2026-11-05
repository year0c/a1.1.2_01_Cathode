package io.github.year0c.cathode.vulkan;

import net.minecraft.world.World;
import net.minecraft.world.LightType;
import net.minecraft.block.Block;

public class VulkanSurfaceShader {

    public static final float[] AO_LEVELS = {0.5f, 0.625f, 0.8125f, 1.0f};

    /**
     * Push a single vertex with no AO (used as fallback).
     */
    public static void push(World w, float x, float y, float z,
                            float u, float v, int color, SectionMesh mesh) {
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

        mesh.pushSolid(x, y, z, r, g, b, 1.0f, u, v, lu, lv);
    }

    /**
     * Push a sub-quad (6 verts, 2 triangles) with per-vertex AO.
     * Each vertex gets its own AO multiplier, enabling smooth
     * gradients within the quad and hard steps at shared edges.
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
                                SectionMesh mesh) {
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

        mesh.pushSolid(x0, y0, z0, cr * ao0, cg * ao0, cb * ao0, 1.0f, u0, v0, lu, lv);
        mesh.pushSolid(x1, y1, z1, cr * ao1, cg * ao1, cb * ao1, 1.0f, u1, v1, lu, lv);
        mesh.pushSolid(x2, y2, z2, cr * ao2, cg * ao2, cb * ao2, 1.0f, u2, v2, lu, lv);
        mesh.pushSolid(x3, y3, z3, cr * ao3, cg * ao3, cb * ao3, 1.0f, u3, v3, lu, lv);
        mesh.pushSolid(x4, y4, z4, cr * ao4, cg * ao4, cb * ao4, 1.0f, u4, v4, lu, lv);
        mesh.pushSolid(x5, y5, z5, cr * ao5, cg * ao5, cb * ao5, 1.0f, u5, v5, lu, lv);
    }

    /**
     * Compute AO level (0–3) for a vertex at a face corner.
     *
     * @param w    world
     * @param x,y,z  vertex position (integer block coords)
     * @param nx,ny,nz  face normal
     * @param tx,ty,tz  tangent (direction toward the next vertex along the face)
     * @return 0 (darkest) to 3 (no occlusion)
     */
    public static int computeAO(World w, int x, int y, int z,
                                int nx, int ny, int nz,
                                int tx, int ty, int tz) {
        // Bitangent = normal × tangent
        int bx = ny * tz - nz * ty;
        int by = nz * tx - nx * tz;
        int bz = nx * ty - ny * tx;

        // Side 1: offset by tangent
        boolean s1 = isOpaque(w, x + tx, y + ty, z + tz);
        // Side 2: offset by bitangent
        boolean s2 = isOpaque(w, x + bx, y + by, z + bz);
        // Corner: offset by both
        boolean c  = isOpaque(w, x + tx + bx, y + ty + by, z + tz + bz);

        if (s1 && s2) return 0;
        int level = 3;
        if (c)  level--;
        if (s1) level--;
        if (s2) level--;
        return level;
    }

    private static boolean isOpaque(World w, int x, int y, int z) {
        if (y < 0 || y >= 128) return false;
        int b = w.getBlock(x, y, z);
        if (b == 0) return false;
        return Block.OPACITIES[b] >= 15;
    }
}   