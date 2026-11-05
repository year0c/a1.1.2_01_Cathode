package org.lwjglx.opengl.func.GL11;

import org.lwjglx.opengl.util.GL11.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK13.*;
import static org.lwjglx.opengl.util.GL11.GL11Constants.*;

public class GL11TextureFunc {

    public static void uploadTexture(int texId, int w, int h, int format, int type, byte[] data) {
        GL11Vars.log("uploadTexture: id=" + texId + " " + w + "x" + h + " format=" + format);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int vkFormat;
            int channels;
            if (format == GL_RGBA) { vkFormat = VK_FORMAT_R8G8B8A8_UNORM; channels = 4; }
            else if (format == GL_RGB) { vkFormat = VK_FORMAT_R8G8B8_UNORM; channels = 3; }
            else if (format == GL_LUMINANCE) { vkFormat = VK_FORMAT_R8_UNORM; channels = 1; }
            else if (format == GL_LUMINANCE_ALPHA) { vkFormat = VK_FORMAT_R8G8_UNORM; channels = 2; }
            else { vkFormat = VK_FORMAT_R8G8B8A8_UNORM; channels = 4; }

            long dataSize = (long)w * h * channels;

            VkImageCreateInfo ici = VkImageCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
                .imageType(VK_IMAGE_TYPE_2D)
                .format(vkFormat)
                .extent(e -> e.width(w).height(h).depth(1))
                .mipLevels(1).arrayLayers(1).samples(VK_SAMPLE_COUNT_1_BIT)
                .tiling(VK_IMAGE_TILING_OPTIMAL)
                .usage(VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT)
                .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            LongBuffer imgPtr = stack.mallocLong(1);
            int ir = vkCreateImage(GL11Vars.device, ici, null, imgPtr);
            if (ir != VK_SUCCESS) throw new RuntimeException("vkCreateImage(tex): " + ir);
            long img = imgPtr.get(0);

            VkMemoryRequirements mr = VkMemoryRequirements.calloc(stack);
            vkGetImageMemoryRequirements(GL11Vars.device, img, mr);
            VkMemoryAllocateInfo mai = VkMemoryAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(mr.size())
                .memoryTypeIndex(GL11UtilFunc.findMemoryType(mr.memoryTypeBits(), VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT));
            LongBuffer memPtr = stack.mallocLong(1);
            vkAllocateMemory(GL11Vars.device, mai, null, memPtr);
            long mem = memPtr.get(0);
            vkBindImageMemory(GL11Vars.device, img, mem, 0);

            VkImageViewCreateInfo ivci = VkImageViewCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                .image(img)
                .viewType(VK_IMAGE_VIEW_TYPE_2D)
                .format(vkFormat)
                .components(c -> c.r(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .g(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .b(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .a(VK_COMPONENT_SWIZZLE_IDENTITY))
                .subresourceRange(r -> r.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    .baseMipLevel(0)
                    .levelCount(1)
                    .baseArrayLayer(0)
                    .layerCount(1));
            LongBuffer viewPtr = stack.mallocLong(1);
            vkCreateImageView(GL11Vars.device, ivci, null, viewPtr);
            long view = viewPtr.get(0);

            ByteBuffer bb = ByteBuffer.wrap(data);
            bb.position(0);
            bb.limit((int)dataSize);
            MemoryUtil.memCopy(MemoryUtil.memAddress(bb), GL11Vars.stagingPtr, (int)dataSize);

            VkImageMemoryBarrier2.Buffer barriers = VkImageMemoryBarrier2.malloc(1);
            barriers.get(0)
                .sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER_2)
                .srcStageMask(VK_PIPELINE_STAGE_2_TOP_OF_PIPE_BIT)
                .srcAccessMask(0)
                .dstStageMask(VK_PIPELINE_STAGE_2_TRANSFER_BIT)
                .dstAccessMask(VK_ACCESS_2_TRANSFER_WRITE_BIT)
                .oldLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                .newLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
                .srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                .dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                .image(img)
                .subresourceRange(r -> r.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    .baseMipLevel(0)
                    .levelCount(1)
                    .baseArrayLayer(0)
                    .layerCount(1));

            VkDependencyInfo depInfo = VkDependencyInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_DEPENDENCY_INFO)
                .pImageMemoryBarriers(barriers);

            VkCommandBufferAllocateInfo cai = VkCommandBufferAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                .commandPool(GL11Vars.commandPool)
                .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                .commandBufferCount(1);
            PointerBuffer cmdBuf = stack.mallocPointer(1);
            vkAllocateCommandBuffers(GL11Vars.device, cai, cmdBuf);
            VkCommandBuffer cmd = new VkCommandBuffer(cmdBuf.get(0), GL11Vars.device);

            VkCommandBufferBeginInfo cbi = VkCommandBufferBeginInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
                .flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
            vkBeginCommandBuffer(cmd, cbi);
            vkCmdPipelineBarrier2(cmd, depInfo);

            VkBufferImageCopy.Buffer regions = VkBufferImageCopy.malloc(1);
            regions.get(0)
                .bufferOffset(0)
                .bufferRowLength(w)
                .bufferImageHeight(h)
                .imageSubresource(s -> s.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    .mipLevel(0)
                    .baseArrayLayer(0)
                    .layerCount(1))
                .imageExtent(e -> e.width(w).height(h).depth(1));

            vkCmdCopyBufferToImage(cmd, GL11Vars.stagingBuffer, img, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, regions);

            PointerBuffer cmdBufs = stack.mallocPointer(1).put(0, cmd.address());
            VkSubmitInfo si = VkSubmitInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                .pCommandBuffers(cmdBufs);
            vkQueueSubmit(GL11Vars.graphicsQueue, si, GL11Vars.inFlightFences[GL11Vars.currentFrame]);
            vkWaitForFences(GL11Vars.device, GL11Vars.inFlightFences[GL11Vars.currentFrame], true, Long.MAX_VALUE);
            vkResetFences(GL11Vars.device, GL11Vars.inFlightFences[GL11Vars.currentFrame]);
            PointerBuffer rawCmd = stack.mallocPointer(1).put(0, cmd.address());
            vkFreeCommandBuffers(GL11Vars.device, GL11Vars.commandPool, rawCmd);

            VkDescriptorSetAllocateInfo dsa = VkDescriptorSetAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO)
                .descriptorPool(GL11Vars.descriptorPool)
                .pSetLayouts(stack.mallocLong(1).put(0, GL11Vars.descriptorSetLayout));
            LongBuffer dsetPtr = stack.mallocLong(1);
            vkAllocateDescriptorSets(GL11Vars.device, dsa, dsetPtr);
            long dset = dsetPtr.get(0);

            VkDescriptorBufferInfo.Buffer bufInfos = VkDescriptorBufferInfo.malloc(1);
            bufInfos.get(0).buffer(GL11Vars.uniformBuffer).offset(0).range(256);

            VkDescriptorImageInfo.Buffer imgInfos = VkDescriptorImageInfo.malloc(1);
            imgInfos.get(0).sampler(GL11Vars.sampler).imageView(view).imageLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);

