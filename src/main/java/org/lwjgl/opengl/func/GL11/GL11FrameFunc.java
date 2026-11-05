package org.lwjgl.opengl.func.GL11;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.util.GL11.*;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK12.VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK13.*;
import static org.lwjgl.opengl.util.GL11.GL11Constants.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;

public class GL11FrameFunc {

    private static final int IN_FLIGHT_FRAMES = 3;

    public static void beginFrame() throws Exception {
        if (!GL11Vars.initialized) return;
        GL11Vars.log("beginFrame: frame=" + GL11Vars.currentFrame);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            vkWaitForFences(GL11Vars.device, GL11Vars.inFlightFences[GL11Vars.currentFrame], true, Long.MAX_VALUE);
            vkResetFences(GL11Vars.device, GL11Vars.inFlightFences[GL11Vars.currentFrame]);

            IntBuffer imgIdx = stack.mallocInt(1);
            vkAcquireNextImageKHR(GL11Vars.device, GL11Vars.swapchain, Long.MAX_VALUE,
                GL11Vars.imageAvailableSemaphores[GL11Vars.currentFrame], VK_NULL_HANDLE, imgIdx);
            GL11Vars.currentImageIndex = imgIdx.get(0);

            VkCommandBuffer cmd = GL11Vars.commandBuffers[GL11Vars.currentFrame];
            VkCommandBufferBeginInfo cbi = VkCommandBufferBeginInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
                .flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
            vkBeginCommandBuffer(cmd, cbi);

            updateUniformBuffer();

            VkClearColorValue ccv = VkClearColorValue.calloc(stack);
            ccv.float32(0, GL11State.clearColorR);
            ccv.float32(1, GL11State.clearColorG);
            ccv.float32(2, GL11State.clearColorB);
            ccv.float32(3, GL11State.clearColorA);

            VkClearValue colorClear = VkClearValue.calloc(stack).color(ccv);

            VkClearDepthStencilValue dsv = VkClearDepthStencilValue.calloc(stack);
            dsv.depth(1.0f);
            dsv.stencil(0);

            VkClearValue depthClear = VkClearValue.calloc(stack).depthStencil(dsv);

            VkRenderingAttachmentInfo depthAtt = VkRenderingAttachmentInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_RENDERING_ATTACHMENT_INFO)
                .imageView(GL11Vars.depthView)
                .imageLayout(VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL)
                .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                .storeOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                .clearValue(depthClear);

            VkRect2D renderArea = VkRect2D.calloc(stack);
            VkOffset2D off = VkOffset2D.calloc(stack).x(0).y(0);
            renderArea.offset(off);
            VkExtent2D ext = VkExtent2D.calloc(stack).width(GL11Vars.width).height(GL11Vars.height);
            renderArea.extent(ext);

            VkRenderingAttachmentInfo.Buffer colorAtts = VkRenderingAttachmentInfo.malloc(1);
            colorAtts.get(0)
                .sType(VK_STRUCTURE_TYPE_RENDERING_ATTACHMENT_INFO)
                .imageView(GL11Vars.swapchainImageViews[GL11Vars.currentImageIndex])
                .imageLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                .clearValue(colorClear);

            VkRenderingInfo ri = VkRenderingInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_RENDERING_INFO)
                .renderArea(renderArea)
                .layerCount(1)
                .pColorAttachments(colorAtts)
                .pDepthAttachment(depthAtt);

            vkCmdBeginRendering(cmd, ri);

            VkViewport.Buffer vps = VkViewport.malloc(1, stack);
            vps.get(0).x(GL11State.viewportX).y(GL11State.viewportY)
                .width(GL11State.viewportW).height(GL11State.viewportH).minDepth(0f).maxDepth(1f);
            vkCmdSetViewport(cmd, 0, vps);

            VkRect2D.Buffer scissors = VkRect2D.malloc(1, stack);
            VkOffset2D scOff = VkOffset2D.calloc(stack).x(GL11State.scissorX).y(GL11State.scissorY);
            scissors.get(0).offset(scOff);
            VkExtent2D scExt = VkExtent2D.calloc(stack).width(GL11State.scissorW).height(GL11State.scissorH);
            scissors.get(0).extent(scExt);
            vkCmdSetScissor(cmd, 0, scissors);

            if (GL11State.vertexCount > 0) {
                long dataSize = GL11State.vertexCount * GL11State.FLOATS_PER_VERTEX * 4L;
                MemoryUtil.memCopy(MemoryUtil.memAddress(GL11State.vertexData), GL11Vars.stagingPtr, (int)dataSize);

                VkBufferCopy.Buffer copies = VkBufferCopy.malloc(1, stack);
                copies.get(0).srcOffset(0).dstOffset(0).size(dataSize);
                vkCmdCopyBuffer(cmd, GL11Vars.stagingBuffer, GL11Vars.vertexBuffer, copies);
                GL11Vars.log("  uploaded " + GL11State.vertexCount + " verts (" + dataSize + " bytes)");
            }
        }
    }

    public static void endFrame() throws Exception {
        if (!GL11Vars.initialized) return;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBuffer cmd = GL11Vars.commandBuffers[GL11Vars.currentFrame];

            vkCmdEndRendering(cmd);
            vkEndCommandBuffer(cmd);

            PointerBuffer cmdBufs = stack.mallocPointer(1).put(0, cmd.address());

            VkSubmitInfo si = VkSubmitInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                .pWaitSemaphores(stack.mallocLong(1).put(0, GL11Vars.imageAvailableSemaphores[GL11Vars.currentFrame]))
                .pWaitDstStageMask(stack.mallocInt(1).put(0, VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT))
                .pCommandBuffers(cmdBufs)
                .pSignalSemaphores(stack.mallocLong(1).put(0, GL11Vars.renderFinishedSemaphores[GL11Vars.currentFrame]));

            vkQueueSubmit(GL11Vars.graphicsQueue, si, GL11Vars.inFlightFences[GL11Vars.currentFrame]);

            VkPresentInfoKHR pi = VkPresentInfoKHR.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
                .pWaitSemaphores(stack.mallocLong(1).put(0, GL11Vars.renderFinishedSemaphores[GL11Vars.currentFrame]))
                .pSwapchains(stack.mallocLong(1).put(0, GL11Vars.swapchain))
                .pImageIndices(stack.mallocInt(1).put(0, GL11Vars.currentImageIndex));

            vkQueuePresentKHR(GL11Vars.graphicsQueue, pi);

            GL11Vars.currentFrame = (GL11Vars.currentFrame + 1) % IN_FLIGHT_FRAMES;
            GL11Vars.log("endFrame: presented, next frame=" + GL11Vars.currentFrame);
        }
    }

    private static void updateUniformBuffer() {
        float[] mv = GL11State.modelviewStack[GL11State.modelviewSp];
        float[] pr = GL11State.projectionStack[GL11State.projectionSp];
        float[] tx = GL11State.textureStack[GL11State.textureSp];

        for (int i = 0; i < 16; i++) {
            MemoryUtil.memPutFloat(GL11Vars.uniformPtr + i * 4L, mv[i]);
            MemoryUtil.memPutFloat(GL11Vars.uniformPtr + (16 + i) * 4L, pr[i]);
            MemoryUtil.memPutFloat(GL11Vars.uniformPtr + (32 + i) * 4L, tx[i]);
        }
    }
}   