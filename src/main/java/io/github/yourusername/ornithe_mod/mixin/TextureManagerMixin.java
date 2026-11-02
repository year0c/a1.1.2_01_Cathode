package io.github.yourusername.ornithe_mod.mixin;

import org.lwjgl.opengl.GL11C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.client.render.texture.TextureManager")
public abstract class TextureManagerMixin {
    @Redirect(
        method = "load(Ljava/awt/image/BufferedImage;I)V",
        at = @At(
                value = "INVOKE",
                target = "Lorg/lwjgl/opengl/GL11;glBindTexture(II)V"
        )
    )
        private static void redirectGlBindTexture(int target, int texture) {
        GL11C.glBindTexture(target, texture);
    }

}
