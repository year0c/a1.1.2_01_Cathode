package io.github.year0c.cathode.mixin;

import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.block.Block;
import net.minecraft.world.WorldView;
import io.github.year0c.cathode.probe.SkyProbe;
import io.github.year0c.cathode.vulkan.VulkanLightContext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin {

    @Redirect(method = "*", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/block/Block;getBrightness(Lnet/minecraft/world/WorldView;III)F"
    ))
    private float cathode$stripBrightness(Block block, WorldView world, int x, int y, int z) {
        float brightness = world.getBrightness(x, y, z);
        VulkanLightContext.setBrightness(brightness);
        float skyLevel = SkyProbe.getInstance().getSkyLightLevel();
        VulkanLightContext.setLight(
            (skyLevel + 0.5f) / 16.0f,
            (brightness * 15.0f + 0.5f) / 16.0f
        );
        return 1.0f;
    }
}