            VkWriteDescriptorSet.Buffer writes = VkWriteDescriptorSet.malloc(2);
            writes.get(0)
                .sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
                .dstSet(dset)
                .dstBinding(0)
                .dstArrayElement(0)
                .descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
                .descriptorCount(1)
                .pBufferInfo(bufInfos);
            writes.get(1)
                .sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
                .dstSet(dset)
                .dstBinding(1)
                .dstArrayElement(0)
                .descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
                .descriptorCount(1)
                .pImageInfo(imgInfos);
            vkUpdateDescriptorSets(GL11Vars.device, writes, null);

            GL11Vars.textureImages.put(texId, img);
            GL11Vars.textureImageViews.put(texId, view);
            GL11Vars.textureMemory.put(texId, mem);
            GL11Vars.descriptorSets.put(texId, dset);
            GL11Vars.log("  uploaded: img=" + img + " view=" + view + " dset=" + dset);
        }
    }

    public static void deleteTexture(int texId) {
        GL11Vars.log("deleteTexture: id=" + texId);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            Long img = GL11Vars.textureImages.remove(texId);
            Long view = GL11Vars.textureImageViews.remove(texId);
            Long mem = GL11Vars.textureMemory.remove(texId);
            Long dset = GL11Vars.descriptorSets.remove(texId);
            if (img != null && img != 0) vkDestroyImage(GL11Vars.device, img, null);
            if (view != null && view != 0) vkDestroyImageView(GL11Vars.device, view, null);
            if (mem != null && mem != 0) vkFreeMemory(GL11Vars.device, mem, null);
            if (dset != null && dset != 0) {
                LongBuffer sets = stack.mallocLong(1).put(0, dset);
                vkFreeDescriptorSets(GL11Vars.device, GL11Vars.descriptorPool, sets);
            }
        }
    }
}   