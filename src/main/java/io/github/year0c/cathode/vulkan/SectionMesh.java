package io.github.year0c.cathode.vulkan;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import io.github.year0c.cathode.vulkan.util.VulkanUtil;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryStack.*;

/**
 * Per-section vertex storage. One instance per 16x16x16 section.
 * Holds both opaque and liquid vertices in separate buffers.
 */
public class SectionMesh {

    private final VkDevice device;
    private final VkPhysicalDevice physicalDevice;

    private long solidBuffer;
    private long solidMemory;
    private long solidPtr;
    private int solidCount;
    private long solidCapacity;

    private long liquidBuffer;
    private long liquidMemory;
    private long liquidPtr;
    private int liquidCount;
    private long liquidCapacity;

    private static final long INITIAL_SIZE = 64 * 1024; // 64K bytes = ~1454 verts

    public SectionMesh(VkDevice device, VkPhysicalDevice physicalDevice) {
        this.device = device;
        this.physicalDevice = physicalDevice;
        allocSolid(INITIAL_SIZE);
        allocLiquid(INITIAL_SIZE);
    }

    private void allocSolid(long size) {
        if (solidBuffer != 0) {
            // Save old state
            long oldPtr = solidPtr;
            long oldSize = (int) Math.min(solidCapacity, size);
            long oldMemory = solidMemory;
            long oldBuffer = solidBuffer;

            // Allocate new buffer
            VkBufferCreateInfo info = VkBufferCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                .size(size)
                .usage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT)
                .sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            long[] out = new long[1];
            vkCreateBuffer(device, info, null, out);
            solidBuffer = out[0];
            info.free();

            VkMemoryRequirements req = VkMemoryRequirements.calloc();
            vkGetBufferMemoryRequirements(device, solidBuffer, req);
            int memType = VulkanUtil.findMemoryType(physicalDevice, req.memoryTypeBits(),
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
            VkMemoryAllocateInfo alloc = VkMemoryAllocateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(req.size())
                .memoryTypeIndex(memType);
            long[] memOut = new long[1];
            vkAllocateMemory(device, alloc, null, memOut);
            solidMemory = memOut[0];
            alloc.free();
            req.free();

            vkBindBufferMemory(device, solidBuffer, solidMemory, 0);

            try (MemoryStack stack = stackPush()) {
                PointerBuffer ptr = stack.callocPointer(1);
                vkMapMemory(device, solidMemory, 0, size, 0, ptr);
                solidPtr = ptr.get(0);
            }

            // Copy old data directly (both pointers are mapped and valid)
            memCopy(oldPtr, solidPtr, (int) oldSize);

            // Free old
            vkUnmapMemory(device, oldMemory);
            vkFreeMemory(device, oldMemory, null);
            vkDestroyBuffer(device, oldBuffer, null);
        } else {
            // First allocation — no data to copy
            VkBufferCreateInfo info = VkBufferCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                .size(size)
                .usage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT)
                .sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            long[] out = new long[1];
            vkCreateBuffer(device, info, null, out);
            solidBuffer = out[0];
            info.free();

            VkMemoryRequirements req = VkMemoryRequirements.calloc();
            vkGetBufferMemoryRequirements(device, solidBuffer, req);
            int memType = VulkanUtil.findMemoryType(physicalDevice, req.memoryTypeBits(),
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
            VkMemoryAllocateInfo alloc = VkMemoryAllocateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(req.size())
                .memoryTypeIndex(memType);
            long[] memOut = new long[1];
            vkAllocateMemory(device, alloc, null, memOut);
            solidMemory = memOut[0];
            alloc.free();
            req.free();

            vkBindBufferMemory(device, solidBuffer, solidMemory, 0);

            try (MemoryStack stack = stackPush()) {
                PointerBuffer ptr = stack.callocPointer(1);
                vkMapMemory(device, solidMemory, 0, size, 0, ptr);
                solidPtr = ptr.get(0);
            }
        }

        solidCapacity = size;
    }   

