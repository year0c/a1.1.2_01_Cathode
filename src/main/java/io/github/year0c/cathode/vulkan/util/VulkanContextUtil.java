package io.github.year0c.cathode.vulkan.util;

import org.lwjgl.opengl.util.GL11.GL11Vars;
import org.lwjgl.vulkan.*;

public class VulkanContextUtil {
    public static VkDevice getDevice() { return GL11Vars.device; }
    public static VkPhysicalDevice getPhysicalDevice() { return GL11Vars.physicalDevice; }
    public static VkQueue getQueue() { return GL11Vars.graphicsQueue; }
    public static long getCommandPool() { return GL11Vars.commandPool; }
    public static long getRenderPass() { return GL11Vars.renderPass; }
    public static long getFramebuffer() { return GL11Vars.swapchainFramebuffers[GL11Vars.currentImageIndex]; }
    public static long getIconImageView() { return GL11Vars.iconImageView; }
    public static long getIconSampler() { return GL11Vars.iconSampler; }
}   