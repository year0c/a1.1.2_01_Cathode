package io.github.year0c.cathode.mixin;

import net.minecraft.client.render.platform.MemoryTracker;
import org.lwjgl.opengl.util.GL11.GL11State;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.IntBuffer;

@Mixin(MemoryTracker.class)
public class MemoryTrackerMixin {

    @Inject(method = "genTextures", at = @At("HEAD"), cancellable = true)
    private static void cathode$genTextures(IntBuffer ids, CallbackInfo ci) {
        ids.put(GL11State.nextTextureId++);
        ci.cancel();
    }
}   