    private void allocLiquid(long size) {
        if (liquidBuffer != 0) {
            long oldPtr = liquidPtr;
            long oldSize = (int) Math.min(liquidCapacity, size);
            long oldMemory = liquidMemory;
            long oldBuffer = liquidBuffer;

            VkBufferCreateInfo info = VkBufferCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                .size(size)
                .usage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT)
                .sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            long[] out = new long[1];
            vkCreateBuffer(device, info, null, out);
            liquidBuffer = out[0];
            info.free();

            VkMemoryRequirements req = VkMemoryRequirements.calloc();
            vkGetBufferMemoryRequirements(device, liquidBuffer, req);
            int memType = VulkanUtil.findMemoryType(physicalDevice, req.memoryTypeBits(),
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
            VkMemoryAllocateInfo alloc = VkMemoryAllocateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(req.size())
                .memoryTypeIndex(memType);
            long[] memOut = new long[1];
            vkAllocateMemory(device, alloc, null, memOut);
            liquidMemory = memOut[0];
            alloc.free();
            req.free();

            vkBindBufferMemory(device, liquidBuffer, liquidMemory, 0);

            try (MemoryStack stack = stackPush()) {
                PointerBuffer ptr = stack.callocPointer(1);
                vkMapMemory(device, liquidMemory, 0, size, 0, ptr);
                liquidPtr = ptr.get(0);
            }

            memCopy(oldPtr, liquidPtr, (int) oldSize);

            vkUnmapMemory(device, oldMemory);
            vkFreeMemory(device, oldMemory, null);
            vkDestroyBuffer(device, oldBuffer, null);
        } else {
            VkBufferCreateInfo info = VkBufferCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                .size(size)
                .usage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT)
                .sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            long[] out = new long[1];
            vkCreateBuffer(device, info, null, out);
            liquidBuffer = out[0];
            info.free();

            VkMemoryRequirements req = VkMemoryRequirements.calloc();
            vkGetBufferMemoryRequirements(device, liquidBuffer, req);
            int memType = VulkanUtil.findMemoryType(physicalDevice, req.memoryTypeBits(),
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
            VkMemoryAllocateInfo alloc = VkMemoryAllocateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(req.size())
                .memoryTypeIndex(memType);
            long[] memOut = new long[1];
            vkAllocateMemory(device, alloc, null, memOut);
            liquidMemory = memOut[0];
            alloc.free();
            req.free();

            vkBindBufferMemory(device, liquidBuffer, liquidMemory, 0);

            try (MemoryStack stack = stackPush()) {
                PointerBuffer ptr = stack.callocPointer(1);
                vkMapMemory(device, liquidMemory, 0, size, 0, ptr);
                liquidPtr = ptr.get(0);
            }
        }

        liquidCapacity = size;
    }

    public void clear() {
        solidCount = 0;
        liquidCount = 0;
    }

    public void pushSolid(float x, float y, float z,
                          float r, float g, float b, float a,
                          float u, float v, float lu, float lv) {
        int offset = solidCount * 44;
        if (offset + 44 > solidCapacity) {
            allocSolid(solidCapacity * 2);
            offset = solidCount * 44;
        }
        memPutFloat(solidPtr + offset + 0, x);
        memPutFloat(solidPtr + offset + 4, y);
        memPutFloat(solidPtr + offset + 8, z);
        memPutFloat(solidPtr + offset + 12, r);
        memPutFloat(solidPtr + offset + 16, g);
        memPutFloat(solidPtr + offset + 20, b);
        memPutFloat(solidPtr + offset + 24, a);
        memPutFloat(solidPtr + offset + 28, u);
        memPutFloat(solidPtr + offset + 32, v);
        memPutFloat(solidPtr + offset + 36, lu);
        memPutFloat(solidPtr + offset + 40, lv);
        solidCount++;
    }

    public void pushLiquid(float x, float y, float z,
                           float r, float g, float b, float a,
                           float u, float v, float lu, float lv) {
        int offset = liquidCount * 44;
        if (offset + 44 > liquidCapacity) {
            allocLiquid(liquidCapacity * 2);
            offset = liquidCount * 44;
        }
        memPutFloat(liquidPtr + offset + 0, x);
        memPutFloat(liquidPtr + offset + 4, y);
        memPutFloat(liquidPtr + offset + 8, z);
        memPutFloat(liquidPtr + offset + 12, r);
        memPutFloat(liquidPtr + offset + 16, g);
        memPutFloat(liquidPtr + offset + 20, b);
        memPutFloat(liquidPtr + offset + 24, a);
        memPutFloat(liquidPtr + offset + 28, u);
        memPutFloat(liquidPtr + offset + 32, v);
        memPutFloat(liquidPtr + offset + 36, lu);
        memPutFloat(liquidPtr + offset + 40, lv);
        liquidCount++;
    }

    public long getSolidBuffer()   { return solidBuffer; }
    public int getSolidCount()     { return solidCount; }
    public long getLiquidBuffer()  { return liquidBuffer; }
    public int getLiquidCount()    { return liquidCount; }

    public void close() {
        if (solidBuffer != 0) {
            vkUnmapMemory(device, solidMemory);
            vkFreeMemory(device, solidMemory, null);
            vkDestroyBuffer(device, solidBuffer, null);
            solidBuffer = 0;
        }
        if (liquidBuffer != 0) {
            vkUnmapMemory(device, liquidMemory);
            vkFreeMemory(device, liquidMemory, null);
            vkDestroyBuffer(device, liquidBuffer, null);
            liquidBuffer = 0;
        }
    }
}   