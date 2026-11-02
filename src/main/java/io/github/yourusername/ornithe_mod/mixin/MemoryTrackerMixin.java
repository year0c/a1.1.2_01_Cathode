package io.github.yourusername.ornithe_mod.mixin;

import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.render.platform.MemoryTracker")
public abstract class MemoryTrackerMixin {
    @Redirect(
        method = "genTextures",
        at = @At(
            value = "INVOKE",
            target = "Lorg/lwjgl/opengl/GL11;glGenTextures(Ljava/nio/IntBuffer;)V"
        )
    )
    private static void redirectGlGenTextures(IntBuffer textures) {
        GL11C.glGenTextures(textures);
    }
}
