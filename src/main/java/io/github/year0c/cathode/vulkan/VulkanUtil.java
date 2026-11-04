package io.github.year0c.cathode.vulkan;

import org.lwjgl.vulkan.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanUtil {

    public static int findMemoryType(VkPhysicalDevice physicalDevice, int typeBits, int requiredProps) {
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
            throw new RuntimeException("No suitable memory type for bits=0x" + Integer.toHexString(typeBits));
        }
        return result;
    }
}