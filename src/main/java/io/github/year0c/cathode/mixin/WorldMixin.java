package io.github.year0c.cathode.mixin;

import net.minecraft.world.World;
import io.github.year0c.cathode.vulkan.SectionManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin {

    @Inject(method = "setBlock", at = @At("HEAD"))
    private void cathode$onBlockChange(int x, int y, int z, int blockId,
                                        CallbackInfoReturnable<Boolean> cir) {
        SectionManager sm = SectionManager.get();
        if (sm != null) {
            sm.markBlockDirty(x, y, z);
        }
    }
}   