package io.github.year0c.cathode.vulkan;

import net.minecraft.world.World;
import net.minecraft.block.Block;

import static io.github.year0c.cathode.vulkan.VulkanSurfaceShader.AO_LEVELS;

public class ChunkMesher {

    private static final int ATLAS_SIZE = 8;

    private static final int[][] TILE = {
        {},           // 0  air
        {0, 0},      // 1  stone
        {1, 0},      // 2  grass (top)
        {2, 0},      // 3  dirt
        {3, 0},      // 4  cobblestone
        {4, 0},      // 5  planks
        {},           // 6
        {5, 0},      // 7  bedrock
        {5, 1},      // 8  flowing water
        {6, 0},      // 9  still water
        {7, 1},      // 10 flowing lava
        {7, 0},      // 11 still lava
        {0, 2},      // 12 sand
        {1, 2},      // 13 gravel
        {2, 2},      // 14 gold ore
        {3, 2},      // 15 iron ore
        {4, 2},      // 16 coal ore
        {5, 2},      // 17 log (side)
        {6, 2},      // 18 log (top)
        {7, 2},      // 19 leaves
        {0, 3},      // 20 sponge
        {1, 3},      // 21 glass
        {2, 3},      // 22 gold block
        {3, 3},      // 23 iron block
        {4, 3},      // 24 double slab
        {5, 3},      // 25 slab
        {6, 3},      // 26 brick
        {7, 3},      // 27 TNT
        {0, 4},      // 28 bookshelf
        {1, 4},      // 29 mossy cobble
        {2, 4},      // 30 obsidian
        {3, 4},      // 31 torch
        {4, 4},      // 32 fire
        {5, 4},      // 33 spawner
        {6, 4},      // 34 golden stair
        {7, 4},      // 35 wooden stair
        {0, 5},      // 36 flower
        {1, 5},      // 37 rose
        {2, 5},      // 38 brown mushroom
        {3, 5},      // 39 red mushroom
        {4, 5},      // 40
        {5, 5},      // 41 wood
        {6, 5},      // 42
        {7, 5},      // 43
    };

    private static final int[] GRASS_SIDE = {0, 1};

    public static void compileSection(World world, int sectionX, int sectionY, int sectionZ,
                                       SectionMesh mesh) {
        int baseX = sectionX << 4;
        int baseY = sectionY << 4;
        int baseZ = sectionZ << 4;

        for (int lx = 0; lx < 16; lx++) {
            for (int ly = 0; ly < 16; ly++) {
                for (int lz = 0; lz < 16; lz++) {
                    int x = baseX + lx;
                    int y = baseY + ly;
                    int z = baseZ + lz;

                    int blockId = world.getBlock(x, y, z);
                    if (blockId == 0) continue;

                    if (RenderLayer.getLayer(blockId) == RenderLayer.SOLID) {
                        renderSolid(world, x, y, z, blockId, mesh);
                    } else {
                        renderLiquid(world, x, y, z, blockId, mesh);
                    }
                }
            }
        }
    }

    // ==================== SOLID ====================

    private static void renderSolid(World w, int x, int y, int z, int blockId, SectionMesh mesh) {
        int color = 0xFFFFFF;

        if (faceVisible(w, x, y + 1, z, blockId))
            renderTop(w, x, y, z, getUV(blockId, 0), color, mesh);
        if (faceVisible(w, x, y - 1, z, blockId))
            renderBottom(w, x, y, z, getUV(blockId, 0), color, mesh);
        if (faceVisible(w, x, y, z - 1, blockId))
            renderNorth(w, x, y, z, getUV(blockId, 1), color, mesh);
        if (faceVisible(w, x, y, z + 1, blockId))
            renderSouth(w, x, y, z, getUV(blockId, 1), color, mesh);
        if (faceVisible(w, x - 1, y, z, blockId))
            renderWest(w, x, y, z, getUV(blockId, 1), color, mesh);
        if (faceVisible(w, x + 1, y, z, blockId))
            renderEast(w, x, y, z, getUV(blockId, 1), color, mesh);
    }

    private static boolean faceVisible(World w, int x, int y, int z, int selfId) {
        if (y < 0 || y >= 128) return true;
        int neighbor = w.getBlock(x, y, z);
        if (neighbor == 0) return true;
        if (neighbor == selfId) return false;
        return Block.OPACITIES[neighbor] < 15;
    }

    private static float[] getUV(int blockId, int face) {
        int[] tile;
        if (blockId == 2) {
            tile = (face == 0) ? TILE[2] : GRASS_SIDE;
        } else if (blockId == 17) {
            tile = (face == 0) ? TILE[18] : TILE[17];
        } else {
            tile = TILE[blockId];
        }

        float u0 = tile[0] / (float) ATLAS_SIZE;
        float v0 = tile[1] / (float) ATLAS_SIZE;
        float u1 = (tile[0] + 1) / (float) ATLAS_SIZE;
        float v1 = (tile[1] + 1) / (float) ATLAS_SIZE;
        return new float[]{u0, v0, u1, v1};
    }   

