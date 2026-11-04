package io.github.year0c.cathode.vulkan;

import org.lwjgl.vulkan.*;

import org.lwjgl.PointerBuffer;   

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.system.MemoryUtil.*;

public class LightmapImage {

    public static final int SIZE = 16;
    private static final int FORMAT = VK_FORMAT_R8G8B8_UNORM;

    private final VkDevice device;
    private final VkPhysicalDevice physicalDevice;
    private final long commandPool;
    private final VkQueue queue;

    private long image;
    private long imageView;
    private long imageMemory;
    private long sampler;
    private long stagingBuffer;
    private long stagingMemory;
    private long stagingPtr;

    public LightmapImage(VkDevice device, VkPhysicalDevice physicalDevice,
        long commandPool, VkQueue queue) {
        this.device = device;
        this.physicalDevice = physicalDevice;
        this.commandPool = commandPool;
        this.queue = queue;

        createImage();
        createImageView();
        createSampler();
        createStagingBuffer();
        uploadInitial();
    }

    private void createImage() {
        VkExtent3D extent = VkExtent3D.calloc();
        extent.width(SIZE);
        extent.height(SIZE);
        extent.depth(1);

        VkImageCreateInfo info = VkImageCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
            .imageType(VK_IMAGE_TYPE_2D)
            .format(FORMAT)
            .extent(extent)
            .mipLevels(1)
            .arrayLayers(1)
            .samples(VK_SAMPLE_COUNT_1_BIT)
            .tiling(VK_IMAGE_TILING_OPTIMAL)
            .usage(VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT)
            .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
            .sharingMode(VK_SHARING_MODE_EXCLUSIVE);

        long[] out = new long[1];
        vkCreateImage(device, info, null, out);
        this.image = out[0];
        info.free();
        extent.free();

        VkMemoryRequirements req = VkMemoryRequirements.calloc();
        vkGetImageMemoryRequirements(device, image, req);

        int memType = VulkanUtil.findMemoryType(physicalDevice, req.memoryTypeBits(),
            VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);

        VkMemoryAllocateInfo alloc = VkMemoryAllocateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
            .allocationSize(req.size())
            .memoryTypeIndex(memType);

        long[] memOut = new long[1];
        vkAllocateMemory(device, alloc, null, memOut);
        this.imageMemory = memOut[0];
        alloc.free();
        req.free();   

        vkBindImageMemory(device, image, imageMemory, 0);   
    }

    private void createImageView() {
        VkComponentMapping components = VkComponentMapping.calloc()
            .r(VK_COMPONENT_SWIZZLE_IDENTITY)
            .g(VK_COMPONENT_SWIZZLE_IDENTITY)
            .b(VK_COMPONENT_SWIZZLE_IDENTITY)
            .a(VK_COMPONENT_SWIZZLE_IDENTITY);

        VkImageSubresourceRange range = VkImageSubresourceRange.calloc()
            .aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
            .levelCount(1)
            .layerCount(1);

        VkImageViewCreateInfo info = VkImageViewCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
            .image(image)
            .viewType(VK_IMAGE_VIEW_TYPE_2D)
            .format(FORMAT)
            .components(components)
            .subresourceRange(range);

        long[] out = new long[1];
        vkCreateImageView(device, info, null, out);
        this.imageView = out[0];
        info.free();
        components.free();
        range.free();
    }

