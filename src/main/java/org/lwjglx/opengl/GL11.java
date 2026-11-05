package org.lwjglx.opengl;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.sdl.SDLVulkan;

import org.jspecify.annotations.Nullable;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK12.VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK13.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.KHRSurface.*;

public class GL11 {

    // ═══════════════════════════════════════════
    //  GL CONSTANTS
    // ═══════════════════════════════════════════

    public static final int GL_MODELVIEW = 0x1B01;
    public static final int GL_PROJECTION = 0x1B05;
    public static final int GL_TEXTURE = 0x1B02;
    public static final int GL_DEPTH_TEST = 0x0B71;
    public static final int GL_BLEND = 0x0BE2;
    public static final int GL_TEXTURE_2D = 0x0DE1;
    public static final int GL_CULL_FACE = 0x0B44;
    public static final int GL_ALPHA_TEST = 0x0BC0;
    public static final int GL_FOG = 0x0B60;
    public static final int GL_SCISSOR_TEST = 0x0C11;
    public static final int GL_POLYGON_OFFSET_FILL = 0x8037;
    public static final int GL_BACK = 0x0405;
    public static final int GL_FRONT = 0x0404;
    public static final int GL_FRONT_AND_BACK = 0x0408;
    public static final int GL_NEVER = 0x0200;
    public static final int GL_LESS = 0x0201;
    public static final int GL_EQUAL = 0x0202;
    public static final int GL_LEQUAL = 0x0203;
    public static final int GL_GREATER = 0x0204;
    public static final int GL_NOTEQUAL = 0x0205;
    public static final int GL_GEQUAL = 0x0206;
    public static final int GL_ALWAYS = 0x0207;
    public static final int GL_ZERO = 0;
    public static final int GL_ONE = 1;
    public static final int GL_SRC_COLOR = 0x0300;
    public static final int GL_ONE_MINUS_SRC_COLOR = 0x0301;
    public static final int GL_SRC_ALPHA = 0x0302;
    public static final int GL_ONE_MINUS_SRC_ALPHA = 0x0303;
    public static final int GL_DST_ALPHA = 0x0304;
    public static final int GL_ONE_MINUS_DST_ALPHA = 0x0305;
    public static final int GL_DST_COLOR = 0x0306;
    public static final int GL_ONE_MINUS_DST_COLOR = 0x0307;
    public static final int GL_SRC_ALPHA_SATURATE = 0x0308;
    public static final int GL_POINTS = 0;
    public static final int GL_LINES = 1;
    public static final int GL_LINE_LOOP = 2;
    public static final int GL_LINE_STRIP = 3;
    public static final int GL_TRIANGLES = 4;
    public static final int GL_TRIANGLE_STRIP = 5;
    public static final int GL_TRIANGLE_FAN = 6;
    public static final int GL_QUADS = 7;
    public static final int GL_COLOR_BUFFER_BIT = 0x4000;
    public static final int GL_DEPTH_BUFFER_BIT = 0x0100;
    public static final int GL_STENCIL_BUFFER_BIT = 0x0400;
    public static final int GL_FOG_MODE = 0x1074;
    public static final int GL_FOG_START = 0x1075;
    public static final int GL_FOG_END = 0x1076;
    public static final int GL_FOG_DENSITY = 0x1077;
    public static final int GL_FOG_COLOR = 0x1079;
    public static final int GL_LINEAR = 0x2601;
    public static final int GL_EXP = 0x0800;
    public static final int GL_EXP2 = 0x0801;
    public static final int GL_VENDOR = 0x1F00;
    public static final int GL_RENDERER = 0x1F01;
    public static final int GL_VERSION = 0x1F02;
    public static final int GL_SHADING_LANGUAGE_VERSION = 0x9245;
    public static final int GL_MAX_TEXTURE_SIZE = 0x0D5B;
    public static final int GL_MAX_VIEWPORT_DIMS = 0x0D5A;
    public static final int GL_TEXTURE_MIN_FILTER = 0x2801;
    public static final int GL_TEXTURE_MAG_FILTER = 0x2800;
    public static final int GL_TEXTURE_WRAP_S = 0x2802;
    public static final int GL_TEXTURE_WRAP_T = 0x2803;
    public static final int GL_NEAREST = 0x2600;
    public static final int GL_LINEAR_MIPMAP_LINEAR = 0x2603;
    public static final int GL_REPEAT = 0x2901;
    public static final int GL_CLAMP_TO_EDGE = 0x812F;
    public static final int GL_INT = 0x1404;
    public static final int GL_FLOAT = 0x1406;
    public static final int GL_RGBA = 0x1908;
    public static final int GL_RGB = 0x1907;
    public static final int GL_DEPTH_COMPONENT = 0x1902;
    public static final int GL_LUMINANCE = 0x1909;
    public static final int GL_LUMINANCE_ALPHA = 0x8040;
    public static final int GL_UNSIGNED_BYTE = 0x1401;
    public static final int GL_BYTE = 0x1400;
    public static final int GL_SHORT = 0x1402;
    public static final int GL_UNSIGNED_SHORT = 0x1403;
    public static final int GL_DEPTH_SIZE = 0x0B51;
    public static final int GL_DEPTH_BITS = 0x0B50;
    public static final int GL_ALIASED_POINT_SIZE_RANGE = 0x846D;
    public static final int GL_POINT_SIZE = 0x0B1C;
    public static final int GL_DEPTH_RANGE = 0x0B70;
    public static final int GL_VIEWPORT = 0x0BA2;
    public static final int GL_POLYGON_OFFSET_FACTOR = 0x8038;
    public static final int GL_POLYGON_OFFSET_UNITS = 0x2A01;
    public static final int GL_CURRENT_COLOR = 0x0800;
    public static final int GL_CURRENT_TEXTURE_COORDS = 0x0862;
    public static final int GL_CURRENT_NORMAL = 0x0B02;

    // ═══════════════════════════════════════════
    //  GL STATE (stays in GL11)
    // ═══════════════════════════════════════════

    private static final float[][] modelviewStack = new float[32][16];
    private static final float[][] projectionStack = new float[32][16];
    private static final float[][] textureStack = new float[32][16];
    private static int modelviewSp = 0, projectionSp = 0, textureSp = 0;
    private static int currentMatrixMode = GL_MODELVIEW;

    private static final boolean[] enabled = new boolean[4096];
    private static int blendSrc = GL_SRC_ALPHA;
    private static int blendDst = GL_ONE_MINUS_SRC_ALPHA;
    private static int depthFunc = GL_LEQUAL;
    private static boolean depthMask = true;
    private static int cullFaceMode = GL_BACK;
    private static float alphaRef = 0.0f;
    private static int alphaFuncMode = GL_GREATER;

    private static float fogStart = 0f, fogEnd = 1f, fogDensity = 1f;
    private static float fogColorR = 1f, fogColorG = 1f, fogColorB = 1f, fogColorA = 1f;
    private static int fogMode = GL_LINEAR;

    private static int viewportX = 0, viewportY = 0, viewportW = 854, viewportH = 480;
    private static int scissorX = 0, scissorY = 0, scissorW = 854, scissorH = 480;

    private static float clearColorR = 0, clearColorG = 0, clearColorB = 0, clearColorA = 1;
    private static double clearDepth = 1.0;

    private static float polygonOffsetFactor = 0f, polygonOffsetUnits = 0f;

    private static int boundTexture = 0;
    private static int[] textureParams = new int[256];
    private static int nextTextureId = 1;

    private static boolean inBegin = false;
    private static int beginMode = 0;
    private static int vertexCount = 0;
    private static final int MAX_VERTICES = 65536;
    private static final int FLOATS_PER_VERTEX = 12;
    private static final FloatBuffer vertexData = org.lwjglx.BufferUtils.createFloatBuffer(MAX_VERTICES * FLOATS_PER_VERTEX);
    private static float curR = 1f, curG = 1f, curB = 1f, curA = 1f;
    private static float curU = 0f, curV = 0f;
    private static float curNx = 0f, curNy = 1f, curNz = 0f;

    private static final int IN_FLIGHT_FRAMES = 3;

    static {
        identity(modelviewStack[0]);
        identity(projectionStack[0]);
        identity(textureStack[0]);
    }
    
    // ═══════════════════════════════════════════
    //  VULKAN INIT
    // ═══════════════════════════════════════════

    public static void initVulkan(long sdlWindow, int w, int h) throws Exception {
        GL11Util.width = w;
        GL11Util.height = h;
        createInstance();
        createSurface(sdlWindow);
        pickPhysicalDevice();
        createDevice();
        createCommandPool();
        createSwapchain();
        createDepthResources();
        createSyncObjects();
        createCommandBuffers();
        createStagingBuffer();
        createVertexBuffer();
        createUniformBuffer();
        createShaders();
        createPipelineLayout();
        createSampler();
        createRenderPass();
        createIcon();
        GL11Util.initialized = true;
        System.out.println("[GL11] Vulkan init done, device=" + GL11Util.device + " renderPass=" + GL11Util.renderPass + " icon=" + GL11Util.iconImageView);   
    }

