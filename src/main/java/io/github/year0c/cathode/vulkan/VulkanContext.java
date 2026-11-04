package io.github.year0c.cathode.vulkan;

import org.lwjgl.vulkan.*;

public class VulkanContext {
    public static VkDevice device;
    public static VkPhysicalDevice physicalDevice;
    public static VkQueue queue;
    public static long commandPool;
    public static long renderPass;
    public static long framebuffer;
    public static long iconImageView;
    public static long iconSampler;

    public static void init(VkDevice device, VkPhysicalDevice physicalDevice,
        VkQueue queue, long commandPool, long renderPass,
        long framebuffer, long iconImageView, long iconSampler) {
        VulkanContext.device = device;
        VulkanContext.physicalDevice = physicalDevice;
        VulkanContext.queue = queue;
        VulkanContext.commandPool = commandPool;
        VulkanContext.renderPass = renderPass;
        VulkanContext.framebuffer = framebuffer;
        VulkanContext.iconImageView = iconImageView;
        VulkanContext.iconSampler = iconSampler;
    }
}   