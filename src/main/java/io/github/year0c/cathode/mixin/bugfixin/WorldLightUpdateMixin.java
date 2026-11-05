package io.github.year0c.cathode.mixin.bugfixin;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldLightUpdateMixin {
    private boolean cathode$inLightUpdate;

    @Inject(method = "doLightUpdates", at = @At("HEAD"))
    private void cathode$enter(CallbackInfoReturnable<Boolean> ci) {
        cathode$inLightUpdate = true;
    }

    @Inject(method = "doLightUpdates", at = @At("RETURN"))
    private void cathode$exit(CallbackInfoReturnable<Boolean> ci) {
        cathode$inLightUpdate = false;
    }

    @Redirect(method = "updateLight(Lnet/minecraft/world/LightType;IIIIIIZ)V",
              at = @At(value = "INVOKE",
                   target = "Lnet/minecraft/world/World;doLightUpdates()Z"))
    private boolean cathode$skipOverflowDrain(World world) {
        if (cathode$inLightUpdate) return false;
        return world.doLightUpdates();
    }
}   