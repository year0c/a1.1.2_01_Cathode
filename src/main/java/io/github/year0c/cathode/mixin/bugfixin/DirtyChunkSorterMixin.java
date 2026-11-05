package io.github.year0c.cathode.mixin.bugfixin;

import net.minecraft.client.render.world.DirtyChunkSorter;
import net.minecraft.client.render.world.RenderChunk;
import net.minecraft.entity.mob.player.PlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DirtyChunkSorter.class)
public abstract class DirtyChunkSorterMixin {

    @Shadow private PlayerEntity camera;

    @Inject(method = "compare", at = @At("HEAD"), cancellable = true)
    private void cathode$fixCompare(RenderChunk a, RenderChunk b, CallbackInfoReturnable<Integer> ci) {
        boolean va = a.visible;
        boolean vb = b.visible;
        if (va && !vb) {
            ci.setReturnValue(1);
            ci.cancel();
            return;
        }
        if (vb && !va) {
            ci.setReturnValue(-1);
            ci.cancel();
            return;
        }
        float d1 = a.squaredDistanceToCenter(this.camera);
        float d2 = b.squaredDistanceToCenter(this.camera);
        ci.setReturnValue(Float.compare(d2, d1));
        ci.cancel();
    }
}   