package io.github.year0c.cathode.mixin;

import net.minecraft.client.render.vertex.Tesselator;
import io.github.year0c.cathode.vulkan.VulkanLightContext;
import io.github.year0c.cathode.vulkan.RenderLoopHolder;

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
        if (RenderLoopHolder.get() == null) return;
        int c = this.color;
        float r = ((c >> 16) & 0xFF) / 255.0f;
        float g = ((c >> 8) & 0xFF) / 255.0f;
        float b = (c & 0xFF) / 255.0f;
        float a = ((c >> 24) & 0xFF) / 255.0f;

        RenderLoopHolder.get().pushVertex(
            (float) x, (float) y, (float) z,
            r, g, b, a,
            (float) u, (float) v,
            VulkanLightContext.getLightU(),
            VulkanLightContext.getLightV()
        );
        ci.cancel();
    }
}