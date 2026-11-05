package io.github.year0c.cathode.vulkan;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import io.github.year0c.cathode.vulkan.util.VulkanUtil;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryStack.*;

public class RenderLoop {

    private final VkDevice device;
    private final VkPhysicalDevice physicalDevice;
    private final VkQueue queue;
    private final long commandPool;
    private final VkCommandBuffer cmd;
    private final long fence;

    private final WorldPipeline pipeline;
    private final SkyPipeline skyPipeline;
    private final LightmapImage lightmap;

    private long vertexBuffer;
    private long vertexMemory;
    private long vertexPtr;
    private int vertexCount;
    private long vertexCapacity;

    private float viewportWidth;
    private float viewportHeight;

    private final byte[] lightmapPixels = new byte[LightmapImage.SIZE * LightmapImage.SIZE * 3];

    public RenderLoop(VkDevice device, VkPhysicalDevice physicalDevice,
        VkQueue queue, long commandPool,
        WorldPipeline pipeline, SkyPipeline skyPipeline, LightmapImage lightmap) {
        this.device = device;
        this.physicalDevice = physicalDevice;
        this.queue = queue;
        this.commandPool = commandPool;
        this.pipeline = pipeline;
        this.skyPipeline = skyPipeline;
        this.lightmap = lightmap;

        try (MemoryStack stack = stackPush()) {
            PointerBuffer cmdOut = stack.callocPointer(1);
            VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                .commandPool(commandPool)
                .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                .commandBufferCount(1);
            vkAllocateCommandBuffers(device, allocInfo, cmdOut);
            this.cmd = new VkCommandBuffer(cmdOut.get(0), device);
            allocInfo.free();
        }

        VkFenceCreateInfo fenceInfo = VkFenceCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
            .flags(VK_FENCE_CREATE_SIGNALED_BIT);
        long[] fenceOut = new long[1];
        vkCreateFence(device, fenceInfo, null, fenceOut);
        this.fence = fenceOut[0];
        fenceInfo.free();

        createVertexBuffer(1024 * 1024);
    }

    private void createVertexBuffer(long size) {
        if (vertexBuffer != 0) {
            vkDestroyBuffer(device, vertexBuffer, null);
            vkFreeMemory(device, vertexMemory, null);
        }

        VkBufferCreateInfo bufInfo = VkBufferCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
            .size(size)
            .usage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT)
            .sharingMode(VK_SHARING_MODE_EXCLUSIVE);

        long[] bufOut = new long[1];
        vkCreateBuffer(device, bufInfo, null, bufOut);
        this.vertexBuffer = bufOut[0];
        bufInfo.free();

        VkMemoryRequirements req = VkMemoryRequirements.calloc();
        vkGetBufferMemoryRequirements(device, vertexBuffer, req);

        int memType = VulkanUtil.findMemoryType(physicalDevice, req.memoryTypeBits(),
            VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);

        VkMemoryAllocateInfo alloc = VkMemoryAllocateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
            .allocationSize(req.size())
            .memoryTypeIndex(memType);

        long[] memOut = new long[1];
        vkAllocateMemory(device, alloc, null, memOut);
        this.vertexMemory = memOut[0];
        alloc.free();
        req.free();

        vkBindBufferMemory(device, vertexBuffer, vertexMemory, 0);

        try (MemoryStack stack = stackPush()) {
            PointerBuffer ptr = stack.callocPointer(1);
            vkMapMemory(device, vertexMemory, 0, size, 0, ptr);
            this.vertexPtr = ptr.get(0);
        }

