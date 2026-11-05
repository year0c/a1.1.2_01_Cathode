package io.github.year0c.cathode.mixin;

import net.minecraft.client.render.vertex.Tesselator;
import net.minecraft.world.World;
import io.github.year0c.cathode.vulkan.VulkanShader;
import io.github.year0c.cathode.vulkan.util.RenderLoopHolderUtil;
import io.github.year0c.cathode.vulkan.util.VulkanLightContextUtil;
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

        VulkanShader.compute(w, (int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));

        int c = this.color;
        float r = ((c >> 16) & 0xFF) / 255.0f;
        float g = ((c >> 8) & 0xFF) / 255.0f;
        float b = (c & 0xFF) / 255.0f;
        float a = ((c >> 24) & 0xFF) / 255.0f;

        if (VulkanLightContextUtil.isLiquid()) {
            RenderLoopHolderUtil.get().pushLiquidVertex(
                (float) x, (float) y, (float) z,
                r, g, b, a,
                (float) u, (float) v,
                VulkanLightContextUtil.getLightU(),
                VulkanLightContextUtil.getLightV()
            );
        } else {
            RenderLoopHolderUtil.get().pushVertex(
                (float) x, (float) y, (float) z,
                r, g, b, a,
                (float) u, (float) v,
                VulkanLightContextUtil.getLightU(),
                VulkanLightContextUtil.getLightV()
            );
        }
        ci.cancel();
    }
}   