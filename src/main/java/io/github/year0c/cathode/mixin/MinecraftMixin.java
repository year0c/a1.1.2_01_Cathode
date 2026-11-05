package io.github.year0c.cathode.mixin;

import net.minecraft.client.Minecraft;
import io.github.year0c.cathode.vulkan.*;
import io.github.year0c.cathode.vulkan.util.RenderLoopHolderUtil;
import io.github.year0c.cathode.vulkan.util.VulkanContextUtil;
import io.github.year0c.cathode.vulkan.util.WorldHolderUtil;
import io.github.year0c.cathode.probe.SkyProbe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    private LightmapImage lightmap;
    private WorldPipeline pipeline;
    private SkyPipeline skyPipeline;
    private RenderLoop renderLoop;

    @Inject(method = "init", at = @At("TAIL"))
    private void cathode$init(CallbackInfo ci) {
        System.out.println("[Cathode] init hook fired");
        System.out.println("[Cathode] device=" + VulkanContextUtil.device);

        if (VulkanContextUtil.device == null) {
            System.err.println("[Cathode] ABORT: VulkanContextUtil not initialized");
            return;
        }

        this.lightmap = new LightmapImage(
            VulkanContextUtil.device, VulkanContextUtil.physicalDevice,
            VulkanContextUtil.commandPool, VulkanContextUtil.queue);

        this.pipeline = new WorldPipeline(
            VulkanContextUtil.device, VulkanContextUtil.physicalDevice,
            VulkanContextUtil.renderPass);   

        this.skyPipeline = new SkyPipeline(
            VulkanContextUtil.device, VulkanContextUtil.physicalDevice,
            VulkanContextUtil.renderPass);

        this.renderLoop = new RenderLoop(
            VulkanContextUtil.device, VulkanContextUtil.physicalDevice,
            VulkanContextUtil.queue, VulkanContextUtil.commandPool,
            pipeline, skyPipeline, lightmap);

        RenderLoopHolderUtil.set(renderLoop);
        System.out.println("[Cathode] init complete");
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void cathode$tick(CallbackInfo ci) {
        if (renderLoop == null) return;
        Minecraft mc = (Minecraft) (Object) this;
        WorldHolderUtil.set(mc.world);
        SkyProbe probe = SkyProbe.getInstance();
        probe.update(mc.world.ticks);
        VulkanShader.updateSky(probe.getTimeOfDay());
        renderLoop.updateLightmap(probe.getSkyLightFactor(), probe.getBlockLightTint());
    }

    @Inject(method = "run", at = @At("HEAD"))
    private void cathode$beginFrame(CallbackInfo ci) {
        if (renderLoop == null) return;
        Minecraft mc = (Minecraft) (Object) this;
        renderLoop.beginFrame(mc.width, mc.height);
        float aspect = (float) mc.width / (float) mc.height;

        // Fog density from viewDistance (chunks → blocks)
        int chunks = mc.options.viewDistance;
        if (chunks < 2) chunks = 2;
        VulkanShader.fogDensity = 1.0f / (chunks * 16.0f);

        renderLoop.updateSkyUniforms(
            (float) mc.player.x,
            (float) mc.player.y,
            (float) mc.player.z,
            70.0f,
            aspect
        );
    }   

    @Inject(method = "run", at = @At("TAIL"))
    private void cathode$submitFrame(CallbackInfo ci) {
        if (renderLoop == null) return;
        renderLoop.recordWorldPass(VulkanContextUtil.renderPass, VulkanContextUtil.framebuffer);
        renderLoop.submit();
    }
}   