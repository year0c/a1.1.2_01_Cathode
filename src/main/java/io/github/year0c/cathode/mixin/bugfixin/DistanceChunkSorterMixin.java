package io.github.year0c.cathode.mixin.bugfixin;

import net.minecraft.client.render.world.DistanceChunkSorter;
import net.minecraft.client.render.world.RenderChunk;
import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DistanceChunkSorter.class)
public abstract class DistanceChunkSorterMixin {

    @Shadow private Entity camera;

    @Inject(method = "compare", at = @At("HEAD"), cancellable = true)
    private void cathode$fixCompare(RenderChunk a, RenderChunk b, CallbackInfoReturnable<Integer> ci) {
        float d1 = a.squaredDistanceToCenter(this.camera);
        float d2 = b.squaredDistanceToCenter(this.camera);
        ci.setReturnValue(Float.compare(d1, d2));
        ci.cancel();
    }   
}   