package org.lwjglx.opengl.util.GL11;

import org.lwjgl.vulkan.*;
import java.util.HashMap;
import java.util.Map;

public class GL11Vars {
    public static boolean debug = true;

    public static void log(String msg) {
        if (debug) System.out.println("[GL11] " + msg);
    }

    // Core
    public static VkInstance instance;
    public static VkPhysicalDevice physicalDevice;
    public static VkDevice device;
    public static VkQueue graphicsQueue;
    public static long surface;
    public static int gfxFamily;
    public static long commandPool;
    public static VkCommandBuffer[] commandBuffers;
    public static boolean initialized = false;

    // Swapchain
    public static long swapchain;
    public static int swapchainImageCount;
    public static long[] swapchainImages;
    public static long[] swapchainImageViews;
    public static int swapchainFormat;
    public static int width = 854;
    public static int height = 480;

    // Depth
    public static long depthImage;
    public static long depthView;
    public static long depthMemory;

    // Sync
    public static long[] inFlightFences;
    public static long[] imageAvailableSemaphores;
    public static long[] renderFinishedSemaphores;
    public static int currentFrame = 0;
    public static int currentImageIndex = 0;

    // Buffers
    public static long stagingBuffer;
    public static long stagingMemory;
    public static long stagingPtr;
    public static long vertexBuffer;
    public static long vertexMemory;

    // Pipeline
    public static long vertShader;
    public static long fragShader;
    public static long pipelineLayout;
    public static long descriptorSetLayout;
    public static long descriptorPool;
    public static long uniformBuffer;
    public static long uniformMemory;
    public static long uniformPtr;
    public static long sampler;

    // Textures
    public static final Map<Integer, Long> textureImages = new HashMap<>();
    public static final Map<Integer, Long> textureImageViews = new HashMap<>();
    public static final Map<Integer, Long> textureMemory = new HashMap<>();
    public static final Map<Integer, Long> descriptorSets = new HashMap<>();
    public static final Map<Long, Long> pipelineCache = new HashMap<>();

    // Render pass + framebuffers
    public static long renderPass;
    public static long[] swapchainFramebuffers;

    // Icon
    public static long iconImageView;
    public static long iconSampler;
}   