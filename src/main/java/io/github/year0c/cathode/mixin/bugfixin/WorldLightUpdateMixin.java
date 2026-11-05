package io.github.year0c.cathode.mixin.bugfixin;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldLightUpdateMixin {
    private boolean cathode$inLightUpdate;

    @Inject(method = "doLightUpdates", at = @At("HEAD"), cancellable = true)
    private void cathode$guard(CallbackInfoReturnable<Boolean> ci) {
        if (cathode$inLightUpdate) {
            ci.setReturnValue(false);
            ci.cancel();
            return;
        }
        cathode$inLightUpdate = true;
    }

    @Inject(method = "doLightUpdates", at = @At("RETURN"))
    private void cathode$unwind(CallbackInfoReturnable<Boolean> ci) {
        cathode$inLightUpdate = false;
    }
}   