    // --- TOP (+Y) ---
    private static void renderTop(World w, int x, int y, int z, float[] uv, int color, SectionMesh mesh) {
        float u0 = uv[0], v0 = uv[1], u1 = uv[2], v1 = uv[3];
        float um = (u0 + u1) * 0.5f;
        float vm = (v0 + v1) * 0.5f;

        int aoA = VulkanSurfaceShader.computeAO(w, x,   y+1, z,   0,1,0,  1,0,0);
        int aoB = VulkanSurfaceShader.computeAO(w, x+1, y+1, z,   0,1,0,  0,0,1);
        int aoC = VulkanSurfaceShader.computeAO(w, x,   y+1, z+1, 0,1,0,  0,0,-1);
        int aoD = VulkanSurfaceShader.computeAO(w, x+1, y+1, z+1, 0,1,0, -1,0,0);
        int aoCenter = (aoA + aoB + aoC + aoD) / 4;

        float cA = AO_LEVELS[aoCenter];
        float aA = AO_LEVELS[aoA], mA = (aA + cA) * 0.5f;
        float aB = AO_LEVELS[aoB], mB = (aB + cA) * 0.5f;
        float aC = AO_LEVELS[aoC], mC = (aC + cA) * 0.5f;
        float aD = AO_LEVELS[aoD], mD = (aD + cA) * 0.5f;

        // A: (x,z) → (x+0.5,z) → center → (x,z+0.5)
        VulkanSurfaceShader.pushQuad(w,
            x, y+1, z,       x+0.5f, y+1, z,       x+0.5f, y+1, z+0.5f,
            x, y+1, z,       x+0.5f, y+1, z+0.5f,  x, y+1, z+0.5f,
            u0, v0,  um, v0,  um, vm,
            u0, v0,  um, vm,  u0, vm,
            color, aA, mA, cA, aA, cA, mA, mesh);

        // B: (x+0.5,z) → (x+1,z) → (x+1,z+0.5) → center
        VulkanSurfaceShader.pushQuad(w,
            x+0.5f, y+1, z,       x+1, y+1, z,       x+1, y+1, z+0.5f,
            x+0.5f, y+1, z,       x+1, y+1, z+0.5f,  x+0.5f, y+1, z+0.5f,
            um, v0,  u1, v0,  u1, vm,
            um, v0,  u1, vm,  um, vm,
            color, aB, mB, cA, aB, cA, mB, mesh);

        // C: (x,z+0.5) → center → (x+0.5,z+1) → (x,z+1)
        VulkanSurfaceShader.pushQuad(w,
            x, y+1, z+0.5f,    x+0.5f, y+1, z+0.5f,  x+0.5f, y+1, z+1,
            x, y+1, z+0.5f,    x+0.5f, y+1, z+1,     x, y+1, z+1,
            u0, vm,  um, vm,  um, v1,
            u0, vm,  um, v1,  u0, v1,
            color, aC, mC, cA, aC, cA, mC, mesh);

        // D: center → (x+1,z+0.5) → (x+1,z+1) → (x+0.5,z+1)
        VulkanSurfaceShader.pushQuad(w,
            x+0.5f, y+1, z+0.5f,  x+1, y+1, z+0.5f,  x+1, y+1, z+1,
            x+0.5f, y+1, z+0.5f,  x+1, y+1, z+1,     x+0.5f, y+1, z+1,
            um, vm,  u1, vm,  u1, v1,
            um, vm,  u1, v1,  um, v1,
            color, aD, mD, cA, aD, cA, mD, mesh);
    }

    // --- BOTTOM (-Y) ---
    private static void renderBottom(World w, int x, int y, int z, float[] uv, int color, SectionMesh mesh) {
        float u0 = uv[0], v0 = uv[1], u1 = uv[2], v1 = uv[3];
        float um = (u0 + u1) * 0.5f;
        float vm = (v0 + v1) * 0.5f;

        int aoA = VulkanSurfaceShader.computeAO(w, x,   y, z,   0,-1,0, -1,0,0);
        int aoB = VulkanSurfaceShader.computeAO(w, x,   y, z+1, 0,-1,0,  0,0,-1);
        int aoC = VulkanSurfaceShader.computeAO(w, x+1, y, z+1, 0,-1,0,  1,0,0);
        int aoD = VulkanSurfaceShader.computeAO(w, x+1, y, z,   0,-1,0,  0,0,1);
        int aoCenter = (aoA + aoB + aoC + aoD) / 4;

        float cA = AO_LEVELS[aoCenter];
        float aA = AO_LEVELS[aoA], mA = (aA + cA) * 0.5f;
        float aB = AO_LEVELS[aoB], mB = (aB + cA) * 0.5f;
        float aC = AO_LEVELS[aoC], mC = (aC + cA) * 0.5f;
        float aD = AO_LEVELS[aoD], mD = (aD + cA) * 0.5f;

        // A: (x,z) → (x+0.5,z) → center → (x,z+0.5)
        VulkanSurfaceShader.pushQuad(w,
            x, y, z,             x+0.5f, y, z,       x+0.5f, y, z+0.5f,
            x, y, z,             x+0.5f, y, z+0.5f,  x, y, z+0.5f,
            u0, v0,  um, v0,  um, vm,
            u0, v0,  um, vm,  u0, vm,
            color, aA, mA, cA, aA, cA, mA, mesh);

        // B: (x,z+0.5) → center → (x+0.5,z+1) → (x,z+1)
        VulkanSurfaceShader.pushQuad(w,
            x, y, z+0.5f,        x+0.5f, y, z+0.5f,  x+0.5f, y, z+1,
            x, y, z+0.5f,        x+0.5f, y, z+1,     x, y, z+1,
            u0, vm,  um, vm,  um, v1,
            u0, vm,  um, v1,  u0, v1,
            color, aB, mB, cA, aB, cA, mB, mesh);

        // C: (x+0.5,z+1) → (x+1,z+1) → (x+1,z+0.5) → center
        VulkanSurfaceShader.pushQuad(w,
            x+0.5f, y, z+1,      x+1, y, z+1,        x+1, y, z+0.5f,
            x+0.5f, y, z+1,      x+1, y, z+0.5f,     x+0.5f, y, z+0.5f,
            um, v1,  u1, v1,  u1, vm,
            um, v1,  u1, vm,  um, vm,
            color, aC, mC, cA, aC, cA, mC, mesh);

        // D: center → (x+1,z+0.5) → (x+1,z) → (x+0.5,z)
        VulkanSurfaceShader.pushQuad(w,
            x+0.5f, y, z+0.5f,   x+1, y, z+0.5f,     x+1, y, z,
            x+0.5f, y, z+0.5f,   x+1, y, z,          x+0.5f, y, z,
            um, vm,  u1, vm,  u1, v0,
            um, vm,  u1, v0,  um, v0,
            color, aD, mD, cA, aD, cA, mD, mesh);
    }   

