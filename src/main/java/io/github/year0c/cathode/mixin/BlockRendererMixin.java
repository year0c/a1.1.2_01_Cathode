package io.github.year0c.cathode.mixin;

import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.block.Block;
import io.github.year0c.cathode.vulkan.util.VulkanLightContextUtil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin {

    @Inject(method = "tesselateLiquid", at = @At("HEAD"))
    private void cathode$liquidStart(Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> ci) {
        VulkanLightContextUtil.setLiquid(true);
    }

    @Inject(method = "tesselateLiquid", at = @At("RETURN"))
    private void cathode$liquidEnd(Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> ci) {
        VulkanLightContextUtil.setLiquid(false);
    }
}   