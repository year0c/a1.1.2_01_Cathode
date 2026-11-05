package io.github.year0c.cathode.vulkan;

import net.minecraft.world.World;
import net.minecraft.world.LightType;
import io.github.year0c.cathode.vulkan.util.VulkanLightContextUtil;
import net.minecraft.block.Block;

public class VulkanShader {

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