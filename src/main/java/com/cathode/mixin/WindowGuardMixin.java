package com.cathode.mixin;

import org.lwjglx.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Window;

@Mixin(Window.class)
public class WindowGuardMixin {

    @Inject(method = "run", at = @At("HEAD"), cancellable = true)
    private void cathode$killNewWindow(CallbackInfo ci) {
        if (Display.isAwTLocked()) {
            StackTraceElement caller = new Exception().getStackTrace()[1];
            throw new RuntimeException(
                "[Cathode] AWT window blocked after SDL takeover.\n" +
                "  Caller: " + caller
            );
        }
    }
}   