    // --- NORTH (-Z) ---
    private static void renderNorth(World w, int x, int y, int z, float[] uv, int color, SectionMesh mesh) {
        float u0 = uv[0], v0 = uv[1], u1 = uv[2], v1 = uv[3];
        float um = (u0 + u1) * 0.5f;
        float vm = (v0 + v1) * 0.5f;

        int aoA = VulkanSurfaceShader.computeAO(w, x,   y,   z, 0,0,-1,  1,0,0);
        int aoB = VulkanSurfaceShader.computeAO(w, x,   y+1, z, 0,0,-1,  0,1,0);
        int aoC = VulkanSurfaceShader.computeAO(w, x+1, y+1, z, 0,0,-1, -1,0,0);
        int aoD = VulkanSurfaceShader.computeAO(w, x+1, y,   z, 0,0,-1,  0,-1,0);
        int aoCenter = (aoA + aoB + aoC + aoD) / 4;

        float cA = AO_LEVELS[aoCenter];
        float aA = AO_LEVELS[aoA], mA = (aA + cA) * 0.5f;
        float aB = AO_LEVELS[aoB], mB = (aB + cA) * 0.5f;
        float aC = AO_LEVELS[aoC], mC = (aC + cA) * 0.5f;
        float aD = AO_LEVELS[aoD], mD = (aD + cA) * 0.5f;

        // A: (x,y) → (x+0.5,y) → center → (x,y+0.5)
        VulkanSurfaceShader.pushQuad(w,
            x, y, z,             x+0.5f, y, z,       x+0.5f, y+0.5f, z,
            x, y, z,             x+0.5f, y+0.5f, z,  x, y+0.5f, z,
            u0, v1,  um, v1,  um, vm,
            u0, v1,  um, vm,  u0, vm,
            color, aA, mA, cA, aA, cA, mA, mesh);

        // B: (x+0.5,y+0.5) → (x+0.5,y+1) → (x+1,y+1) → center
        VulkanSurfaceShader.pushQuad(w,
            x+0.5f, y+0.5f, z,   x+0.5f, y+1, z,     x+1, y+1, z,
            x+0.5f, y+0.5f, z,   x+1, y+1, z,        x+1, y+0.5f, z,
            um, vm,  um, v0,  u1, v0,
            um, vm,  u1, v0,  u1, vm,
            color, aB, mB, cA, aB, cA, mB, mesh);

        // C: center → (x+1,y+1) → (x+1,y+0.5) → (x+0.5,y+0.5)
        VulkanSurfaceShader.pushQuad(w,
            x+0.5f, y+0.5f, z,   x+1, y+1, z,        x+1, y+0.5f, z,
            x+0.5f, y+0.5f, z,   x+1, y+0.5f, z,     x+0.5f, y+0.5f, z,
            um, vm,  u1, v0,  u1, vm,
            um, vm,  u1, vm,  um, vm,
            color, aC, mC, cA, aC, cA, mC, mesh);

        // D: (x+0.5,y) → center → (x+1,y+0.5) → (x+1,y)
        VulkanSurfaceShader.pushQuad(w,
            x+0.5f, y, z,        x+0.5f, y+0.5f, z,  x+1, y+0.5f, z,
            x+0.5f, y, z,        x+1, y+0.5f, z,     x+1, y, z,
            um, v1,  um, vm,  u1, vm,
            um, v1,  u1, vm,  u1, v1,
            color, aD, mD, cA, aD, cA, mD, mesh);
    }