    private static void createInstance() throws Exception {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                .pApplicationName(stack.UTF8("Cathode"))
                .applicationVersion(VK_API_VERSION_1_0)
                .pEngineName(stack.UTF8("GL11-Vulkan"))
                .engineVersion(VK_API_VERSION_1_0)
                .apiVersion(VK_API_VERSION_1_3);

            String[] exts = { VK_KHR_SURFACE_EXTENSION_NAME };
            PointerBuffer extPtrs = stack.mallocPointer(exts.length);
            for (int i = 0; i < exts.length; i++)
                extPtrs.put(i, MemoryUtil.memAddress(stack.UTF8(exts[i])));

            VkInstanceCreateInfo ci = VkInstanceCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                .pApplicationInfo(appInfo)
                .ppEnabledExtensionNames(extPtrs);

            PointerBuffer instPtr = stack.mallocPointer(1);
            int result = vkCreateInstance(ci, null, instPtr);
            if (result != VK_SUCCESS) throw new RuntimeException("vkCreateInstance: " + result);
            GL11Util.instance = new VkInstance(instPtr.get(0), ci);
        }
    }

    private static void createSurface(long sdlWindow) throws Exception {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer surfBuf = stack.mallocLong(1);
            boolean ok = SDLVulkan.SDL_Vulkan_CreateSurface(sdlWindow, GL11Util.instance, null, surfBuf);
            if (!ok) throw new RuntimeException("SDL_Vulkan_CreateSurface failed");
            GL11Util.surface = surfBuf.get(0);
        }
    }

    private static void pickPhysicalDevice() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer count = stack.mallocInt(1).put(0, 0);
            vkEnumeratePhysicalDevices(GL11Util.instance, count, null);
            PointerBuffer devices = stack.mallocPointer(count.get(0));
            vkEnumeratePhysicalDevices(GL11Util.instance, count, devices);
            GL11Util.physicalDevice = new VkPhysicalDevice(devices.get(0), GL11Util.instance);
        }
    }

    private static void createDevice() throws Exception {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer qCount = stack.mallocInt(1).put(0, 0);
            vkGetPhysicalDeviceQueueFamilyProperties(GL11Util.physicalDevice, qCount, null);
            VkQueueFamilyProperties.Buffer families = VkQueueFamilyProperties.malloc(qCount.get(0));
            vkGetPhysicalDeviceQueueFamilyProperties(GL11Util.physicalDevice, qCount, families);
            GL11Util.gfxFamily = 0;
            for (int i = 0; i < qCount.get(0); i++) {
                if ((families.get(i).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) { GL11Util.gfxFamily = i; break; }
            }

            VkPhysicalDeviceVulkan13Features v13 = VkPhysicalDeviceVulkan13Features.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_3_FEATURES)
                .dynamicRendering(true)
                .synchronization2(true);

            String[] devExts = { VK_KHR_SWAPCHAIN_EXTENSION_NAME };
            PointerBuffer devExtPtrs = stack.mallocPointer(devExts.length);
            for (int i = 0; i < devExts.length; i++)
                devExtPtrs.put(i, MemoryUtil.memAddress(stack.UTF8(devExts[i])));

            VkDeviceQueueCreateInfo.Buffer qcis = VkDeviceQueueCreateInfo.malloc(1);
            qcis.get(0)
                .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                .queueFamilyIndex(GL11Util.gfxFamily)
                .pQueuePriorities(stack.mallocFloat(1).put(0, 1.0f));

            VkDeviceCreateInfo dci = VkDeviceCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                .pNext(v13)
                .pQueueCreateInfos(qcis)
                .ppEnabledExtensionNames(devExtPtrs);

            PointerBuffer devPtr = stack.mallocPointer(1);
            int result = vkCreateDevice(GL11Util.physicalDevice, dci, null, devPtr);
            if (result != VK_SUCCESS) throw new RuntimeException("vkCreateDevice: " + result);
            GL11Util.device = new VkDevice(devPtr.get(0), GL11Util.physicalDevice, dci, VK_API_VERSION_1_3);
            PointerBuffer qPtr = stack.mallocPointer(1);
            vkGetDeviceQueue(GL11Util.device, GL11Util.gfxFamily, 0, qPtr);
            GL11Util.graphicsQueue = new VkQueue(qPtr.get(0), GL11Util.device);
        }
    }

    private static void createCommandPool() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandPoolCreateInfo ci = VkCommandPoolCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
                .flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT)
                .queueFamilyIndex(GL11Util.gfxFamily);
            LongBuffer poolPtr = stack.mallocLong(1);
            vkCreateCommandPool(GL11Util.device, ci, null, poolPtr);
            GL11Util.commandPool = poolPtr.get(0);
        }
    }

    private static void createSwapchain() throws Exception {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkSurfaceCapabilitiesKHR caps = VkSurfaceCapabilitiesKHR.calloc(stack);
            vkGetPhysicalDeviceSurfaceCapabilitiesKHR(GL11Util.physicalDevice, GL11Util.surface, caps);

            int fmtFormat = VK_FORMAT_B8G8R8A8_SRGB;
            int fmtColorSpace = VK_COLOR_SPACE_SRGB_NONLINEAR_KHR;

            int extW = (int) caps.currentExtent().width();
            int extH = (int) caps.currentExtent().height();
            if (extW == 0xFFFFFFFF) { extW = GL11Util.width; extH = GL11Util.height; }

            int imgCount = caps.minImageCount() + 1;
            if (caps.maxImageCount() > 0 && imgCount > caps.maxImageCount()) imgCount = caps.maxImageCount();

            VkExtent2D extent = VkExtent2D.calloc(stack).width(extW).height(extH);
            VkSwapchainCreateInfoKHR sci = VkSwapchainCreateInfoKHR.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                .surface(GL11Util.surface)
                .minImageCount(imgCount)
                .imageFormat(fmtFormat)
                .imageColorSpace(fmtColorSpace)
                .imageExtent(extent)
                .imageArrayLayers(1)
                .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
                .imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
                .preTransform(caps.currentTransform())
                .compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
                .presentMode(VK_PRESENT_MODE_FIFO_KHR)
                .clipped(true)
                .oldSwapchain(VK_NULL_HANDLE);

            LongBuffer swapPtr = stack.mallocLong(1);
            int result = vkCreateSwapchainKHR(GL11Util.device, sci, null, swapPtr);
            if (result != VK_SUCCESS) throw new RuntimeException("vkCreateSwapchainKHR: " + result);
            GL11Util.swapchain = swapPtr.get(0);
            GL11Util.swapchainFormat = fmtFormat;
            GL11Util.width = extW;
            GL11Util.height = extH;

            IntBuffer imgCountBuf = stack.mallocInt(1).put(0, 0);
            vkGetSwapchainImagesKHR(GL11Util.device, GL11Util.swapchain, imgCountBuf, null);
            GL11Util.swapchainImageCount = imgCountBuf.get(0);
            GL11Util.swapchainImages = new long[GL11Util.swapchainImageCount];
            LongBuffer imgBuf = stack.mallocLong(GL11Util.swapchainImageCount);
            vkGetSwapchainImagesKHR(GL11Util.device, GL11Util.swapchain, imgCountBuf, imgBuf);
            for (int i = 0; i < GL11Util.swapchainImageCount; i++)
                GL11Util.swapchainImages[i] = imgBuf.get(i);

            GL11Util.swapchainImageViews = new long[GL11Util.swapchainImageCount];
            for (int i = 0; i < GL11Util.swapchainImageCount; i++) {
                VkImageViewCreateInfo ivci = VkImageViewCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                    .image(GL11Util.swapchainImages[i])
                    .viewType(VK_IMAGE_VIEW_TYPE_2D)
                    .format(GL11Util.swapchainFormat)
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
                vkCreateImageView(GL11Util.device, ivci, null, viewPtr);
                GL11Util.swapchainImageViews[i] = viewPtr.get(0);
            }
        }
    }

    private static void createDepthResources() throws Exception {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkImageCreateInfo ici = VkImageCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
                .imageType(VK_IMAGE_TYPE_2D)
                .format(VK_FORMAT_D32_SFLOAT)
                .extent(e -> e.width(GL11Util.width).height(GL11Util.height).depth(1))
                .mipLevels(1).arrayLayers(1).samples(VK_SAMPLE_COUNT_1_BIT)
                .tiling(VK_IMAGE_TILING_OPTIMAL)
                .usage(VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT)
                .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            LongBuffer imgPtr = stack.mallocLong(1);
            vkCreateImage(GL11Util.device, ici, null, imgPtr);
            GL11Util.depthImage = imgPtr.get(0);

            VkMemoryRequirements mr = VkMemoryRequirements.calloc(stack);
            vkGetImageMemoryRequirements(GL11Util.device, GL11Util.depthImage, mr);
            VkMemoryAllocateInfo mai = VkMemoryAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(mr.size())
                .memoryTypeIndex(findMemoryType(mr.memoryTypeBits(), VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT));
            LongBuffer memPtr = stack.mallocLong(1);
            vkAllocateMemory(GL11Util.device, mai, null, memPtr);
            GL11Util.depthMemory = memPtr.get(0);
            vkBindImageMemory(GL11Util.device, GL11Util.depthImage, GL11Util.depthMemory, 0);

            VkImageViewCreateInfo ivci = VkImageViewCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                .image(GL11Util.depthImage)
                .viewType(VK_IMAGE_VIEW_TYPE_2D)
                .format(VK_FORMAT_D32_SFLOAT)
                .components(c -> c.r(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .g(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .b(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .a(VK_COMPONENT_SWIZZLE_IDENTITY))
                .subresourceRange(r -> r.aspectMask(VK_IMAGE_ASPECT_DEPTH_BIT)
                    .baseMipLevel(0)
                    .levelCount(1)
                    .baseArrayLayer(0)
                    .layerCount(1));
            LongBuffer viewPtr = stack.mallocLong(1);
            vkCreateImageView(GL11Util.device, ivci, null, viewPtr);
            GL11Util.depthView = viewPtr.get(0);
        }
    }

    private static void createSyncObjects() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL11Util.inFlightFences = new long[IN_FLIGHT_FRAMES];
            GL11Util.imageAvailableSemaphores = new long[IN_FLIGHT_FRAMES];
            GL11Util.renderFinishedSemaphores = new long[IN_FLIGHT_FRAMES];
            VkFenceCreateInfo fci = VkFenceCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
                .flags(VK_FENCE_CREATE_SIGNALED_BIT);
            VkSemaphoreCreateInfo sci = VkSemaphoreCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);
            LongBuffer ptr = stack.mallocLong(1);
            for (int i = 0; i < IN_FLIGHT_FRAMES; i++) {
                vkCreateFence(GL11Util.device, fci, null, ptr);
                GL11Util.inFlightFences[i] = ptr.get(0);
                vkCreateSemaphore(GL11Util.device, sci, null, ptr);
                GL11Util.imageAvailableSemaphores[i] = ptr.get(0);
                vkCreateSemaphore(GL11Util.device, sci, null, ptr);
                GL11Util.renderFinishedSemaphores[i] = ptr.get(0);
            }
        }
    }

    private static void createCommandBuffers() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferAllocateInfo cai = VkCommandBufferAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                .commandPool(GL11Util.commandPool)
                .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                .commandBufferCount(IN_FLIGHT_FRAMES);
            PointerBuffer cmdBuf = stack.mallocPointer(IN_FLIGHT_FRAMES);
            vkAllocateCommandBuffers(GL11Util.device, cai, cmdBuf);
            GL11Util.commandBuffers = new VkCommandBuffer[IN_FLIGHT_FRAMES];
            for (int i = 0; i < IN_FLIGHT_FRAMES; i++)
                GL11Util.commandBuffers[i] = new VkCommandBuffer(cmdBuf.get(i), GL11Util.device);
        }
    }

    private static void createStagingBuffer() {
        long size = MAX_VERTICES * FLOATS_PER_VERTEX * 4L;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCreateInfo bci = VkBufferCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                .size(size)
                .usage(VK_BUFFER_USAGE_TRANSFER_SRC_BIT)
                .sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            LongBuffer bufPtr = stack.mallocLong(1);
            vkCreateBuffer(GL11Util.device, bci, null, bufPtr);
            GL11Util.stagingBuffer = bufPtr.get(0);

            VkMemoryRequirements mr = VkMemoryRequirements.calloc(stack);
            vkGetBufferMemoryRequirements(GL11Util.device, GL11Util.stagingBuffer, mr);
            VkMemoryAllocateInfo mai = VkMemoryAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(mr.size())
                .memoryTypeIndex(findMemoryType(mr.memoryTypeBits(), VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT));
            LongBuffer memPtr = stack.mallocLong(1);
            vkAllocateMemory(GL11Util.device, mai, null, memPtr);
            GL11Util.stagingMemory = memPtr.get(0);
            vkBindBufferMemory(GL11Util.device, GL11Util.stagingBuffer, GL11Util.stagingMemory, 0);
            PointerBuffer mapped = stack.mallocPointer(1);
            vkMapMemory(GL11Util.device, GL11Util.stagingMemory, 0, size, 0, mapped);
            GL11Util.stagingPtr = mapped.get(0);
        }
    }

    private static void createVertexBuffer() {
        long size = MAX_VERTICES * FLOATS_PER_VERTEX * 4L;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCreateInfo bci = VkBufferCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                .size(size)
                .usage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT)
                .sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            LongBuffer bufPtr = stack.mallocLong(1);
            vkCreateBuffer(GL11Util.device, bci, null, bufPtr);
            GL11Util.vertexBuffer = bufPtr.get(0);

            VkMemoryRequirements mr = VkMemoryRequirements.calloc(stack);
            vkGetBufferMemoryRequirements(GL11Util.device, GL11Util.vertexBuffer, mr);
            VkMemoryAllocateInfo mai = VkMemoryAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(mr.size())
                .memoryTypeIndex(findMemoryType(mr.memoryTypeBits(), VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT));
            LongBuffer memPtr = stack.mallocLong(1);
            vkAllocateMemory(GL11Util.device, mai, null, memPtr);
            GL11Util.vertexMemory = memPtr.get(0);
            vkBindBufferMemory(GL11Util.device, GL11Util.vertexBuffer, GL11Util.vertexMemory, 0);
        }
    }

    private static void createUniformBuffer() {
        long size = 256;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCreateInfo bci = VkBufferCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                .size(size)
                .usage(VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT)
                .sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            LongBuffer bufPtr = stack.mallocLong(1);
            vkCreateBuffer(GL11Util.device, bci, null, bufPtr);
            GL11Util.uniformBuffer = bufPtr.get(0);

            VkMemoryRequirements mr = VkMemoryRequirements.calloc(stack);
            vkGetBufferMemoryRequirements(GL11Util.device, GL11Util.uniformBuffer, mr);
            VkMemoryAllocateInfo mai = VkMemoryAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(mr.size())
                .memoryTypeIndex(findMemoryType(mr.memoryTypeBits(), VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT));
            LongBuffer memPtr = stack.mallocLong(1);
            vkAllocateMemory(GL11Util.device, mai, null, memPtr);
            GL11Util.uniformMemory = memPtr.get(0);
            vkBindBufferMemory(GL11Util.device, GL11Util.uniformBuffer, GL11Util.uniformMemory, 0);
            PointerBuffer mapped = stack.mallocPointer(1);
            vkMapMemory(GL11Util.device, GL11Util.uniformMemory, 0, size, 0, mapped);
            GL11Util.uniformPtr = mapped.get(0);
        }
    }

    private static void createShaders() throws Exception {
        byte[] vertSpv = loadSpirV("vert.spv");
        byte[] fragSpv = loadSpirV("frag.spv");

        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer vCode = stack.malloc(vertSpv.length);
            vCode.put(vertSpv);
            VkShaderModuleCreateInfo vsci = VkShaderModuleCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                .pCode(vCode);
            LongBuffer vPtr = stack.mallocLong(1);
            vkCreateShaderModule(GL11Util.device, vsci, null, vPtr);
            GL11Util.vertShader = vPtr.get(0);

            ByteBuffer fCode = stack.malloc(fragSpv.length);
            fCode.put(fragSpv);
            VkShaderModuleCreateInfo fsci = VkShaderModuleCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                .pCode(fCode);
            LongBuffer fPtr = stack.mallocLong(1);
            vkCreateShaderModule(GL11Util.device, fsci, null, fPtr);
            GL11Util.fragShader = fPtr.get(0);
        }
    }

    private static void createPipelineLayout() throws Exception {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkDescriptorSetLayoutBinding.Buffer bindings = VkDescriptorSetLayoutBinding.malloc(2);
            bindings.get(0)
                .binding(0)
                .descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
                .descriptorCount(1)
                .stageFlags(VK_SHADER_STAGE_VERTEX_BIT);
            bindings.get(1)
                .binding(1)
                .descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
                .descriptorCount(1)
                .stageFlags(VK_SHADER_STAGE_FRAGMENT_BIT);

            VkDescriptorSetLayoutCreateInfo dslci = VkDescriptorSetLayoutCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
                .pBindings(bindings);
            LongBuffer dslPtr = stack.mallocLong(1);
            vkCreateDescriptorSetLayout(GL11Util.device, dslci, null, dslPtr);
            GL11Util.descriptorSetLayout = dslPtr.get(0);

            LongBuffer layoutPtrs = stack.mallocLong(1).put(0, GL11Util.descriptorSetLayout);
            VkPipelineLayoutCreateInfo plci = VkPipelineLayoutCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                .setLayoutCount(1)
                .pSetLayouts(layoutPtrs);
            LongBuffer plPtr = stack.mallocLong(1);
            vkCreatePipelineLayout(GL11Util.device, plci, null, plPtr);
            GL11Util.pipelineLayout = plPtr.get(0);

            VkDescriptorPoolSize.Buffer sizes = VkDescriptorPoolSize.malloc(2);
            sizes.get(0).type(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER).descriptorCount(64);
            sizes.get(1).type(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER).descriptorCount(64);

            VkDescriptorPoolCreateInfo dpci = VkDescriptorPoolCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO)
                .maxSets(64)
                .pPoolSizes(sizes);
            LongBuffer dpPtr = stack.mallocLong(1);
            vkCreateDescriptorPool(GL11Util.device, dpci, null, dpPtr);
            GL11Util.descriptorPool = dpPtr.get(0);
        }
    }

    private static void createSampler() throws Exception {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkSamplerCreateInfo sci = VkSamplerCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO)
                .magFilter(VK_FILTER_NEAREST)
                .minFilter(VK_FILTER_NEAREST)
                .mipmapMode(VK_SAMPLER_MIPMAP_MODE_NEAREST)
                .addressModeU(VK_SAMPLER_ADDRESS_MODE_REPEAT)
                .addressModeV(VK_SAMPLER_ADDRESS_MODE_REPEAT)
                .addressModeW(VK_SAMPLER_ADDRESS_MODE_REPEAT)
                .anisotropyEnable(false)
                .mipLodBias(0f)
                .compareEnable(false)
                .maxAnisotropy(1f)
                .borderColor(VK_BORDER_COLOR_INT_TRANSPARENT_BLACK);
            LongBuffer sPtr = stack.mallocLong(1);
            vkCreateSampler(GL11Util.device, sci, null, sPtr);
            GL11Util.sampler = sPtr.get(0);
        }
    }

    private static void createRenderPass() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkAttachmentDescription.Buffer atts = VkAttachmentDescription.malloc(2);
            atts.get(0)
                .format(GL11Util.swapchainFormat)
                .samples(VK_SAMPLE_COUNT_1_BIT)
                .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                .finalLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);
            atts.get(1)
                .format(VK_FORMAT_D32_SFLOAT)
                .samples(VK_SAMPLE_COUNT_1_BIT)
                .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                .storeOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                .finalLayout(VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL);

            VkAttachmentReference.Buffer colorRefs = VkAttachmentReference.malloc(1, stack);
            colorRefs.get(0).attachment(0).layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

            VkAttachmentReference depthRef = VkAttachmentReference.calloc(stack).attachment(1).layout(VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL);

            VkSubpassDescription.Buffer subs = VkSubpassDescription.malloc(1, stack);
            subs.get(0)
                .pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS)
                .colorAttachmentCount(1)
                .pColorAttachments(colorRefs)
                .pDepthStencilAttachment(depthRef);

            VkRenderPassCreateInfo rpci = VkRenderPassCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO)
                .pAttachments(atts)
                .pSubpasses(subs);   

            LongBuffer ptr = stack.mallocLong(1);
            vkCreateRenderPass(GL11Util.device, rpci, null, ptr);
            GL11Util.renderPass = ptr.get(0);

            GL11Util.swapchainFramebuffers = new long[GL11Util.swapchainImageCount];
            for (int i = 0; i < GL11Util.swapchainImageCount; i++) {
                LongBuffer views = stack.mallocLong(2);
                views.put(0, GL11Util.swapchainImageViews[i]);
                views.put(1, GL11Util.depthView);
                VkFramebufferCreateInfo fci = VkFramebufferCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                    .renderPass(GL11Util.renderPass)
                    .width(GL11Util.width).height(GL11Util.height).layers(1)
                    .attachmentCount(2)
                    .pAttachments(views);
                LongBuffer fbPtr = stack.mallocLong(1);
                vkCreateFramebuffer(GL11Util.device, fci, null, fbPtr);
                GL11Util.swapchainFramebuffers[i] = fbPtr.get(0);
            }
        }
    }

    private static void createIcon() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkImageCreateInfo ici = VkImageCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
                .imageType(VK_IMAGE_TYPE_2D)
                .format(VK_FORMAT_R8G8B8_UNORM)
                .extent(e -> e.width(1).height(1).depth(1))
                .mipLevels(1).arrayLayers(1).samples(VK_SAMPLE_COUNT_1_BIT)
                .tiling(VK_IMAGE_TILING_OPTIMAL)
                .usage(VK_IMAGE_USAGE_SAMPLED_BIT)
                .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            LongBuffer imgPtr = stack.mallocLong(1);
            vkCreateImage(GL11Util.device, ici, null, imgPtr);
            long img = imgPtr.get(0);

            VkMemoryRequirements mr = VkMemoryRequirements.calloc(stack);
            vkGetImageMemoryRequirements(GL11Util.device, img, mr);
            VkMemoryAllocateInfo mai = VkMemoryAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(mr.size())
                .memoryTypeIndex(findMemoryType(mr.memoryTypeBits(), VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT));
            LongBuffer memPtr = stack.mallocLong(1);
            vkAllocateMemory(GL11Util.device, mai, null, memPtr);
            long mem = memPtr.get(0);
            vkBindImageMemory(GL11Util.device, img, mem, 0);

            VkImageViewCreateInfo ivci = VkImageViewCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                .image(img)
                .viewType(VK_IMAGE_VIEW_TYPE_2D)
                .format(VK_FORMAT_R8G8B8_UNORM)
                .components(c -> c.r(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .g(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .b(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .a(VK_COMPONENT_SWIZZLE_IDENTITY))
                .subresourceRange(r -> r.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    .levelCount(1).layerCount(1));
            LongBuffer viewPtr = stack.mallocLong(1);
            vkCreateImageView(GL11Util.device, ivci, null, viewPtr);
            GL11Util.iconImageView = viewPtr.get(0);
            GL11Util.iconSampler = GL11Util.sampler;
        }
    }   

    // ═══════════════════════════════════════════
    //  PIPELINE CREATION
    // ═══════════════════════════════════════════

    private static long getOrCreatePipeline(int topology) {
        int cull = 0;
        if (enabled[GL_CULL_FACE]) {
            if (cullFaceMode == GL_BACK) cull = 1;
            else if (cullFaceMode == GL_FRONT) cull = 2;
            else cull = 3;
        }
        int blend = 0;
        if (enabled[GL_BLEND]) {
            blend = (blendSrc << 8) | blendDst;
        }
        int depthTest = enabled[GL_DEPTH_TEST] ? 1 : 0;
        int depthWrite = depthTest == 1 && depthMask ? 1 : 0;
        int depthFuncCode = depthTest == 1 ? depthFunc : 0;

        long key = ((long)topology << 48) | ((long)cull << 40) | ((long)blend << 24)
                 | ((long)depthTest << 20) | ((long)depthWrite << 16) | depthFuncCode;

        Long cached = GL11Util.pipelineCache.get(key);
        if (cached != null) return cached;

        long pipeline = createPipeline(topology, cull, blend, depthTest, depthWrite);
        GL11Util.pipelineCache.put(key, pipeline);
        return pipeline;
    }

    private static long createPipeline(int topology, int cull, int blend, int depthTest, int depthWrite) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int stride = FLOATS_PER_VERTEX * 4;
            VkVertexInputBindingDescription.Buffer vbDescs = VkVertexInputBindingDescription.malloc(1, stack);
            vbDescs.get(0).binding(0).stride(stride).inputRate(VK_VERTEX_INPUT_RATE_VERTEX);

            VkVertexInputAttributeDescription.Buffer vaDescs = VkVertexInputAttributeDescription.malloc(4, stack);
            vaDescs.get(0).location(0).binding(0).format(VK_FORMAT_R32G32B32_SFLOAT).offset(0);
            vaDescs.get(1).location(1).binding(0).format(VK_FORMAT_R32G32B32A32_SFLOAT).offset(12);
            vaDescs.get(2).location(2).binding(0).format(VK_FORMAT_R32G32_SFLOAT).offset(28);
            vaDescs.get(3).location(3).binding(0).format(VK_FORMAT_R32G32B32_SFLOAT).offset(36);

            VkPipelineVertexInputStateCreateInfo viState = VkPipelineVertexInputStateCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
                .pVertexBindingDescriptions(vbDescs)
                .pVertexAttributeDescriptions(vaDescs);

            int vkTopo;
            switch (topology) {
                case GL_POINTS: vkTopo = VK_PRIMITIVE_TOPOLOGY_POINT_LIST; break;
                case GL_LINES: vkTopo = VK_PRIMITIVE_TOPOLOGY_LINE_LIST; break;
                case GL_LINE_LOOP: vkTopo = VK_PRIMITIVE_TOPOLOGY_LINE_LIST; break;
                case GL_LINE_STRIP: vkTopo = VK_PRIMITIVE_TOPOLOGY_LINE_STRIP; break;
                case GL_TRIANGLES: vkTopo = VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST; break;
                case GL_TRIANGLE_STRIP: vkTopo = VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP; break;
                case GL_TRIANGLE_FAN: vkTopo = VK_PRIMITIVE_TOPOLOGY_TRIANGLE_FAN; break;
                case GL_QUADS: vkTopo = VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST; break;
                default: vkTopo = VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;
            }

            VkPipelineInputAssemblyStateCreateInfo iaState = VkPipelineInputAssemblyStateCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO)
                .topology(vkTopo)
                .primitiveRestartEnable(false);

            IntBuffer dynStates = stack.mallocInt(2);
            dynStates.put(0, VK_DYNAMIC_STATE_VIEWPORT);
            dynStates.put(1, VK_DYNAMIC_STATE_SCISSOR);

            VkViewport.Buffer vps = VkViewport.malloc(1);
            vps.get(0).x(viewportX).y(viewportY).width(viewportW).height(viewportH).minDepth(0f).maxDepth(1f);

            VkRect2D.Buffer scissors = VkRect2D.malloc(1);
            VkOffset2D scOff = VkOffset2D.calloc(stack).x(scissorX).y(scissorY);
            scissors.get(0).offset(scOff);
            VkExtent2D scExt = VkExtent2D.calloc(stack).width(scissorW).height(scissorH);
            scissors.get(0).extent(scExt);

            VkPipelineViewportStateCreateInfo vpState = VkPipelineViewportStateCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO)
                .viewportCount(1)
                .scissorCount(1)
                .pViewports(vps)
                .pScissors(scissors);

            VkPipelineRasterizationStateCreateInfo rsState = VkPipelineRasterizationStateCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO)
                .depthClampEnable(false)
                .rasterizerDiscardEnable(false)
                .polygonMode(VK_POLYGON_MODE_FILL)
                .lineWidth(1.0f)
                .cullMode(VK_CULL_MODE_NONE);
            if (cull == 1) rsState.cullMode(VK_CULL_MODE_BACK_BIT);
            else if (cull == 2) rsState.cullMode(VK_CULL_MODE_FRONT_BIT);
            else if (cull == 3) rsState.cullMode(VK_CULL_MODE_FRONT_BIT | VK_CULL_MODE_BACK_BIT);
            rsState.frontFace(VK_FRONT_FACE_COUNTER_CLOCKWISE);

            VkPipelineMultisampleStateCreateInfo msState = VkPipelineMultisampleStateCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
                .rasterizationSamples(VK_SAMPLE_COUNT_1_BIT)
                .sampleShadingEnable(false);

            VkPipelineDepthStencilStateCreateInfo dsState = VkPipelineDepthStencilStateCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO)
                .depthTestEnable(depthTest == 1)
                .depthWriteEnable(depthWrite == 1)
                .depthCompareOp(VK_COMPARE_OP_LESS_OR_EQUAL)
                .depthBoundsTestEnable(false)
                .stencilTestEnable(false);
            if (depthTest == 1) {
                switch (depthFunc) {
                    case GL_NEVER: dsState.depthCompareOp(VK_COMPARE_OP_NEVER); break;
                    case GL_LESS: dsState.depthCompareOp(VK_COMPARE_OP_LESS); break;
                    case GL_EQUAL: dsState.depthCompareOp(VK_COMPARE_OP_EQUAL); break;
                    case GL_LEQUAL: dsState.depthCompareOp(VK_COMPARE_OP_LESS_OR_EQUAL); break;
                    case GL_GREATER: dsState.depthCompareOp(VK_COMPARE_OP_GREATER); break;
                    case GL_NOTEQUAL: dsState.depthCompareOp(VK_COMPARE_OP_NOT_EQUAL); break;
                    case GL_GEQUAL: dsState.depthCompareOp(VK_COMPARE_OP_GREATER_OR_EQUAL); break;
                    case GL_ALWAYS: dsState.depthCompareOp(VK_COMPARE_OP_ALWAYS); break;
                }
            }

            VkPipelineColorBlendAttachmentState.Buffer attachments = VkPipelineColorBlendAttachmentState.malloc(1);
            attachments.get(0)
                .colorWriteMask(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT)
                .blendEnable(false)
                .srcColorBlendFactor(VK_BLEND_FACTOR_ONE)
                .dstColorBlendFactor(VK_BLEND_FACTOR_ZERO)
                .colorBlendOp(VK_BLEND_OP_ADD)
                .srcAlphaBlendFactor(VK_BLEND_FACTOR_ONE)
                .dstAlphaBlendFactor(VK_BLEND_FACTOR_ZERO)
                .alphaBlendOp(VK_BLEND_OP_ADD);
            if (blend != 0) {
                attachments.get(0).blendEnable(true);
                int src = (blend >> 8) & 0xFF;
                int dst = blend & 0xFF;
                attachments.get(0).srcColorBlendFactor(glBlendFactorToVk(src))
                    .dstColorBlendFactor(glBlendFactorToVk(dst))
                    .srcAlphaBlendFactor(glBlendFactorToVk(src))
                    .dstAlphaBlendFactor(glBlendFactorToVk(dst));
            }

            VkPipelineColorBlendStateCreateInfo cbState = VkPipelineColorBlendStateCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO)
                .logicOpEnable(false)
                .logicOp(VK_LOGIC_OP_COPY)
                .attachmentCount(1)
                .pAttachments(attachments);

            VkPipelineShaderStageCreateInfo.Buffer stages = VkPipelineShaderStageCreateInfo.malloc(2, stack);
            stages.get(0)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                .stage(VK_SHADER_STAGE_VERTEX_BIT)
                .module(GL11Util.vertShader)
                .pName(stack.UTF8("main"));
            stages.get(1)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                .stage(VK_SHADER_STAGE_FRAGMENT_BIT)
                .module(GL11Util.fragShader)
                .pName(stack.UTF8("main"));

            VkGraphicsPipelineCreateInfo.Buffer gpcis = VkGraphicsPipelineCreateInfo.malloc(1, stack);
            gpcis.get(0)
                .sType(VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO)
                .pStages(stages)
                .pVertexInputState(viState)
                .pInputAssemblyState(iaState)
                .pTessellationState(null)
                .pViewportState(vpState)
                .pRasterizationState(rsState)
                .pMultisampleState(msState)
                .pDepthStencilState(dsState)
                .pColorBlendState(cbState)
                .layout(GL11Util.pipelineLayout);

            LongBuffer pipePtr = stack.mallocLong(1);
            int result = vkCreateGraphicsPipelines(GL11Util.device, VK_NULL_HANDLE, gpcis, null, pipePtr);
            if (result != VK_SUCCESS) throw new RuntimeException("vkCreateGraphicsPipelines: " + result);
            return pipePtr.get(0);
        }
    }

    private static int glBlendFactorToVk(int glFactor) {
        switch (glFactor) {
            case GL_ZERO: return VK_BLEND_FACTOR_ZERO;
            case GL_ONE: return VK_BLEND_FACTOR_ONE;
            case GL_SRC_COLOR: return VK_BLEND_FACTOR_SRC_COLOR;
            case GL_ONE_MINUS_SRC_COLOR: return VK_BLEND_FACTOR_ONE_MINUS_SRC_COLOR;
            case GL_SRC_ALPHA: return VK_BLEND_FACTOR_SRC_ALPHA;
            case GL_ONE_MINUS_SRC_ALPHA: return VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA;
            case GL_DST_ALPHA: return VK_BLEND_FACTOR_DST_ALPHA;
            case GL_ONE_MINUS_DST_ALPHA: return VK_BLEND_FACTOR_ONE_MINUS_DST_ALPHA;
            case GL_DST_COLOR: return VK_BLEND_FACTOR_DST_COLOR;
            case GL_ONE_MINUS_DST_COLOR: return VK_BLEND_FACTOR_ONE_MINUS_DST_COLOR;
            case GL_SRC_ALPHA_SATURATE: return VK_BLEND_FACTOR_SRC_ALPHA_SATURATE;
            default: return VK_BLEND_FACTOR_ONE;
        }
    }

    // ═══════════════════════════════════════════
    //  TEXTURE UPLOAD
    // ═══════════════════════════════════════════

    private static void uploadTexture(int texId, int w, int h, int format, int type, byte[] data) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int vkFormat;
            int channels;
            if (format == GL_RGBA) { vkFormat = VK_FORMAT_R8G8B8A8_UNORM; channels = 4; }
            else if (format == GL_RGB) { vkFormat = VK_FORMAT_R8G8B8_UNORM; channels = 3; }
            else if (format == GL_LUMINANCE) { vkFormat = VK_FORMAT_R8_UNORM; channels = 1; }
            else if (format == GL_LUMINANCE_ALPHA) { vkFormat = VK_FORMAT_R8G8_UNORM; channels = 2; }
            else { vkFormat = VK_FORMAT_R8G8B8A8_UNORM; channels = 4; }

            int bytesPerChannel = 1;
            long dataSize = (long)w * h * channels * bytesPerChannel;

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
            vkCreateImage(GL11Util.device, ici, null, imgPtr);
            long img = imgPtr.get(0);

            VkMemoryRequirements mr = VkMemoryRequirements.calloc(stack);
            vkGetImageMemoryRequirements(GL11Util.device, img, mr);
            VkMemoryAllocateInfo mai = VkMemoryAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(mr.size())
                .memoryTypeIndex(findMemoryType(mr.memoryTypeBits(), VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT));
            LongBuffer memPtr = stack.mallocLong(1);
            vkAllocateMemory(GL11Util.device, mai, null, memPtr);
            long mem = memPtr.get(0);
            vkBindImageMemory(GL11Util.device, img, mem, 0);

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
            vkCreateImageView(GL11Util.device, ivci, null, viewPtr);
            long view = viewPtr.get(0);

            ByteBuffer bb = ByteBuffer.wrap(data);
            bb.position(0);
            bb.limit((int)dataSize);
            MemoryUtil.memCopy(MemoryUtil.memAddress(bb), GL11Util.stagingPtr, (int)dataSize);

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
                .commandPool(GL11Util.commandPool)
                .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                .commandBufferCount(1);
            PointerBuffer cmdBuf = stack.mallocPointer(1);
            vkAllocateCommandBuffers(GL11Util.device, cai, cmdBuf);
            VkCommandBuffer cmd = new VkCommandBuffer(cmdBuf.get(0), GL11Util.device);

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

            vkCmdCopyBufferToImage(cmd, GL11Util.stagingBuffer, img, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, regions);

            PointerBuffer cmdBufs = stack.mallocPointer(1).put(0, cmd.address());
            VkSubmitInfo si = VkSubmitInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                .pCommandBuffers(cmdBufs);
            vkQueueSubmit(GL11Util.graphicsQueue, si, GL11Util.inFlightFences[GL11Util.currentFrame]);
            vkWaitForFences(GL11Util.device, GL11Util.inFlightFences[GL11Util.currentFrame], true, Long.MAX_VALUE);
            vkResetFences(GL11Util.device, GL11Util.inFlightFences[GL11Util.currentFrame]);
            PointerBuffer rawCmd = stack.mallocPointer(1).put(0, cmd.address());
            vkFreeCommandBuffers(GL11Util.device, GL11Util.commandPool, rawCmd);

            VkDescriptorSetAllocateInfo dsa = VkDescriptorSetAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO)
                .descriptorPool(GL11Util.descriptorPool)
                .pSetLayouts(stack.mallocLong(1).put(0, GL11Util.descriptorSetLayout));
            LongBuffer dsetPtr = stack.mallocLong(1);
            vkAllocateDescriptorSets(GL11Util.device, dsa, dsetPtr);
            long dset = dsetPtr.get(0);

            VkDescriptorBufferInfo.Buffer bufInfos = VkDescriptorBufferInfo.malloc(1);
            bufInfos.get(0).buffer(GL11Util.uniformBuffer).offset(0).range(256);

            VkDescriptorImageInfo.Buffer imgInfos = VkDescriptorImageInfo.malloc(1);
            imgInfos.get(0).sampler(GL11Util.sampler).imageView(view).imageLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);

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
            vkUpdateDescriptorSets(GL11Util.device, writes, null);

            GL11Util.textureImages.put(texId, img);
            GL11Util.textureImageViews.put(texId, view);
            GL11Util.textureMemory.put(texId, mem);
            GL11Util.descriptorSets.put(texId, dset);
        }
    }   

    // ═══════════════════════════════════════════
    //  FRAME LOOP
    // ═══════════════════════════════════════════

    public static void beginFrame() throws Exception {
        if (!GL11Util.initialized) return;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            vkWaitForFences(GL11Util.device, GL11Util.inFlightFences[GL11Util.currentFrame], true, Long.MAX_VALUE);
            vkResetFences(GL11Util.device, GL11Util.inFlightFences[GL11Util.currentFrame]);

            IntBuffer imgIdx = stack.mallocInt(1);
            vkAcquireNextImageKHR(GL11Util.device, GL11Util.swapchain, Long.MAX_VALUE,
                GL11Util.imageAvailableSemaphores[GL11Util.currentFrame], VK_NULL_HANDLE, imgIdx);
            GL11Util.currentImageIndex = imgIdx.get(0);

            VkCommandBuffer cmd = GL11Util.commandBuffers[GL11Util.currentFrame];
            VkCommandBufferBeginInfo cbi = VkCommandBufferBeginInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
                .flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
            vkBeginCommandBuffer(cmd, cbi);

            updateUniformBuffer();

            VkClearColorValue ccv = VkClearColorValue.calloc(stack);
            ccv.float32(0, clearColorR);
            ccv.float32(1, clearColorG);
            ccv.float32(2, clearColorB);
            ccv.float32(3, clearColorA);

            VkClearValue colorClear = VkClearValue.calloc(stack).color(ccv);

            VkClearDepthStencilValue dsv = VkClearDepthStencilValue.calloc(stack);
            dsv.depth(1.0f);
            dsv.stencil(0);

            VkClearValue depthClear = VkClearValue.calloc(stack).depthStencil(dsv);

            VkRenderingAttachmentInfo depthAtt = VkRenderingAttachmentInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_RENDERING_ATTACHMENT_INFO)
                .imageView(GL11Util.depthView)
                .imageLayout(VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL)
                .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                .storeOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                .clearValue(depthClear);

            VkRect2D renderArea = VkRect2D.calloc(stack);
            VkOffset2D off = VkOffset2D.calloc(stack).x(0).y(0);
            renderArea.offset(off);
            VkExtent2D ext = VkExtent2D.calloc(stack).width(GL11Util.width).height(GL11Util.height);
            renderArea.extent(ext);

            VkRenderingAttachmentInfo.Buffer colorAtts = VkRenderingAttachmentInfo.malloc(1);
            colorAtts.get(0)
                .sType(VK_STRUCTURE_TYPE_RENDERING_ATTACHMENT_INFO)
                .imageView(GL11Util.swapchainImageViews[GL11Util.currentImageIndex])
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
            vps.get(0).x(viewportX).y(viewportY).width(viewportW).height(viewportH).minDepth(0f).maxDepth(1f);
            vkCmdSetViewport(cmd, 0, vps);

            VkRect2D.Buffer scissors = VkRect2D.malloc(1, stack);
            VkOffset2D scOff = VkOffset2D.calloc(stack).x(scissorX).y(scissorY);
            scissors.get(0).offset(scOff);
            VkExtent2D scExt = VkExtent2D.calloc(stack).width(scissorW).height(scissorH);
            scissors.get(0).extent(scExt);
            vkCmdSetScissor(cmd, 0, scissors);

            if (vertexCount > 0) {
                long dataSize = vertexCount * FLOATS_PER_VERTEX * 4L;
                MemoryUtil.memCopy(MemoryUtil.memAddress(vertexData), GL11Util.stagingPtr, (int)dataSize);

                VkBufferCopy.Buffer copies = VkBufferCopy.malloc(1, stack);
                copies.get(0).srcOffset(0).dstOffset(0).size(dataSize);
                vkCmdCopyBuffer(cmd, GL11Util.stagingBuffer, GL11Util.vertexBuffer, copies);
            }
        }
    }

    public static void endFrame() throws Exception {
        if (!GL11Util.initialized) return;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBuffer cmd = GL11Util.commandBuffers[GL11Util.currentFrame];

            vkCmdEndRendering(cmd);
            vkEndCommandBuffer(cmd);

            PointerBuffer cmdBufs = stack.mallocPointer(1).put(0, cmd.address());

            VkSubmitInfo si = VkSubmitInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                .pWaitSemaphores(stack.mallocLong(1).put(0, GL11Util.imageAvailableSemaphores[GL11Util.currentFrame]))
                .pWaitDstStageMask(stack.mallocInt(1).put(0, VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT))
                .pCommandBuffers(cmdBufs)
                .pSignalSemaphores(stack.mallocLong(1).put(0, GL11Util.renderFinishedSemaphores[GL11Util.currentFrame]));

            vkQueueSubmit(GL11Util.graphicsQueue, si, GL11Util.inFlightFences[GL11Util.currentFrame]);

            VkPresentInfoKHR pi = VkPresentInfoKHR.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
                .pWaitSemaphores(stack.mallocLong(1).put(0, GL11Util.renderFinishedSemaphores[GL11Util.currentFrame]))
                .pSwapchains(stack.mallocLong(1).put(0, GL11Util.swapchain))
                .pImageIndices(stack.mallocInt(1).put(0, GL11Util.currentImageIndex));

            vkQueuePresentKHR(GL11Util.graphicsQueue, pi);

            GL11Util.currentFrame = (GL11Util.currentFrame + 1) % IN_FLIGHT_FRAMES;
        }
    }

    private static void updateUniformBuffer() {
        float[] mv = modelviewStack[modelviewSp];
        float[] pr = projectionStack[projectionSp];
        float[] tx = textureStack[textureSp];

        for (int i = 0; i < 16; i++) {
            MemoryUtil.memPutFloat(GL11Util.uniformPtr + i * 4L, mv[i]);
            MemoryUtil.memPutFloat(GL11Util.uniformPtr + (16 + i) * 4L, pr[i]);
            MemoryUtil.memPutFloat(GL11Util.uniformPtr + (32 + i) * 4L, tx[i]);
        }
    }   

    // ═══════════════════════════════════════════
    //  GL11 METHOD IMPLEMENTATIONS
    // ═══════════════════════════════════════════

    // --- Matrix ---

    public static void glMatrixMode(int mode) {
        currentMatrixMode = mode;
    }

    public static void glLoadIdentity() {
        float[] m = currentMatrix();
        identity(m);
    }

    public static void glLoadMatrixf(FloatBuffer m) {
        float[] cur = currentMatrix();
        for (int i = 0; i < 16; i++) cur[i] = m.get(i);
    }

    public static void glMultMatrixf(FloatBuffer m) {
        float[] cur = currentMatrix();
        float[] result = new float[16];
        multiplyMatrices(cur, m, result);
        System.arraycopy(result, 0, cur, 0, 16);
    }

    public static void glTranslatef(float x, float y, float z) {
        float[] t = {1,0,0,0, 0,1,0,0, 0,0,1,0, x,y,z,1};
        float[] cur = currentMatrix();
        float[] result = new float[16];
        multiplyMatrices(cur, t, result);
        System.arraycopy(result, 0, cur, 0, 16);
    }

    public static void glRotatef(float angle, float x, float y, float z) {
        float rad = (float) Math.toRadians(angle);
        float c = (float) Math.cos(rad);
        float s = (float) Math.sin(rad);
        float t = 1.0f - c;
        float len = (float) Math.sqrt(x*x + y*y + z*z);
        if (len < 1e-6f) return;
        x /= len; y /= len; z /= len;
        float[] r = {
            t*x*x+c,   t*x*y+s*z, t*x*z-s*y, 0,
            t*x*y-s*z, t*y*y+c,   t*y*z+s*x, 0,
            t*x*z+s*y, t*y*z-s*x, t*z*z+c,   0,
            0,         0,         0,         1
        };
        float[] cur = currentMatrix();
        float[] result = new float[16];
        multiplyMatrices(cur, r, result);
        System.arraycopy(result, 0, cur, 0, 16);
    }

    public static void glScalef(float x, float y, float z) {
        float[] s = {x,0,0,0, 0,y,0,0, 0,0,z,0, 0,0,0,1};
        float[] cur = currentMatrix();
        float[] result = new float[16];
        multiplyMatrices(cur, s, result);
        System.arraycopy(result, 0, cur, 0, 16);
    }

    public static void glPushMatrix() {
        if (currentMatrixMode == GL_MODELVIEW) {
            if (modelviewSp < 31) {
                modelviewSp++;
                System.arraycopy(modelviewStack[modelviewSp-1], 0, modelviewStack[modelviewSp], 0, 16);
            }
        } else if (currentMatrixMode == GL_PROJECTION) {
            if (projectionSp < 31) {
                projectionSp++;
                System.arraycopy(projectionStack[projectionSp-1], 0, projectionStack[projectionSp], 0, 16);
            }
        } else if (currentMatrixMode == GL_TEXTURE) {
            if (textureSp < 31) {
                textureSp++;
                System.arraycopy(textureStack[textureSp-1], 0, textureStack[textureSp], 0, 16);
            }
        }
    }

    public static void glPopMatrix() {
        if (currentMatrixMode == GL_MODELVIEW) {
            if (modelviewSp > 0) modelviewSp--;
        } else if (currentMatrixMode == GL_PROJECTION) {
            if (projectionSp > 0) projectionSp--;
        } else if (currentMatrixMode == GL_TEXTURE) {
            if (textureSp > 0) textureSp--;
        }
    }

    public static void glOrtho(double left, double right, double bottom, double top, double zNear, double zFar) {
        float[] m = {
            (float)(2.0 / (right - left)), 0, 0, (float)(-(right + left) / (right - left)),
            0, (float)(2.0 / (top - bottom)), 0, (float)(-(top + bottom) / (top - bottom)),
            0, 0, (float)(-2.0 / (zFar - zNear)), (float)(-(zFar + zNear) / (zFar - zNear)),
            0, 0, 0, 1
        };
        float[] cur = currentMatrix();
        float[] result = new float[16];
        multiplyMatrices(cur, m, result);
        System.arraycopy(result, 0, cur, 0, 16);
    }

    public static void glFrustum(double left, double right, double bottom, double top, double zNear, double zFar) {
        float[] m = {
            (float)(2.0 * zNear / (right - left)), 0, (float)((right + left) / (right - left)), 0,
            0, (float)(2.0 * zNear / (top - bottom)), 0, (float)((top + bottom) / (top - bottom)), 0,
            0, 0, (float)(-(zFar + zNear) / (zFar - zNear)), (float)(-2.0 * zFar * zNear / (zFar - zNear)),
            0, 0, -1, 0
        };
        float[] cur = currentMatrix();
        float[] result = new float[16];
        multiplyMatrices(cur, m, result);
        System.arraycopy(result, 0, cur, 0, 16);
    }

    public static void glPerspective(double fovy, double aspect, double zNear, double zFar) {
        double theta = Math.toRadians(fovy);
        double f = 1.0 / Math.tan(theta / 2.0);
        float[] m = {
            (float)(f / aspect), 0, 0, 0,
            0, (float) f, 0, 0,
            0, 0, (float)(-(zFar + zNear) / (zFar - zNear)), (float)(-2.0 * zFar * zNear / (zFar - zNear)),
            0, 0, -1, 0
        };
        float[] cur = currentMatrix();
        float[] result = new float[16];
        multiplyMatrices(cur, m, result);
        System.arraycopy(result, 0, cur, 0, 16);
    }

    // --- State ---

    public static void glEnable(int cap) {
        if (cap < enabled.length) enabled[cap] = true;
    }

    public static void glDisable(int cap) {
        if (cap < enabled.length) enabled[cap] = false;
    }

    public static boolean glIsEnabled(int cap) {
        return cap < enabled.length && enabled[cap];
    }

    public static void glBlendFunc(int sfactor, int dfactor) {
        blendSrc = sfactor;
        blendDst = dfactor;
    }

    public static void glDepthFunc(int func) {
        depthFunc = func;
    }

    public static void glDepthMask(boolean mask) {
        depthMask = mask;
    }

    public static void glCullFace(int mode) {
        cullFaceMode = mode;
    }

    public static void glAlphaFunc(int func, float ref) {
        alphaFuncMode = func;
        alphaRef = ref;
    }

    public static void glFogf(int pname, float param) {
        switch (pname) {
            case GL_FOG_MODE: fogMode = (int) param; break;
            case GL_FOG_START: fogStart = param; break;
            case GL_FOG_END: fogEnd = param; break;
            case GL_FOG_DENSITY: fogDensity = param; break;
        }
    }

    public static void glFogfv(int pname, FloatBuffer params) {
        switch (pname) {
            case GL_FOG_COLOR:
                fogColorR = params.get(0);
                fogColorG = params.get(1);
                fogColorB = params.get(2);
                if (params.capacity() > 3) fogColorA = params.get(3);
                break;
        }
    }

    public static void glViewport(int x, int y, int w, int h) {
        viewportX = x; viewportY = y; viewportW = w; viewportH = h;
    }

    public static void glScissor(int x, int y, int w, int h) {
        scissorX = x; scissorY = y; scissorW = w; scissorH = h;
    }

    public static void glClearColor(float r, float g, float b, float a) {
        clearColorR = r; clearColorG = g; clearColorB = b; clearColorA = a;
    }

    public static void glClearDepth(double depth) {
        clearDepth = depth;
    }

    public static void glClear(int mask) {
        // Handled in beginFrame via loadOp = CLEAR
    }

    public static void glPolygonOffset(float factor, float units) {
        polygonOffsetFactor = factor;
        polygonOffsetUnits = units;
    }

    public static String glGetString(int name) {
        switch (name) {
            case GL_VENDOR: return "LWJGL";
            case GL_RENDERER: return "Cathode Vulkan";
            case GL_VERSION: return "1.1.0 Cathode";
            case GL_SHADING_LANGUAGE_VERSION: return "1.10";
            default: return "";
        }
    }

    public static void glGetIntegerv(int pname, IntBuffer params) {
        switch (pname) {
            case GL_MAX_TEXTURE_SIZE: params.put(0, 16384); break;
            case GL_DEPTH_BITS: params.put(0, 24); break;
            case GL_VIEWPORT:
                params.put(0, viewportX); params.put(1, viewportY);
                params.put(2, viewportW); params.put(3, viewportH);
                break;
            case GL_ALIASED_POINT_SIZE_RANGE:
                params.put(0, 1); params.put(1, 255);
                break;
            default: params.put(0, 0);
        }
    }

    public static void glGetFloatv(int pname, FloatBuffer params) {
        switch (pname) {
            case GL_CURRENT_COLOR:
                params.put(0, curR); params.put(1, curG);
                params.put(2, curB); params.put(3, curA);
                break;
            case GL_CURRENT_TEXTURE_COORDS:
                params.put(0, curU); params.put(1, curV);
                break;
            case GL_CURRENT_NORMAL:
                params.put(0, curNx); params.put(1, curNy); params.put(2, curNz);
                break;
            default:
                for (int i = 0; i < params.capacity(); i++) params.put(i, 0f);
        }
    }

    // --- Textures ---

    public static void glGenTextures(int n, IntBuffer ids) {
        for (int i = 0; i < n; i++) ids.put(i, nextTextureId++);
    }

    public static void glBindTexture(int target, int texId) {
        boundTexture = texId;
    }

    public static void glDeleteTextures(int n, IntBuffer ids) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            for (int i = 0; i < n; i++) {
                int id = ids.get(i);
                Long img = GL11Util.textureImages.remove(id);
                Long view = GL11Util.textureImageViews.remove(id);
                Long mem = GL11Util.textureMemory.remove(id);
                Long dset = GL11Util.descriptorSets.remove(id);
                if (img != null && img != 0) vkDestroyImage(GL11Util.device, img, null);
                if (view != null && view != 0) vkDestroyImageView(GL11Util.device, view, null);
                if (mem != null && mem != 0) vkFreeMemory(GL11Util.device, mem, null);
                if (dset != null && dset != 0) {
                    LongBuffer sets = stack.mallocLong(1).put(0, dset);
                    vkFreeDescriptorSets(GL11Util.device, GL11Util.descriptorPool, sets);
                }
            }
        }
    }

    public static void glTexImage2D(int target, int level, int internalFormat, int w, int h,
                                     int border, int format, int type, java.nio.ByteBuffer data) {
        uploadTexture(boundTexture, w, h, format, type, data.array());
    }

    public static void glTexImage2D(int target, int level, int internalFormat, int w, int h,
                                     int border, int format, int type, byte[] data) {
        uploadTexture(boundTexture, w, h, format, type, data);
    }

    public static void glTexParameteri(int target, int pname, int param) {
        if (pname < textureParams.length) textureParams[pname] = param;
    }

    // --- Immediate Mode ---

    public static void glBegin(int mode) {
        inBegin = true;
        beginMode = mode;
        vertexCount = 0;
        vertexData.clear();
    }

    public static void glEnd() {
        inBegin = false;
    }

    public static void glVertex3f(float x, float y, float z) {
        writeVertex(x, y, z, 1f);
    }

    public static void glVertex2f(float x, float y) {
        writeVertex(x, y, 0f, 1f);
    }

    public static void glVertex4f(float x, float y, float z, float w) {
        writeVertex(x, y, z, w);
    }

    private static void writeVertex(float x, float y, float z, float w) {
        if (!inBegin || vertexCount >= MAX_VERTICES) return;
        vertexData.put(x);
        vertexData.put(y);
        vertexData.put(z);
        vertexData.put(w);
        vertexData.put(curR);
        vertexData.put(curG);
        vertexData.put(curB);
        vertexData.put(curA);
        vertexData.put(curU);
        vertexData.put(curV);
        vertexData.put(curNx);
        vertexData.put(curNy);
        vertexData.put(curNz);
        vertexCount++;
    }

    public static void glColor3f(float r, float g, float b) {
        curR = r; curG = g; curB = b;
    }

    public static void glColor4f(float r, float g, float b, float a) {
        curR = r; curG = g; curB = b; curA = a;
    }

    public static void glColor3ub(byte r, byte g, byte b) {
        curR = (r & 0xFF) / 255f;
        curG = (g & 0xFF) / 255f;
        curB = (b & 0xFF) / 255f;
    }

    public static void glColor4ub(byte r, byte g, byte b, byte a) {
        curR = (r & 0xFF) / 255f;
        curG = (g & 0xFF) / 255f;
        curB = (b & 0xFF) / 255f;
        curA = (a & 0xFF) / 255f;
    }

    public static void glTexCoord2f(float u, float v) {
        curU = u; curV = v;
    }

    public static void glTexCoord2d(double u, double v) {
        curU = (float) u; curV = (float) v;
    }

    public static void glNormal3f(float x, float y, float z) {
        curNx = x; curNy = y; curNz = z;
    }

    // --- Client State (no-op) ---

    public static void glClientActiveTexture(int which) { }
    public static void glActiveTexture(int which) { }
    public static void glClientVertexPointer(int size, int type, int stride, FloatBuffer buf) { }
    public static void glClientColorPointer(int size, int type, int stride, FloatBuffer buf) { }
    public static void glClientTexCoordPointer(int size, int type, int stride, FloatBuffer buf) { }
    public static void glClientNormalPointer(int type, int stride, FloatBuffer buf) { }
    public static void glArrayElement(int i) { }
    public static void glEnableClientState(int cap) { }
    public static void glDisableClientState(int cap) { }

    // --- Draw ---

    public static void glDrawArrays(int mode, int first, int count) { }
    public static void glDrawElements(int mode, int count, int type, IntBuffer indices) { }

    // --- Misc ---

    public static void glFinish() {
        if (GL11Util.initialized) vkDeviceWaitIdle(GL11Util.device);
    }

    public static void glFlush() { }
    public static void glHint(int target, int mode) { }
    public static void glPixelStorei(int pname, int param) { }
    public static void glShadeModel(int mode) { }
    public static void glLineWidth(float width) { }
    public static void glPointSize(float size) { }
    public static void glFrontFace(int mode) { }
    public static void glLogicOp(int opcode) { }   

    // ═══════════════════════════════════════════
    //  UTILITIES
    // ═══════════════════════════════════════════

    private static float[] currentMatrix() {
        switch (currentMatrixMode) {
            case GL_PROJECTION: return projectionStack[projectionSp];
            case GL_TEXTURE: return textureStack[textureSp];
            default: return modelviewStack[modelviewSp];
        }
    }

    private static void identity(float[] m) {
        java.util.Arrays.fill(m, 0f);
        m[0] = m[5] = m[10] = m[15] = 1f;
    }

    private static void multiplyMatrices(float[] a, float[] b, float[] result) {
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                float sum = 0f;
                for (int k = 0; k < 4; k++) {
                    sum += a[k * 4 + row] * b[col * 4 + k];
                }
                result[col * 4 + row] = sum;
            }
        }
    }

    private static void multiplyMatrices(float[] a, FloatBuffer b, float[] result) {
        float[] bArr = new float[16];
        for (int i = 0; i < 16; i++) bArr[i] = b.get(i);
        multiplyMatrices(a, bArr, result);
    }

    private static int findMemoryType(int typeBits, int props) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkPhysicalDeviceMemoryProperties memProps = VkPhysicalDeviceMemoryProperties.calloc(stack);
            vkGetPhysicalDeviceMemoryProperties(GL11Util.physicalDevice, memProps);
            for (int i = 0; i < memProps.memoryTypeCount(); i++) {
                if ((typeBits & (1 << i)) != 0 &&
                    (memProps.memoryTypes().get(i).propertyFlags() & props) == props) {
                    return i;
                }
            }
        }
        throw new RuntimeException("No suitable memory type found");
    }

    private static byte[] loadSpirV(String name) throws Exception {
        java.io.InputStream is = GL11.class.getResourceAsStream("/shaders/" + name);
        if (is == null) is = GL11.class.getResourceAsStream(name);
        if (is == null) throw new RuntimeException("Shader not found: " + name);
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = is.read(buf)) != -1) bos.write(buf, 0, n);
        is.close();
        return bos.toByteArray();
    }

    public static boolean isInitialized() {
        return GL11Util.initialized;
    }
}   