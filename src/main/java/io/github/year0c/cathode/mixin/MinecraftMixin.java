package io.github.year0c.cathode.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import io.github.year0c.cathode.vulkan.*;
import io.github.year0c.cathode.vulkan.util.VulkanContextUtil;
import io.github.year0c.cathode.vulkan.util.VulkanUtil;
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
    private LiquidPipeline liquidPipeline;
    private RenderLoop renderLoop;
    private SectionManager sectionManager;
    private boolean vkcInit = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void cathode$tick(CallbackInfo ci) {
        if (renderLoop == null) return;
        Minecraft mc = (Minecraft) (Object) this;
        WorldHolderUtil.set(mc.world);
        SkyProbe probe = SkyProbe.getInstance();
        probe.update(mc.world.ticks);
        VulkanSkyShader.updateSky(probe.getTimeOfDay());
        renderLoop.updateLightmap(probe.getSkyLightFactor(), probe.getBlockLightTint());
    }

    @Inject(method = "run", at = @At("HEAD"))
    private void cathode$beginFrame(CallbackInfo ci) {
        System.out.println("[Cathode] gate check: device=" + VulkanContextUtil.getDevice());   
        Minecraft mc = (Minecraft) (Object) this;

        if (!vkcInit) {
            System.out.println("[Cathode] gate check: device=" + VulkanContextUtil.getDevice());   
            if (VulkanContextUtil.getDevice() == null) return;
            vkcInit = true;

            this.lightmap = new LightmapImage(
                VulkanContextUtil.getDevice(), VulkanContextUtil.getPhysicalDevice(),
                VulkanContextUtil.getCommandPool(), VulkanContextUtil.getQueue());

            this.pipeline = new WorldPipeline(
                VulkanContextUtil.getDevice(), VulkanContextUtil.getPhysicalDevice(),
                VulkanContextUtil.getRenderPass());
            this.pipeline.initDescriptorSet(
                VulkanContextUtil.getIconImageView(), VulkanContextUtil.getIconSampler(),
                lightmap.getImageView(), lightmap.getSampler());

            this.skyPipeline = new SkyPipeline(
                VulkanContextUtil.getDevice(), VulkanContextUtil.getPhysicalDevice(),
                VulkanContextUtil.getRenderPass());

            this.liquidPipeline = new LiquidPipeline(
                VulkanContextUtil.getDevice(), VulkanContextUtil.getPhysicalDevice(),
                VulkanContextUtil.getRenderPass());
            this.liquidPipeline.initDescriptorSet(
                VulkanContextUtil.getIconImageView(), VulkanContextUtil.getIconSampler(),
                lightmap.getImageView(), lightmap.getSampler());

            this.renderLoop = new RenderLoop(
                VulkanContextUtil.getDevice(), VulkanContextUtil.getPhysicalDevice(),
                VulkanContextUtil.getQueue(), VulkanContextUtil.getCommandPool(),
                pipeline, skyPipeline, liquidPipeline, lightmap);

            this.sectionManager = new SectionManager(
                VulkanContextUtil.getDevice(), VulkanContextUtil.getPhysicalDevice());
            SectionManager.setInstance(this.sectionManager);

            System.out.println("[Cathode] init complete");
            return;
        }

        if (renderLoop == null) return;

        renderLoop.beginFrame(mc.width, mc.height);

        int chunks = mc.options.viewDistance;
        if (chunks < 2) chunks = 2;
        VulkanUtil.fogDensity = 1.0f / (chunks * 16.0f);

        renderLoop.updateAllUniforms(
            (float) mc.player.x,
            (float) mc.player.y,
            (float) mc.player.z,
            70.0f,
            (float) mc.width / (float) mc.height,
            mc.player.yaw,
            mc.player.pitch
        );

        World w = mc.world;
        if (w != null) {
            int px = (int) mc.player.x >> 4;
            int py = (int) mc.player.y >> 4;
            int pz = (int) mc.player.z >> 4;

            sectionManager.rebuildDirty(w, px, py, pz, chunks);
            renderLoop.setVisibleSections(sectionManager.getVisible(px, py, pz, chunks));
        }
    }

    @Inject(method = "run", at = @At("TAIL"))
    private void cathode$submitFrame(CallbackInfo ci) {
        if (renderLoop == null) return;
        renderLoop.recordWorldPass(VulkanContextUtil.getRenderPass(), VulkanContextUtil.getFramebuffer());
        renderLoop.submit();
    }
}   