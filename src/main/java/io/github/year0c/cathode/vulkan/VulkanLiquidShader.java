package io.github.year0c.cathode.vulkan;

import net.minecraft.world.World;
import net.minecraft.world.LightType;
import net.minecraft.block.Block;
import io.github.year0c.cathode.vulkan.util.VulkanLightContextUtil;
import io.github.year0c.cathode.vulkan.util.RenderLoopHolderUtil;

public class VulkanLiquidShader {

    public static void push(World w, float x, float y, float z, float u, float v, int color) {
        int bx = (int) Math.floor(x);
        int by = (int) Math.floor(y);
        int bz = (int) Math.floor(z);

        // Water gets reduced sky light (light doesn't penetrate water well)
        int sky = computeSkyLight(w, bx, by, bz);
        int blockLight = w.getLight(LightType.BLOCK, bx, by, bz);
        VulkanLightContextUtil.setLight(
            (sky + 0.5f) / 16.0f,
            (blockLight + 0.5f) / 16.0f
        );

        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = ((color >> 24) & 0xFF) / 255.0f;

        RenderLoopHolderUtil.get().pushLiquidVertex(x, y, z, r, g, b, a, u, v,
            VulkanLightContextUtil.getLightU(), VulkanLightContextUtil.getLightV());
    }

    // Same raycast as ground — duplicate for isolation, or share later if needed
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