package io.github.year0c.cathode.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Tesselator;
import io.github.year0c.cathode.vulkan.*;
import io.github.year0c.cathode.probe.SkyProbe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    private LightmapImage lightmap;
    private WorldPipeline pipeline;
    private RenderLoop renderLoop;

    @Inject(method = "init", at = @At("TAIL"))
    private void cathode$init(CallbackInfo ci) {
        this.lightmap = new LightmapImage(
            VulkanContext.device, VulkanContext.physicalDevice,
            VulkanContext.commandPool, VulkanContext.queue);

        this.pipeline = new WorldPipeline(VulkanContext.device, VulkanContext.renderPass);
        this.pipeline.initDescriptorSet(
            VulkanContext.iconImageView, VulkanContext.iconSampler,
            lightmap.getImageView(), lightmap.getSampler());

        this.renderLoop = new RenderLoop(
            VulkanContext.device, VulkanContext.physicalDevice,
            VulkanContext.queue, VulkanContext.commandPool,
            pipeline, lightmap);

        Tesselator.setRenderLoop(renderLoop);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void cathode$tick(CallbackInfo ci) {
        Minecraft mc = (Minecraft) (Object) this;
        SkyProbe probe = SkyProbe.getInstance();
        probe.update(mc.world.ticks);
        renderLoop.updateLightmap(probe.getSkyLightFactor(), probe.getBlockLightTint());
    }

    @Inject(method = "run", at = @At("HEAD"))
    private void cathode$beginFrame(CallbackInfo ci) {
        Minecraft mc = (Minecraft) (Object) this;
        renderLoop.beginFrame(mc.width, mc.height);
    }

    @Inject(method = "run", at = @At("TAIL"))
    private void cathode$submitFrame(CallbackInfo ci) {
        renderLoop.recordWorldPass(VulkanContext.renderPass, VulkanContext.framebuffer);
        renderLoop.submit();
    }
}   