    // --- SOUTH (+Z) ---
    private static void renderSouth(World w, int x, int y, int z, float[] uv, int color, SectionMesh mesh) {
        float u0 = uv[0], v0 = uv[1], u1 = uv[2], v1 = uv[3];
        float um = (u0 + u1) * 0.5f;
        float vm = (v0 + v1) * 0.5f;

        int aoA = VulkanSurfaceShader.computeAO(w, x+1, y,   z+1, 0,0,1, -1,0,0);
        int aoB = VulkanSurfaceShader.computeAO(w, x+1, y+1, z+1, 0,0,1,  0,1,0);
        int aoC = VulkanSurfaceShader.computeAO(w, x,   y+1, z+1, 0,0,1,  1,0,0);
        int aoD = VulkanSurfaceShader.computeAO(w, x,   y,   z+1, 0,0,1,  0,-1,0);
        int aoCenter = (aoA + aoB + aoC + aoD) / 4;

        float cA = AO_LEVELS[aoCenter];
        float aA = AO_LEVELS[aoA], mA = (aA + cA) * 0.5f;
        float aB = AO_LEVELS[aoB], mB = (aB + cA) * 0.5f;
        float aC = AO_LEVELS[aoC], mC = (aC + cA) * 0.5f;
        float aD = AO_LEVELS[aoD], mD = (aD + cA) * 0.5f;

        // A: (x+1,y) → (x+0.5,y) → center → (x+1,y+0.5)
        VulkanSurfaceShader.pushQuad(w,
            x+1, y, z+1,          x+0.5f, y, z+1,     x+0.5f, y+0.5f, z+1,
            x+1, y, z+1,          x+0.5f, y+0.5f, z+1, x+1, y+0.5f, z+1,
            u0, v1,  um, v1,  um, vm,
            u0, v1,  um, vm,  u0, vm,
            color, aA, mA, cA, aA, cA, mA, mesh);

        // B: (x+0.5,y+0.5) → (x+0.5,y+1) → (x,y+1) → center
        VulkanSurfaceShader.pushQuad(w,
            x+0.5f, y+0.5f, z+1,  x+0.5f, y+1, z+1,   x, y+1, z+1,
            x+0.5f, y+0.5f, z+1,  x, y+1, z+1,        x, y+0.5f, z+1,
            um, vm,  um, v0,  u1, v0,
            um, vm,  u1, v0,  u1, vm,
            color, aB, mB, cA, aB, cA, mB, mesh);

        // C: center → (x,y+1) → (x,y+0.5) → (x+0.5,y+0.5)
        VulkanSurfaceShader.pushQuad(w,
            x+0.5f, y+0.5f, z+1,  x, y+1, z+1,        x, y+0.5f, z+1,
            x+0.5f, y+0.5f, z+1,  x, y+0.5f, z+1,     x+0.5f, y+0.5f, z+1,
            um, vm,  u1, v0,  u1, vm,
            um, vm,  u1, vm,  um, vm,
            color, aC, mC, cA, aC, cA, mC, mesh);

        // D: (x+1,y+0.5) → center → (x,y+0.5) → (x+1,y)
        VulkanSurfaceShader.pushQuad(w,
            x+1, y+0.5f, z+1,     x+0.5f, y+0.5f, z+1, x, y+0.5f, z+1,
            x+1, y+0.5f, z+1,     x, y+0.5f, z+1,      x+1, y, z+1,
            u0, vm,  um, vm,  u1, vm,
            u0, vm,  u1, vm,  u1, v1,
            color, aD, mD, cA, aD, cA, mD, mesh);
    }   

    // --- WEST (-X) ---
    private static void renderWest(World w, int x, int y, int z, float[] uv, int color, SectionMesh mesh) {
        float u0 = uv[0], v0 = uv[1], u1 = uv[2], v1 = uv[3];
        float um = (u0 + u1) * 0.5f;
        float vm = (v0 + v1) * 0.5f;

        int aoA = VulkanSurfaceShader.computeAO(w, x, y,   z,   -1,0,0, 0,0,1);
        int aoB = VulkanSurfaceShader.computeAO(w, x, y,   z+1, -1,0,0, 0,1,0);
        int aoC = VulkanSurfaceShader.computeAO(w, x, y+1, z+1, -1,0,0, 0,0,-1);
        int aoD = VulkanSurfaceShader.computeAO(w, x, y+1, z,   -1,0,0, 0,-1,0);
        int aoCenter = (aoA + aoB + aoC + aoD) / 4;

        float cA = AO_LEVELS[aoCenter];
        float aA = AO_LEVELS[aoA], mA = (aA + cA) * 0.5f;
        float aB = AO_LEVELS[aoB], mB = (aB + cA) * 0.5f;
        float aC = AO_LEVELS[aoC], mC = (aC + cA) * 0.5f;
        float aD = AO_LEVELS[aoD], mD = (aD + cA) * 0.5f;

        // A: (z,y) → (z+0.5,y) → center → (z,y+0.5)
        VulkanSurfaceShader.pushQuad(w,
            x, y, z,             x, y, z+0.5f,       x, y+0.5f, z+0.5f,
            x, y, z,             x, y+0.5f, z+0.5f,  x, y+0.5f, z,
            u0, v1,  um, v1,  um, vm,
            u0, v1,  um, vm,  u0, vm,
            color, aA, mA, cA, aA, cA, mA, mesh);

        // B: (z+0.5,y+0.5) → (z+0.5,y+1) → (z+1,y+1) → center
        VulkanSurfaceShader.pushQuad(w,
            x, y+0.5f, z+0.5f,   x, y+1, z+0.5f,     x, y+1, z+1,
            x, y+0.5f, z+0.5f,   x, y+1, z+1,        x, y+0.5f, z+1,
            um, vm,  um, v0,  u1, v0,
            um, vm,  u1, v0,  u1, vm,
            color, aB, mB, cA, aB, cA, mB, mesh);

        // C: center → (z+1,y+1) → (z+1,y+0.5) → (z+0.5,y+0.5)
        VulkanSurfaceShader.pushQuad(w,
            x, y+0.5f, z+0.5f,   x, y+1, z+1,        x, y+0.5f, z+1,
            x, y+0.5f, z+0.5f,   x, y+0.5f, z+1,     x, y+0.5f, z+0.5f,
            um, vm,  u1, v0,  u1, vm,
            um, vm,  u1, vm,  um, vm,
            color, aC, mC, cA, aC, cA, mC, mesh);

        // D: (z+0.5,y) → center → (z+1,y+0.5) → (z+1,y)
        VulkanSurfaceShader.pushQuad(w,
            x, y, z+0.5f,        x, y+0.5f, z+0.5f,  x, y+0.5f, z+1,
            x, y, z+0.5f,        x, y+0.5f, z+1,     x, y, z+1,
            um, v1,  um, vm,  u1, vm,
            um, v1,  u1, vm,  u1, v1,
            color, aD, mD, cA, aD, cA, mD, mesh);
    }