        this.vertexCapacity = size;
    }

    public void pushVertex(float x, float y, float z,
        float r, float g, float b, float a,
        float u, float v,
        float lightU, float lightV) {
        int offset = vertexCount * 44;
        if (offset + 44 > vertexCapacity) {
            growVertexBuffer();
            return;
        }
        memPutFloat(vertexPtr + offset + 0, x);
        memPutFloat(vertexPtr + offset + 4, y);
        memPutFloat(vertexPtr + offset + 8, z);
        memPutFloat(vertexPtr + offset + 12, r);
        memPutFloat(vertexPtr + offset + 16, g);
        memPutFloat(vertexPtr + offset + 20, b);
        memPutFloat(vertexPtr + offset + 24, a);
        memPutFloat(vertexPtr + offset + 28, u);
        memPutFloat(vertexPtr + offset + 32, v);
        memPutFloat(vertexPtr + offset + 36, lightU);
        memPutFloat(vertexPtr + offset + 40, lightV);
        vertexCount++;
    }

    private void growVertexBuffer() {
        long newSize = vertexCapacity * 2;
        createVertexBuffer(newSize);
    }

    public void beginFrame(float width, float height) {
        this.viewportWidth = width;
        this.viewportHeight = height;
        this.vertexCount = 0;
    }

    public void updateSkyUniforms(float camX, float camY, float camZ, float fov, float aspect) {
        skyPipeline.updateUniforms(
            VulkanShader.zenithColor,
            VulkanShader.horizonColor,
            VulkanShader.sunDir,
            VulkanShader.starAlpha,
            camX, camY, camZ,
            fov, aspect
        );
        pipeline.updateFogUniforms(
            VulkanShader.fogColor,
            VulkanShader.fogDensity
        );
    }   

    public void recordWorldPass(long renderPass, long framebuffer) {
        VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
            .flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
        vkBeginCommandBuffer(cmd, beginInfo);
        beginInfo.free();

        lightmap.upload(lightmapPixels, cmd);

        // Render pass
        VkOffset2D offset = VkOffset2D.calloc();
        offset.x(0);
        offset.y(0);

        VkExtent2D extent = VkExtent2D.calloc();
        extent.width((int) viewportWidth);
        extent.height((int) viewportHeight);

        VkRect2D rect = VkRect2D.calloc()
            .offset(offset)
            .extent(extent);

        VkRenderPassBeginInfo rpInfo = VkRenderPassBeginInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
            .renderPass(renderPass)
            .framebuffer(framebuffer)
            .renderArea(rect);

        vkCmdBeginRenderPass(cmd, rpInfo, VK_SUBPASS_CONTENTS_INLINE);
        rpInfo.free();
        rect.free();
        extent.free();
        offset.free();

        // === SKY ===
        vkCmdBindPipeline(cmd, VK_PIPELINE_BIND_POINT_GRAPHICS, skyPipeline.getPipeline());

        java.nio.LongBuffer skySets = memAllocLong(1);
        skySets.put(0, skyPipeline.getDescriptorSet());
        skySets.limit(1);
        vkCmdBindDescriptorSets(cmd, VK_PIPELINE_BIND_POINT_GRAPHICS,
                skyPipeline.getPipelineLayout(), 0, skySets, null);
        memFree(skySets);

        VkViewport.Buffer skyViewports = VkViewport.calloc(1);
        skyViewports.get(0)
            .x(0).y(0)
            .width(viewportWidth)
            .height(viewportHeight)
            .minDepth(0.0f)
            .maxDepth(1.0f);
        vkCmdSetViewport(cmd, 0, skyViewports);
        skyViewports.free();

        VkOffset2D skyScissorOffset = VkOffset2D.calloc();
        skyScissorOffset.x(0);
        skyScissorOffset.y(0);
        VkExtent2D skyScissorExtent = VkExtent2D.calloc();
        skyScissorExtent.width((int) viewportWidth);
        skyScissorExtent.height((int) viewportHeight);
        VkRect2D.Buffer skyScissors = VkRect2D.calloc(1);
        skyScissors.get(0).offset(skyScissorOffset).extent(skyScissorExtent);
        vkCmdSetScissor(cmd, 0, skyScissors);
        skyScissors.free();
        skyScissorExtent.free();
        skyScissorOffset.free();

        vkCmdDraw(cmd, 3, 1, 0, 0);

        // === WORLD ===
        vkCmdBindPipeline(cmd, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline.getPipeline());

        java.nio.LongBuffer sets = memAllocLong(1);
        sets.put(0, pipeline.getDescriptorSet());
        sets.limit(1);
        vkCmdBindDescriptorSets(cmd, VK_PIPELINE_BIND_POINT_GRAPHICS,
                pipeline.getPipelineLayout(), 0, sets, null);
        memFree(sets);

        java.nio.LongBuffer bufs = memAllocLong(1);
        bufs.put(0, vertexBuffer);
        bufs.limit(1);
        java.nio.LongBuffer offsets = memAllocLong(1);
        offsets.put(0, 0);
        offsets.limit(1);
        vkCmdBindVertexBuffers(cmd, 0, bufs, offsets);
        memFree(bufs);
        memFree(offsets);

        VkViewport.Buffer viewports = VkViewport.calloc(1);
        viewports.get(0)
            .x(0).y(0)
            .width(viewportWidth)
            .height(viewportHeight)
            .minDepth(0.0f)
            .maxDepth(1.0f);
        vkCmdSetViewport(cmd, 0, viewports);
        viewports.free();

        VkOffset2D scissorOffset = VkOffset2D.calloc();
        scissorOffset.x(0);
        scissorOffset.y(0);

        VkExtent2D scissorExtent = VkExtent2D.calloc();
        scissorExtent.width((int) viewportWidth);
        scissorExtent.height((int) viewportHeight);

        VkRect2D.Buffer scissors = VkRect2D.calloc(1);
        scissors.get(0)
            .offset(scissorOffset)
            .extent(scissorExtent);
        vkCmdSetScissor(cmd, 0, scissors);
        scissors.free();
        scissorExtent.free();
        scissorOffset.free();

        vkCmdDraw(cmd, vertexCount, 1, 0, 0);

        vkCmdEndRenderPass(cmd);
        vkEndCommandBuffer(cmd);
    }

    public void submit() {
        java.nio.LongBuffer fenceBuf = memAllocLong(1);
        fenceBuf.put(0, fence);
        fenceBuf.limit(1);

        vkWaitForFences(device, fenceBuf, true, Long.MAX_VALUE);
        vkResetFences(device, fenceBuf);
        memFree(fenceBuf);

        try (MemoryStack stack = stackPush()) {
            PointerBuffer cmdBuf = stack.callocPointer(1);
            cmdBuf.put(0, cmd.address());
            cmdBuf.limit(1);

            VkSubmitInfo submit = VkSubmitInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                .pCommandBuffers(cmdBuf);

            vkQueueSubmit(queue, submit, fence);
            submit.free();
        }
    }

    public void updateLightmap(float skyLightFactor, float blockLightTint) {
        for (int i = 0; i < lightmapPixels.length / 3; i++) {
            int skyLevel = i / 16;
            int blockLevel = i % 16;

            float sky = (skyLevel / 15.0f) * skyLightFactor;
            float block = (blockLevel / 15.0f) * blockLightTint;
            float brightness = Math.min(sky + block, 1.0f);

            brightness = (float) Math.pow(brightness, 1.0 / 2.2);
            int c = (int) (brightness * 255.0f);

            lightmapPixels[i * 3 + 0] = (byte) c;
            lightmapPixels[i * 3 + 1] = (byte) c;
            lightmapPixels[i * 3 + 2] = (byte) c;
        }
    }

    public void close() {
        vkDestroyFence(device, fence, null);
        vkFreeMemory(device, vertexMemory, null);
        vkDestroyBuffer(device, vertexBuffer, null);
    }
}   