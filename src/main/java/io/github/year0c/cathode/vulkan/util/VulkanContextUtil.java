package io.github.year0c.cathode.vulkan.util;

import org.lwjgl.vulkan.*;
import org.lwjglx.opengl.GL11Util;

public class VulkanContextUtil {
    public static VkDevice getDevice() { return GL11Util.device; }
    public static VkPhysicalDevice getPhysicalDevice() { return GL11Util.physicalDevice; }
    public static VkQueue getQueue() { return GL11Util.graphicsQueue; }
    public static long getCommandPool() { return GL11Util.commandPool; }
    public static long getRenderPass() { return GL11Util.renderPass; }
    public static long getFramebuffer() { return GL11Util.swapchainFramebuffers[GL11Util.currentImageIndex]; }
    public static long getIconImageView() { return GL11Util.iconImageView; }
    public static long getIconSampler() { return GL11Util.iconSampler; }
}   