    // --- EAST (+X) ---
    private static void renderEast(World w, int x, int y, int z, float[] uv, int color, SectionMesh mesh) {
        float u0 = uv[0], v0 = uv[1], u1 = uv[2], v1 = uv[3];
        float um = (u0 + u1) * 0.5f;
        float vm = (v0 + v1) * 0.5f;

        int aoA = VulkanSurfaceShader.computeAO(w, x+1, y,   z+1, 1,0,0, 0,0,-1);
        int aoB = VulkanSurfaceShader.computeAO(w, x+1, y,   z,   1,0,0, 0,1,0);
        int aoC = VulkanSurfaceShader.computeAO(w, x+1, y+1, z,   1,0,0, 0,0,1);
        int aoD = VulkanSurfaceShader.computeAO(w, x+1, y+1, z+1, 1,0,0, 0,-1,0);
        int aoCenter = (aoA + aoB + aoC + aoD) / 4;

        float cA = AO_LEVELS[aoCenter];
        float aA = AO_LEVELS[aoA], mA = (aA + cA) * 0.5f;
        float aB = AO_LEVELS[aoB], mB = (aB + cA) * 0.5f;
        float aC = AO_LEVELS[aoC], mC = (aC + cA) * 0.5f;
        float aD = AO_LEVELS[aoD], mD = (aD + cA) * 0.5f;

        // A: (z+1,y) → (z+0.5,y) → center → (z+1,y+0.5)
        VulkanSurfaceShader.pushQuad(w,
            x+1, y, z+1,          x+1, y, z+0.5f,     x+1, y+0.5f, z+0.5f,
            x+1, y, z+1,          x+1, y+0.5f, z+0.5f, x+1, y+0.5f, z+1,
            u0, v1,  um, v1,  um, vm,
            u0, v1,  um, vm,  u0, vm,
            color, aA, mA, cA, aA, cA, mA, mesh);

        // B: (z+0.5,y+0.5) → (z+0.5,y+1) → (z,y+1) → center
        VulkanSurfaceShader.pushQuad(w,
            x+1, y+0.5f, z+0.5f,  x+1, y+1, z+0.5f,   x+1, y+1, z,
            x+1, y+0.5f, z+0.5f,  x+1, y+1, z,        x+1, y+0.5f, z,
            um, vm,  um, v0,  u1, v0,
            um, vm,  u1, v0,  u1, vm,
            color, aB, mB, cA, aB, cA, mB, mesh);

        // C: center → (z,y+1) → (z,y+0.5) → (z+0.5,y+0.5)
        VulkanSurfaceShader.pushQuad(w,
            x+1, y+0.5f, z+0.5f,  x+1, y+1, z,        x+1, y+0.5f, z,
            x+1, y+0.5f, z+0.5f,  x+1, y+0.5f, z,     x+1, y+0.5f, z+0.5f,
            um, vm,  u1, v0,  u1, vm,
            um, vm,  u1, vm,  um, vm,
            color, aC, mC, cA, aC, cA, mC, mesh);

        // D: (z+1,y+0.5) → center → (z+0.5,y+0.5) → (z+1,y)
        VulkanSurfaceShader.pushQuad(w,
            x+1, y+0.5f, z+1,     x+1, y+0.5f, z+0.5f, x+1, y+0.5f, z+0.5f,
            x+1, y+0.5f, z+1,     x+1, y+0.5f, z+0.5f, x+1, y, z+1,
            u0, vm,  um, vm,  um, vm,
            u0, vm,  um, vm,  u1, v1,
            color, aD, mD, cA, aD, cA, mD, mesh);
    }   

        // ==================== LIQUID (quarter-face AO) ====================