    private void createSampler() {
        VkSamplerCreateInfo info = VkSamplerCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO)
            .magFilter(VK_FILTER_NEAREST)
            .minFilter(VK_FILTER_NEAREST)
            .mipmapMode(VK_SAMPLER_MIPMAP_MODE_NEAREST)
            .addressModeU(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
            .addressModeV(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
            .addressModeW(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
            .mipLodBias(0.0f)
            .anisotropyEnable(false)
            .maxAnisotropy(1.0f)
            .compareEnable(false)
            .minLod(0.0f)
            .maxLod(0.0f)
            .borderColor(VK_BORDER_COLOR_INT_TRANSPARENT_BLACK);

        long[] out = new long[1];
        vkCreateSampler(device, info, null, out);
        this.sampler = out[0];
        info.free();
    }

    private void createStagingBuffer() {
        long size = SIZE * SIZE * 3;

        VkBufferCreateInfo bufInfo = VkBufferCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
            .size(size)
            .usage(VK_BUFFER_USAGE_TRANSFER_SRC_BIT)
            .sharingMode(VK_SHARING_MODE_EXCLUSIVE);

        long[] bufOut = new long[1];
        vkCreateBuffer(device, bufInfo, null, bufOut);
        this.stagingBuffer = bufOut[0];
        bufInfo.free();

        VkMemoryRequirements bufReq = VkMemoryRequirements.calloc();
        vkGetBufferMemoryRequirements(device, stagingBuffer, bufReq);

        int memType = VulkanUtil.findMemoryType(physicalDevice, bufReq.memoryTypeBits(),
            VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);

        VkMemoryAllocateInfo alloc = VkMemoryAllocateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
            .allocationSize(bufReq.size())
            .memoryTypeIndex(memType);

        long[] memOut = new long[1];
        vkAllocateMemory(device, alloc, null, memOut);
        this.stagingMemory = memOut[0];
        alloc.free();
        bufReq.free();

        vkBindBufferMemory(device, stagingBuffer, stagingMemory, 0);

        PointerBuffer ptr = memAllocPointer(1);
        vkMapMemory(device, stagingMemory, 0, size, 0, ptr);
        this.stagingPtr = ptr.get(0);
        memFree(ptr);   
    }   

    private int findMemoryType(int typeBits, int requiredProps) {
        VkPhysicalDeviceMemoryProperties memProps = VkPhysicalDeviceMemoryProperties.calloc();
        vkGetPhysicalDeviceMemoryProperties(physicalDevice, memProps);

        int result = -1;
        for (int i = 0; i < memProps.memoryTypeCount(); i++) {
            if ((typeBits & (1 << i)) != 0) {
                VkMemoryType mt = memProps.memoryTypes(i);
                if ((mt.propertyFlags() & requiredProps) == requiredProps) {
                    result = i;
                    break;
                }
            }
        }
        if (result == -1) {
            for (int i = 0; i < memProps.memoryTypeCount(); i++) {
                if ((typeBits & (1 << i)) != 0) {
                    result = i;
                    break;
                }
            }
        }
        memProps.free();

        if (result == -1) {
            throw new RuntimeException("No suitable memory type for bits=0x"
                + Integer.toHexString(typeBits));
        }
        return result;
    }

    /**
     * Upload 768 bytes (16x16 R8G8B8) into the lightmap image.
     * Call once per tick from your render loop, inside a command buffer.
     */
    public void upload(byte[] pixels, VkCommandBuffer cmd) {
        memCopy(pixels, stagingPtr, 0, pixels.length);

        VkImageSubresourceRange range = VkImageSubresourceRange.calloc()
            .aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
            .levelCount(1)
            .layerCount(1);

        // Barrier 1: UNDEFINED → TRANSFER_DST
        VkImageMemoryBarrier.Buffer imgBarriers = VkImageMemoryBarrier.calloc(1);
        imgBarriers.get(0)
            .sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
            .srcAccessMask(0)
            .dstAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT)
            .oldLayout(VK_IMAGE_LAYOUT_UNDEFINED)
            .newLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
            .image(image)
            .subresourceRange(range);

        vkCmdPipelineBarrier(cmd,
            VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT,
            VK_PIPELINE_STAGE_TRANSFER_BIT,
            0,
            (VkMemoryBarrier.Buffer) null,
            (VkBufferMemoryBarrier.Buffer) null,
            imgBarriers);

        // Copy
        VkOffset3D offset = VkOffset3D.calloc();
        offset.x(0); offset.y(0); offset.z(0);

        VkExtent3D copyExtent = VkExtent3D.calloc();
        copyExtent.width(SIZE); copyExtent.height(SIZE); copyExtent.depth(1);

        VkImageSubresourceLayers subres = VkImageSubresourceLayers.calloc()
            .aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
            .mipLevel(0)
            .baseArrayLayer(0)
            .layerCount(1);

        VkBufferImageCopy.Buffer regions = VkBufferImageCopy.calloc(1);
        regions.get(0)
            .bufferOffset(0)
            .bufferRowLength(SIZE)
            .bufferImageHeight(SIZE)
            .imageSubresource(subres)
            .imageOffset(offset)
            .imageExtent(copyExtent);

        vkCmdCopyBufferToImage(cmd, stagingBuffer, image,
            VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, regions);

        // Barrier 2: TRANSFER_DST → SHADER_READ_ONLY
        imgBarriers.get(0)
            .srcAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT)
            .dstAccessMask(VK_ACCESS_SHADER_READ_BIT)
            .oldLayout(VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL)
            .newLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);

        vkCmdPipelineBarrier(cmd,
            VK_PIPELINE_STAGE_TRANSFER_BIT,
            VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT,
            0,
            (VkMemoryBarrier.Buffer) null,
            (VkBufferMemoryBarrier.Buffer) null,
            imgBarriers);

        // Free
        regions.free();
        subres.free();
        offset.free();
        copyExtent.free();
        imgBarriers.free();
        range.free();
    }   

    /** Fill with white (fully lit) on init. */
    private void uploadInitial() {
        byte[] white = new byte[SIZE * SIZE * 3];
        java.util.Arrays.fill(white, (byte) 255);

        VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
            .commandPool(commandPool)
            .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
            .commandBufferCount(1);

        PointerBuffer cmdOut = memAllocPointer(1);
        vkAllocateCommandBuffers(device, allocInfo, cmdOut);
        VkCommandBuffer cmd = new VkCommandBuffer(cmdOut.get(0), device);   
        allocInfo.free();

        VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
            .flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);

        vkBeginCommandBuffer(cmd, beginInfo);
        upload(white, cmd);
        vkEndCommandBuffer(cmd);
        beginInfo.free();

        VkSubmitInfo submit = VkSubmitInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
            .pCommandBuffers(cmdOut);

        vkQueueSubmit(queue, submit, VK_NULL_HANDLE);
        vkQueueWaitIdle(queue);

        submit.free();
        vkFreeCommandBuffers(device, commandPool, cmdOut);
        memFree(cmdOut);   
    }

    public long getImageView() { return imageView; }
    public long getSampler()   { return sampler; }

    public void close() {
        vkDestroySampler(device, sampler, null);
        vkDestroyImageView(device, imageView, null);
        vkUnmapMemory(device, stagingMemory);
        vkFreeMemory(device, stagingMemory, null);
        vkDestroyBuffer(device, stagingBuffer, null);
        vkFreeMemory(device, imageMemory, null);
        vkDestroyImage(device, image, null);
    }
}