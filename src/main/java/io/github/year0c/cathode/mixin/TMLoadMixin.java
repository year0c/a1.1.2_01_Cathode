package io.github.year0c.cathode.mixin;

import net.minecraft.client.render.texture.TextureManager;
import org.lwjgl.opengl.util.GL11.GL11State;
import org.lwjgl.opengl.func.GL11.GL11TextureFunc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.ByteBuffer;

@Mixin(TextureManager.class)
public class TMLoadMixin {

    @Redirect(method = {"load", "bind", "tick"}, at = @At(value = "INVOKE",
        target = "Lorg/lwjgl/opengl/GL11;glBindTexture(II)V"))
    private void cathode$glBindTexture(int target, int texture) {
        GL11State.boundTexture = texture;
    }

    @Redirect(method = "load", at = @At(value = "INVOKE",
        target = "Lorg/lwjgl/opengl/GL11;glTexParameteri(III)V"))
    private void cathode$glTexParameteri(int target, int pname, int param) {
        if (pname < GL11State.textureParams.length) GL11State.textureParams[pname] = param;
    }

    @Redirect(method = "load", at = @At(value = "INVOKE",
        target = "Lorg/lwjgl/opengl/GL11;glTexImage2D(IIIIIIILjava/nio/ByteBuffer;)V"))
    private void cathode$glTexImage2D(int target, int level, int internalformat,
                                      int width, int height, int border,
                                      int format, int type, ByteBuffer pixels) {
        int size = width * height * 4;
        byte[] data = new byte[size];
        pixels.position(0);
        pixels.get(data, 0, size);
        GL11TextureFunc.uploadTexture(GL11State.boundTexture, width, height, format, type, data);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE",
        target = "Lorg/lwjgl/opengl/GL11;glTexSubImage2D(IIIIIIIILjava/nio/ByteBuffer;)V"))
    private void cathode$glTexSubImage2D(int target, int level, int xoffset, int yoffset,
                                         int width, int height,
                                         int format, int type, ByteBuffer pixels) {
        // no-op for now
    }
}   