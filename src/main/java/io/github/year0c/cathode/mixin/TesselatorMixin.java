package io.github.year0c.cathode.mixin;

import net.minecraft.client.render.vertex.Tesselator;
import net.minecraft.world.World;
import net.minecraft.block.Block;

import io.github.year0c.cathode.vulkan.VulkanSurfaceShader;
import io.github.year0c.cathode.vulkan.VulkanLiquidShader;
import io.github.year0c.cathode.vulkan.util.RenderLoopHolderUtil;
import io.github.year0c.cathode.vulkan.util.WorldHolderUtil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Tesselator.class)
public abstract class TesselatorMixin {

    @Shadow
    private int color;

    @Inject(method = "vertex(DDDDD)V", at = @At("HEAD"), cancellable = true)
    private void cathode$vertex(double x, double y, double z, double u, double v, CallbackInfo ci) {
        if (RenderLoopHolderUtil.get() == null) return;
        World w = WorldHolderUtil.get();
        if (w == null) return;

        int bx = (int) Math.floor(x);
        int by = (int) Math.floor(y);
        int bz = (int) Math.floor(z);
        int block = w.getBlock(bx, by, bz);
        boolean isLiquid = block >= 0 && block < Block.BY_ID.length
            && Block.BY_ID[block] != null
            && Block.BY_ID[block].material.isLiquid();

        if (isLiquid) {
            VulkanLiquidShader.push(w, (float) x, (float) y, (float) z, (float) u, (float) v, this.color);
        } else {
            VulkanSurfaceShader.push(w, (float) x, (float) y, (float) z, (float) u, (float) v, this.color);
        }
        ci.cancel();
    }
}   