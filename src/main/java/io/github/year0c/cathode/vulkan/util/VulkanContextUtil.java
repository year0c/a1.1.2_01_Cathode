package io.github.year0c.cathode.vulkan.util;

import org.lwjgl.vulkan.*;

public class VulkanContextUtil {
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
        VulkanContextUtil.device = device;
        VulkanContextUtil.physicalDevice = physicalDevice;
        VulkanContextUtil.queue = queue;
        VulkanContextUtil.commandPool = commandPool;
        VulkanContextUtil.renderPass = renderPass;
        VulkanContextUtil.framebuffer = framebuffer;
        VulkanContextUtil.iconImageView = iconImageView;
        VulkanContextUtil.iconSampler = iconSampler;
    }
}   