    private static void renderLiquid(World w, int x, int y, int z, int blockId, SectionMesh mesh) {
        int meta = w.getBlockMetadata(x, y, z);
        boolean isWater = (blockId == 8 || blockId == 9);
        boolean isFlowing = (blockId == 8 || blockId == 10);

        float height;
        if (isFlowing) {
            height = (meta + 1) / 16.0f;
        } else {
            height = 15.0f / 16.0f;
        }

        float topY = y + height;
        int color = isWater ? 0x3333FF : 0xFF4400;
        float alpha = isWater ? 0.7f : 0.5f;
        float[] uv = getUV(blockId, 0);

        int above = (y + 1 < 128) ? w.getBlock(x, y + 1, z) : 0;
        if (above != blockId)
            renderLiquidTop(w, x, y, topY, z, uv, color, alpha, mesh);

        int n = (z - 1 >= 0) ? w.getBlock(x, y, z - 1) : 0;
        if (n != blockId)
            renderLiquidSide(w, x, y, topY, z, 0, uv, color, alpha, mesh);

        int s = w.getBlock(x, y, z + 1);
        if (s != blockId)
            renderLiquidSide(w, x, y, topY, z, 1, uv, color, alpha, mesh);

        int wst = w.getBlock(x - 1, y, z);
        if (wst != blockId)
            renderLiquidSide(w, x, y, topY, z, 2, uv, color, alpha, mesh);

        int e = w.getBlock(x + 1, y, z);
        if (e != blockId)
            renderLiquidSide(w, x, y, topY, z, 3, uv, color, alpha, mesh);
    }

    private static void renderLiquidTop(World w, int x, int y, float topY, int z,
                                        float[] uv, int color, float alpha, SectionMesh mesh) {
        float u0 = uv[0], v0 = uv[1], u1 = uv[2], v1 = uv[3];
        float um = (u0 + u1) * 0.5f;
        float vm = (v0 + v1) * 0.5f;

        int aoA = VulkanSurfaceShader.computeAO(w, x,   (int)topY, z,   0,1,0,  1,0,0);
        int aoB = VulkanSurfaceShader.computeAO(w, x+1, (int)topY, z,   0,1,0,  0,0,1);
        int aoC = VulkanSurfaceShader.computeAO(w, x,   (int)topY, z+1, 0,1,0,  0,0,-1);
        int aoD = VulkanSurfaceShader.computeAO(w, x+1, (int)topY, z+1, 0,1,0, -1,0,0);
        int aoCenter = (aoA + aoB + aoC + aoD) / 4;

        float cA = AO_LEVELS[aoCenter];
        float aA = AO_LEVELS[aoA], mA = (aA + cA) * 0.5f;
        float aB = AO_LEVELS[aoB], mB = (aB + cA) * 0.5f;
        float aC = AO_LEVELS[aoC], mC = (aC + cA) * 0.5f;
        float aD = AO_LEVELS[aoD], mD = (aD + cA) * 0.5f;

        VulkanLiquidShader.pushQuad(w,
            x, topY, z,       x+0.5f, topY, z,       x+0.5f, topY, z+0.5f,
            x, topY, z,       x+0.5f, topY, z+0.5f,  x, topY, z+0.5f,
            u0, v0,  um, v0,  um, vm,
            u0, v0,  um, vm,  u0, vm,
            color, aA, mA, cA, aA, cA, mA, alpha, mesh);

        VulkanLiquidShader.pushQuad(w,
            x+0.5f, topY, z,       x+1, topY, z,       x+1, topY, z+0.5f,
            x+0.5f, topY, z,       x+1, topY, z+0.5f,  x+0.5f, topY, z+0.5f,
            um, v0,  u1, v0,  u1, vm,
            um, v0,  u1, vm,  um, vm,
            color, aB, mB, cA, aB, cA, mB, alpha, mesh);

        VulkanLiquidShader.pushQuad(w,
            x, topY, z+0.5f,    x+0.5f, topY, z+0.5f,  x+0.5f, topY, z+1,
            x, topY, z+0.5f,    x+0.5f, topY, z+1,     x, topY, z+1,
            u0, vm,  um, vm,  um, v1,
            u0, vm,  um, v1,  u0, v1,
            color, aC, mC, cA, aC, cA, mC, alpha, mesh);

        VulkanLiquidShader.pushQuad(w,
            x+0.5f, topY, z+0.5f,  x+1, topY, z+0.5f,  x+1, topY, z+1,
            x+0.5f, topY, z+0.5f,  x+1, topY, z+1,     x+0.5f, topY, z+1,
            um, vm,  u1, vm,  u1, v1,
            um, vm,  u1, v1,  um, v1,
            color, aD, mD, cA, aD, cA, mD, alpha, mesh);
    }

