package io.github.year0c.cathode.mixin;

import org.lwjgl.opengl.GL11Shadow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.tomgirl.pylon.window.DisplaySdl;
import pl.tomgirl.pylon.window.GpuSurface;

@Mixin(DisplaySdl.class)
public class DisplaySdlMixin {

    @Inject(method = "create", at = @At("TAIL"))
    private void cathode$initVulkan(GpuSurface fallbackSurface, CallbackInfo ci) {
        DisplaySdl self = (DisplaySdl)(Object)this;
        if (GL11Shadow.isInitialized()) return;
        try {
            GL11Shadow.initVulkan(self.getHandle(), self.getWidth(), self.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}   