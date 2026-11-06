package io.github.year0c.cathode.mixin;

import net.minecraft.client.render.texture.TextureManager;
import org.lwjgl.opengl.func.GL11.GL11TextureFunc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.IntBuffer;

@Mixin(TextureManager.class)
public class TMReleaseMixin {

    @Redirect(method = "release", at = @At(value = "INVOKE",
        target = "Lorg/lwjgl/opengl/GL11;glDeleteTextures(Ljava/nio/IntBuffer;)V"))
    private void cathode$glDeleteTextures(IntBuffer ids) {
        while (ids.hasRemaining()) {
            GL11TextureFunc.deleteTexture(ids.get());
        }
    }
}   