    private static void renderLiquidSide(World w, int x, int y, float topY, int z,
                                        int side, float[] uv, int color, float alpha, SectionMesh mesh) {
        float u0 = uv[0], v0 = uv[1], u1 = uv[2], v1 = uv[3];
        float um = (u0 + u1) * 0.5f;
        float vm = (v0 + v1) * 0.5f;
        float midY = (y + topY) * 0.5f;

        switch (side) {
            case 0: // North (-Z)
            {
                int aoA = VulkanSurfaceShader.computeAO(w, x,   y,   z, 0,0,-1,  1,0,0);
                int aoB = VulkanSurfaceShader.computeAO(w, x,   y+1, z, 0,0,-1,  0,1,0);
                int aoC = VulkanSurfaceShader.computeAO(w, x+1, y+1, z, 0,0,-1, -1,0,0);
                int aoD = VulkanSurfaceShader.computeAO(w, x+1, y,   z, 0,0,-1,  0,-1,0);
                int aoCenter = (aoA + aoB + aoC + aoD) / 4;

                float cA = AO_LEVELS[aoCenter];
                float aA = AO_LEVELS[aoA], mA = (aA + cA) * 0.5f;
                float aB = AO_LEVELS[aoB], mB = (aB + cA) * 0.5f;
                float aC = AO_LEVELS[aoC], mC = (aC + cA) * 0.5f;
                float aD = AO_LEVELS[aoD], mD = (aD + cA) * 0.5f;

                VulkanLiquidShader.pushQuad(w,
                    x, y, z,             x+0.5f, y, z,       x+0.5f, midY, z,
                    x, y, z,             x+0.5f, midY, z,    x, midY, z,
                    u0, v1,  um, v1,  um, vm,
                    u0, v1,  um, vm,  u0, vm,
                    color, aA, mA, cA, aA, cA, mA, alpha, mesh);

                VulkanLiquidShader.pushQuad(w,
                    x+0.5f, midY, z,     x+0.5f, topY, z,    x+1, topY, z,
                    x+0.5f, midY, z,     x+1, topY, z,       x+1, midY, z,
                    um, vm,  um, v0,  u1, v0,
                    um, vm,  u1, v0,  u1, vm,
                    color, aB, mB, cA, aB, cA, mB, alpha, mesh);

                VulkanLiquidShader.pushQuad(w,
                    x+0.5f, midY, z,     x+1, topY, z,       x+1, midY, z,
                    x+0.5f, midY, z,     x+1, midY, z,       x+0.5f, midY, z,
                    um, vm,  u1, v0,  u1, vm,
                    um, vm,  u1, vm,  um, vm,
                    color, aC, mC, cA, aC, cA, mC, alpha, mesh);

                VulkanLiquidShader.pushQuad(w,
                    x+0.5f, y, z,        x+0.5f, midY, z,    x+1, midY, z,
                    x+0.5f, y, z,        x+1, midY, z,       x+1, y, z,
                    um, v1,  um, vm,  u1, vm,
                    um, v1,  u1, vm,  u1, v1,
                    color, aD, mD, cA, aD, cA, mD, alpha, mesh);
                break;
            }
            case 1: // South (+Z)
            {
                int aoA = VulkanSurfaceShader.computeAO(w, x+1, y,   z+1, 0,0,1, -1,0,0);
                int aoB = VulkanSurfaceShader.computeAO(w, x+1, y+1, z+1, 0,0,1,  0,1,0);
                int aoC = VulkanSurfaceShader.computeAO(w, x,   y+1, z+1, 0,0,1,  1,0,0);
                int aoD = VulkanSurfaceShader.computeAO(w, x,   y,   z+1, 0,0,1,  0,-1,0);
                int aoCenter = (aoA + aoB + aoC + aoD) / 4;

                float cA = AO_LEVELS[aoCenter];
                float aA = AO_LEVELS[aoA], mA = (aA + cA) * 0.5f;
                float aB = AO_LEVELS[aoB], mB = (aB + cA) * 0.5f;
                float aC = AO_LEVELS[aoC], mC = (aC + cA) * 0.5f;
                float aD = AO_LEVELS[aoD], mD = (aD + cA) * 0.5f;

                VulkanLiquidShader.pushQuad(w,
                    x+1, y, z+1,          x+0.5f, y, z+1,     x+0.5f, midY, z+1,
                    x+1, y, z+1,          x+0.5f, midY, z+1,  x+1, midY, z+1,
                    u0, v1,  um, v1,  um, vm,
                    u0, v1,  um, vm,  u0, vm,
                    color, aA, mA, cA, aA, cA, mA, alpha, mesh);

                VulkanLiquidShader.pushQuad(w,
                    x+0.5f, midY, z+1,    x+0.5f, topY, z+1,  x, topY, z+1,
                    x+0.5f, midY, z+1,    x, topY, z+1,       x, midY, z+1,
                    um, vm,  um, v0,  u1, v0,
                    um, vm,  u1, v0,  u1, vm,
                    color, aB, mB, cA, aB, cA, mB, alpha, mesh);

                VulkanLiquidShader.pushQuad(w,
                    x+0.5f, midY, z+1,    x, topY, z+1,       x, midY, z+1,
                    x+0.5f, midY, z+1,    x, midY, z+1,       x+0.5f, midY, z+1,
                    um, vm,  u1, v0,  u1, vm,
                    um, vm,  u1, vm,  um, vm,
                    color, aC, mC, cA, aC, cA, mC, alpha, mesh);

                VulkanLiquidShader.pushQuad(w,
                    x+1, midY, z+1,       x+0.5f, midY, z+1,  x, midY, z+1,
                    x+1, midY, z+1,       x, midY, z+1,       x+1, y, z+1,
                    u0, vm,  um, vm,  u1, vm,
                    u0, vm,  u1, vm,  u1, v1,
                    color, aD, mD, cA, aD, cA, mD, alpha, mesh);
                break;
            }
            case 2: // West (-X)
            {
                int aoA = VulkanSurfaceShader.computeAO(w, x, y,   z,   -1,0,0, 0,0,1);
                int aoB = VulkanSurfaceShader.computeAO(w, x, y,   z+1, -1,0,0, 0,1,0);
                int aoC = VulkanSurfaceShader.computeAO(w, x, y+1, z+1, -1,0,0, 0,0,-1);
                int aoD = VulkanSurfaceShader.computeAO(w, x, y+1, z,   -1,0,0, 0,-1,0);
                int aoCenter = (aoA + aoB + aoC + aoD) / 4;

                float cA = AO_LEVELS[aoCenter];
                float aA = AO_LEVELS[aoA], mA = (aA + cA) * 0.5f;
                float aB = AO_LEVELS[aoB], mB = (aB + cA) * 0.5f;
                float aC = AO_LEVELS[aoC], mC = (aC + cA) * 0.5f;
                float aD = AO_LEVELS[aoD], mD = (aD + cA) * 0.5f;

                VulkanLiquidShader.pushQuad(w,
                    x, y, z,             x, y, z+0.5f,       x, midY, z+0.5f,
                    x, y, z,             x, midY, z+0.5f,    x, midY, z,
                    u0, v1,  um, v1,  um, vm,
                    u0, v1,  um, vm,  u0, vm,
                    color, aA, mA, cA, aA, cA, mA, alpha, mesh);

                VulkanLiquidShader.pushQuad(w,
                    x, midY, z+0.5f,     x, topY, z+0.5f,    x, topY, z+1,
                    x, midY, z+0.5f,     x, topY, z+1,       x, midY, z+1,
                    um, vm,  um, v0,  u1, v0,
                    um, vm,  u1, v0,  u1, vm,
                    color, aB, mB, cA, aB, cA, mB, alpha, mesh);

                VulkanLiquidShader.pushQuad(w,
                    x, midY, z+0.5f,     x, topY, z+1,       x, midY, z+1,
                    x, midY, z+0.5f,     x, midY, z+1,       x, midY, z+0.5f,
                    um, vm,  u1, v0,  u1, vm,
                    um, vm,  u1, vm,  um, vm,
                    color, aC, mC, cA, aC, cA, mC, alpha, mesh);

                VulkanLiquidShader.pushQuad(w,
                    x, y, z+0.5f,        x, midY, z+0.5f,    x, midY, z+1,
                    x, y, z+0.5f,        x, midY, z+1,       x, y, z+1,
                    um, v1,  um, vm,  u1, vm,
                    um, v1,  u1, vm,  u1, v1,
                    color, aD, mD, cA, aD, cA, mD, alpha, mesh);
                break;
            }
            case 3: // East (+X)
            {
                int aoA = VulkanSurfaceShader.computeAO(w, x+1, y,   z+1, 1,0,0, 0,0,-1);
                int aoB = VulkanSurfaceShader.computeAO(w, x+1, y,   z,   1,0,0, 0,1,0);
                int aoC = VulkanSurfaceShader.computeAO(w, x+1, y+1, z,   1,0,0, 0,0,1);
                int aoD = VulkanSurfaceShader.computeAO(w, x+1, y+1, z+1, 1,0,0, 0,-1,0);
                int aoCenter = (aoA + aoB + aoC + aoD) / 4;

                float cA = AO_LEVELS[aoCenter];
                float aA = AO_LEVELS[aoA], mA = (aA + cA) * 0.5f;
                float aB = AO_LEVELS[aoB], mB = (aB + cA) * 0.5f;
                float aC = AO_LEVELS[aoC], mC = (aC + cA) * 0.5f;
                float aD = AO_LEVELS[aoD], mD = (aD + cA) * 0.5f;

                VulkanLiquidShader.pushQuad(w,
                    x+1, y, z+1,          x+1, y, z+0.5f,     x+1, midY, z+0.5f,
                    x+1, y, z+1,          x+1, midY, z+0.5f,  x+1, midY, z+1,
                    u0, v1,  um, v1,  um, vm,
                    u0, v1,  um, vm,  u0, vm,
                    color, aA, mA, cA, aA, cA, mA, alpha, mesh);

                VulkanLiquidShader.pushQuad(w,
                    x+1, midY, z+0.5f,    x+1, topY, z+0.5f,  x+1, topY, z,
                    x+1, midY, z+0.5f,    x+1, topY, z,       x+1, midY, z,
                    um, vm,  um, v0,  u1, v0,
                    um, vm,  u1, v0,  u1, vm,
                    color, aB, mB, cA, aB, cA, mB, alpha, mesh);

                VulkanLiquidShader.pushQuad(w,
                    x+1, midY, z+0.5f,    x+1, topY, z,       x+1, midY, z,
                    x+1, midY, z+0.5f,    x+1, midY, z,       x+1, midY, z+0.5f,
                    um, vm,  u1, v0,  u1, vm,
                    um, vm,  u1, vm,  um, vm,
                    color, aC, mC, cA, aC, cA, mC, alpha, mesh);

                VulkanLiquidShader.pushQuad(w,
                    x+1, midY, z+1,       x+1, midY, z+0.5f,  x+1, midY, z+0.5f,
                    x+1, midY, z+1,       x+1, midY, z+0.5f,  x+1, y, z+1,
                    u0, vm,  um, vm,  um, vm,
                    u0, vm,  um, vm,  u1, v1,
                    color, aD, mD, cA, aD, cA, mD, alpha, mesh);
                break;
            }
        }
    }
}   