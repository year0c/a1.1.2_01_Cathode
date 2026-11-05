package org.lwjgl.opengl.func.GL11;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.util.GL11.*;
import org.lwjgl.vulkan.*;
import org.lwjgl.sdl.SDLVulkan;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK12.VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK13.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.opengl.util.GL11.GL11Constants.*;
import static org.lwjgl.vulkan.KHRSurface.*;

public class GL11InitFunc {

    private static final int IN_FLIGHT_FRAMES = 3;

    public static void initVulkan(long sdlWindow, int w, int h) throws Exception {
        GL11Vars.log("initVulkan start: " + w + "x" + h);
        GL11Vars.width = w;
        GL11Vars.height = h;
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
        GL11Vars.initialized = true;
        GL11Vars.log("initVulkan done: device=" + GL11Vars.device
            + " renderPass=" + GL11Vars.renderPass
            + " icon=" + GL11Vars.iconImageView
            + " framebuffers=" + GL11Vars.swapchainFramebuffers.length);
    }

    private static void createInstance() throws Exception {
        GL11Vars.log("createInstance");
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
            GL11Vars.instance = new VkInstance(instPtr.get(0), ci);
            GL11Vars.log("  instance=" + GL11Vars.instance);
        }
    }

    private static void createSurface(long sdlWindow) throws Exception {
        GL11Vars.log("createSurface");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer surfBuf = stack.mallocLong(1);
            boolean ok = SDLVulkan.SDL_Vulkan_CreateSurface(sdlWindow, GL11Vars.instance, null, surfBuf);
            if (!ok) throw new RuntimeException("SDL_Vulkan_CreateSurface failed");
            GL11Vars.surface = surfBuf.get(0);
            GL11Vars.log("  surface=" + GL11Vars.surface);
        }
    }

    private static void pickPhysicalDevice() {
        GL11Vars.log("pickPhysicalDevice");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer count = stack.mallocInt(1).put(0, 0);
            vkEnumeratePhysicalDevices(GL11Vars.instance, count, null);
            PointerBuffer devices = stack.mallocPointer(count.get(0));
            vkEnumeratePhysicalDevices(GL11Vars.instance, count, devices);
            GL11Vars.physicalDevice = new VkPhysicalDevice(devices.get(0), GL11Vars.instance);
            GL11Vars.log("  physicalDevice=" + GL11Vars.physicalDevice);
        }
    }

    private static void createDevice() throws Exception {
        GL11Vars.log("createDevice");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer qCount = stack.mallocInt(1).put(0, 0);
            vkGetPhysicalDeviceQueueFamilyProperties(GL11Vars.physicalDevice, qCount, null);
            VkQueueFamilyProperties.Buffer families = VkQueueFamilyProperties.malloc(qCount.get(0));
            vkGetPhysicalDeviceQueueFamilyProperties(GL11Vars.physicalDevice, qCount, families);
            GL11Vars.gfxFamily = 0;
            for (int i = 0; i < qCount.get(0); i++) {
                if ((families.get(i).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) { GL11Vars.gfxFamily = i; break; }
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
                .queueFamilyIndex(GL11Vars.gfxFamily)
                .pQueuePriorities(stack.mallocFloat(1).put(0, 1.0f));

            VkDeviceCreateInfo dci = VkDeviceCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                .pNext(v13)
                .pQueueCreateInfos(qcis)
                .ppEnabledExtensionNames(devExtPtrs);

            PointerBuffer devPtr = stack.mallocPointer(1);
            int result = vkCreateDevice(GL11Vars.physicalDevice, dci, null, devPtr);
            if (result != VK_SUCCESS) throw new RuntimeException("vkCreateDevice: " + result);
            GL11Vars.device = new VkDevice(devPtr.get(0), GL11Vars.physicalDevice, dci, VK_API_VERSION_1_3);
            PointerBuffer qPtr = stack.mallocPointer(1);
            vkGetDeviceQueue(GL11Vars.device, GL11Vars.gfxFamily, 0, qPtr);
            GL11Vars.graphicsQueue = new VkQueue(qPtr.get(0), GL11Vars.device);
            GL11Vars.log("  device=" + GL11Vars.device + " gfxFamily=" + GL11Vars.gfxFamily);
        }
    }

    private static void createCommandPool() {
        GL11Vars.log("createCommandPool");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandPoolCreateInfo ci = VkCommandPoolCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
                .flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT)
                .queueFamilyIndex(GL11Vars.gfxFamily);
            LongBuffer poolPtr = stack.mallocLong(1);
            vkCreateCommandPool(GL11Vars.device, ci, null, poolPtr);
            GL11Vars.commandPool = poolPtr.get(0);
            GL11Vars.log("  commandPool=" + GL11Vars.commandPool);
        }
    }

    private static void createSwapchain() throws Exception {
        GL11Vars.log("createSwapchain");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkSurfaceCapabilitiesKHR caps = VkSurfaceCapabilitiesKHR.calloc(stack);
            vkGetPhysicalDeviceSurfaceCapabilitiesKHR(GL11Vars.physicalDevice, GL11Vars.surface, caps);

            int fmtFormat = VK_FORMAT_B8G8R8A8_SRGB;
            int fmtColorSpace = VK_COLOR_SPACE_SRGB_NONLINEAR_KHR;

            int extW = (int) caps.currentExtent().width();
            int extH = (int) caps.currentExtent().height();
            if (extW == 0xFFFFFFFF) { extW = GL11Vars.width; extH = GL11Vars.height; }

            int imgCount = caps.minImageCount() + 1;
            if (caps.maxImageCount() > 0 && imgCount > caps.maxImageCount()) imgCount = caps.maxImageCount();

            VkExtent2D extent = VkExtent2D.calloc(stack).width(extW).height(extH);
            VkSwapchainCreateInfoKHR sci = VkSwapchainCreateInfoKHR.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                .surface(GL11Vars.surface)
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
            int result = vkCreateSwapchainKHR(GL11Vars.device, sci, null, swapPtr);
            if (result != VK_SUCCESS) throw new RuntimeException("vkCreateSwapchainKHR: " + result);
            GL11Vars.swapchain = swapPtr.get(0);
            GL11Vars.swapchainFormat = fmtFormat;
            GL11Vars.width = extW;
            GL11Vars.height = extH;

            IntBuffer imgCountBuf = stack.mallocInt(1).put(0, 0);
            vkGetSwapchainImagesKHR(GL11Vars.device, GL11Vars.swapchain, imgCountBuf, null);
            GL11Vars.swapchainImageCount = imgCountBuf.get(0);
            GL11Vars.swapchainImages = new long[GL11Vars.swapchainImageCount];
            LongBuffer imgBuf = stack.mallocLong(GL11Vars.swapchainImageCount);
            vkGetSwapchainImagesKHR(GL11Vars.device, GL11Vars.swapchain, imgCountBuf, imgBuf);
            for (int i = 0; i < GL11Vars.swapchainImageCount; i++)
                GL11Vars.swapchainImages[i] = imgBuf.get(i);

            GL11Vars.swapchainImageViews = new long[GL11Vars.swapchainImageCount];
            for (int i = 0; i < GL11Vars.swapchainImageCount; i++) {
                VkImageViewCreateInfo ivci = VkImageViewCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                    .image(GL11Vars.swapchainImages[i])
                    .viewType(VK_IMAGE_VIEW_TYPE_2D)
                    .format(GL11Vars.swapchainFormat)
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
                GL11Vars.swapchainImageViews[i] = viewPtr.get(0);
            }
            GL11Vars.log("  swapchain=" + GL11Vars.swapchain + " images=" + GL11Vars.swapchainImageCount + " extent=" + extW + "x" + extH);
        }
    }

    private static void createDepthResources() throws Exception {
        GL11Vars.log("createDepthResources");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkImageCreateInfo ici = VkImageCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
                .imageType(VK_IMAGE_TYPE_2D)
                .format(VK_FORMAT_D32_SFLOAT)
                .extent(e -> e.width(GL11Vars.width).height(GL11Vars.height).depth(1))
                .mipLevels(1).arrayLayers(1).samples(VK_SAMPLE_COUNT_1_BIT)
                .tiling(VK_IMAGE_TILING_OPTIMAL)
                .usage(VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT)
                .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            LongBuffer imgPtr = stack.mallocLong(1);
            vkCreateImage(GL11Vars.device, ici, null, imgPtr);
            GL11Vars.depthImage = imgPtr.get(0);

            VkMemoryRequirements mr = VkMemoryRequirements.calloc(stack);
            vkGetImageMemoryRequirements(GL11Vars.device, GL11Vars.depthImage, mr);
            VkMemoryAllocateInfo mai = VkMemoryAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(mr.size())
                .memoryTypeIndex(GL11UtilFunc.findMemoryType(mr.memoryTypeBits(), VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT));
            LongBuffer memPtr = stack.mallocLong(1);
            vkAllocateMemory(GL11Vars.device, mai, null, memPtr);
            GL11Vars.depthMemory = memPtr.get(0);
            vkBindImageMemory(GL11Vars.device, GL11Vars.depthImage, GL11Vars.depthMemory, 0);

            VkImageViewCreateInfo ivci = VkImageViewCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                .image(GL11Vars.depthImage)
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
            vkCreateImageView(GL11Vars.device, ivci, null, viewPtr);
            GL11Vars.depthView = viewPtr.get(0);
            GL11Vars.log("  depthImage=" + GL11Vars.depthImage + " depthView=" + GL11Vars.depthView);
        }
    }

    private static void createSyncObjects() {
        GL11Vars.log("createSyncObjects");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL11Vars.inFlightFences = new long[IN_FLIGHT_FRAMES];
            GL11Vars.imageAvailableSemaphores = new long[IN_FLIGHT_FRAMES];
            GL11Vars.renderFinishedSemaphores = new long[IN_FLIGHT_FRAMES];
            VkFenceCreateInfo fci = VkFenceCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
                .flags(VK_FENCE_CREATE_SIGNALED_BIT);
            VkSemaphoreCreateInfo sci = VkSemaphoreCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);
            LongBuffer ptr = stack.mallocLong(1);
            for (int i = 0; i < IN_FLIGHT_FRAMES; i++) {
                vkCreateFence(GL11Vars.device, fci, null, ptr);
                GL11Vars.inFlightFences[i] = ptr.get(0);
                vkCreateSemaphore(GL11Vars.device, sci, null, ptr);
                GL11Vars.imageAvailableSemaphores[i] = ptr.get(0);
                vkCreateSemaphore(GL11Vars.device, sci, null, ptr);
                GL11Vars.renderFinishedSemaphores[i] = ptr.get(0);
            }
            GL11Vars.log("  " + IN_FLIGHT_FRAMES + " sync object sets created");
        }
    }

    private static void createCommandBuffers() {
        GL11Vars.log("createCommandBuffers");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferAllocateInfo cai = VkCommandBufferAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                .commandPool(GL11Vars.commandPool)
                .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                .commandBufferCount(IN_FLIGHT_FRAMES);
            PointerBuffer cmdBuf = stack.mallocPointer(IN_FLIGHT_FRAMES);
            vkAllocateCommandBuffers(GL11Vars.device, cai, cmdBuf);
            GL11Vars.commandBuffers = new VkCommandBuffer[IN_FLIGHT_FRAMES];
            for (int i = 0; i < IN_FLIGHT_FRAMES; i++)
                GL11Vars.commandBuffers[i] = new VkCommandBuffer(cmdBuf.get(i), GL11Vars.device);
            GL11Vars.log("  " + IN_FLIGHT_FRAMES + " command buffers allocated");
        }
    }

    private static void createStagingBuffer() {
        GL11Vars.log("createStagingBuffer");
        long size = GL11State.MAX_VERTICES * GL11State.FLOATS_PER_VERTEX * 4L;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCreateInfo bci = VkBufferCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                .size(size)
                .usage(VK_BUFFER_USAGE_TRANSFER_SRC_BIT)
                .sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            LongBuffer bufPtr = stack.mallocLong(1);
            vkCreateBuffer(GL11Vars.device, bci, null, bufPtr);
            GL11Vars.stagingBuffer = bufPtr.get(0);

            VkMemoryRequirements mr = VkMemoryRequirements.calloc(stack);
            vkGetBufferMemoryRequirements(GL11Vars.device, GL11Vars.stagingBuffer, mr);
            VkMemoryAllocateInfo mai = VkMemoryAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(mr.size())
                .memoryTypeIndex(GL11UtilFunc.findMemoryType(mr.memoryTypeBits(), VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT));
            LongBuffer memPtr = stack.mallocLong(1);
            vkAllocateMemory(GL11Vars.device, mai, null, memPtr);
            GL11Vars.stagingMemory = memPtr.get(0);
            vkBindBufferMemory(GL11Vars.device, GL11Vars.stagingBuffer, GL11Vars.stagingMemory, 0);
            PointerBuffer mapped = stack.mallocPointer(1);
            vkMapMemory(GL11Vars.device, GL11Vars.stagingMemory, 0, size, 0, mapped);
            GL11Vars.stagingPtr = mapped.get(0);
            GL11Vars.log("  stagingBuffer=" + GL11Vars.stagingBuffer + " size=" + size);
        }
    }

    private static void createVertexBuffer() {
        GL11Vars.log("createVertexBuffer");
        long size = GL11State.MAX_VERTICES * GL11State.FLOATS_PER_VERTEX * 4L;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCreateInfo bci = VkBufferCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                .size(size)
                .usage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT)
                .sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            LongBuffer bufPtr = stack.mallocLong(1);
            vkCreateBuffer(GL11Vars.device, bci, null, bufPtr);
            GL11Vars.vertexBuffer = bufPtr.get(0);

            VkMemoryRequirements mr = VkMemoryRequirements.calloc(stack);
            vkGetBufferMemoryRequirements(GL11Vars.device, GL11Vars.vertexBuffer, mr);
            VkMemoryAllocateInfo mai = VkMemoryAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(mr.size())
                .memoryTypeIndex(GL11UtilFunc.findMemoryType(mr.memoryTypeBits(), VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT));
            LongBuffer memPtr = stack.mallocLong(1);
            vkAllocateMemory(GL11Vars.device, mai, null, memPtr);
            GL11Vars.vertexMemory = memPtr.get(0);
            vkBindBufferMemory(GL11Vars.device, GL11Vars.vertexBuffer, GL11Vars.vertexMemory, 0);
            GL11Vars.log("  vertexBuffer=" + GL11Vars.vertexBuffer + " size=" + size);
        }
    }

    private static void createUniformBuffer() {
        GL11Vars.log("createUniformBuffer");
        long size = 256;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCreateInfo bci = VkBufferCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                .size(size)
                .usage(VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT)
                .sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            LongBuffer bufPtr = stack.mallocLong(1);
            vkCreateBuffer(GL11Vars.device, bci, null, bufPtr);
            GL11Vars.uniformBuffer = bufPtr.get(0);

            VkMemoryRequirements mr = VkMemoryRequirements.calloc(stack);
            vkGetBufferMemoryRequirements(GL11Vars.device, GL11Vars.uniformBuffer, mr);
            VkMemoryAllocateInfo mai = VkMemoryAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(mr.size())
                .memoryTypeIndex(GL11UtilFunc.findMemoryType(mr.memoryTypeBits(), VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT));
            LongBuffer memPtr = stack.mallocLong(1);
            vkAllocateMemory(GL11Vars.device, mai, null, memPtr);
            GL11Vars.uniformMemory = memPtr.get(0);
            vkBindBufferMemory(GL11Vars.device, GL11Vars.uniformBuffer, GL11Vars.uniformMemory, 0);
            PointerBuffer mapped = stack.mallocPointer(1);
            vkMapMemory(GL11Vars.device, GL11Vars.uniformMemory, 0, size, 0, mapped);
            GL11Vars.uniformPtr = mapped.get(0);
            GL11Vars.log("  uniformBuffer=" + GL11Vars.uniformBuffer);
        }
    }

    private static void createShaders() throws Exception {
        GL11Vars.log("createShaders");
        byte[] vertSpv = GL11UtilFunc.loadSpirV("vert.spv");
        byte[] fragSpv = GL11UtilFunc.loadSpirV("frag.spv");

        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer vCode = stack.malloc(vertSpv.length);
            vCode.put(vertSpv);
            VkShaderModuleCreateInfo vsci = VkShaderModuleCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                .pCode(vCode);
            LongBuffer vPtr = stack.mallocLong(1);
            int vr = vkCreateShaderModule(GL11Vars.device, vsci, null, vPtr);
            if (vr != VK_SUCCESS) throw new RuntimeException("vkCreateShaderModule(vert): " + vr);
            GL11Vars.vertShader = vPtr.get(0);

            ByteBuffer fCode = stack.malloc(fragSpv.length);
            fCode.put(fragSpv);
            VkShaderModuleCreateInfo fsci = VkShaderModuleCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                .pCode(fCode);
            LongBuffer fPtr = stack.mallocLong(1);
            int fr = vkCreateShaderModule(GL11Vars.device, fsci, null, fPtr);
            if (fr != VK_SUCCESS) throw new RuntimeException("vkCreateShaderModule(frag): " + fr);
            GL11Vars.fragShader = fPtr.get(0);
            GL11Vars.log("  vertShader=" + GL11Vars.vertShader + " fragShader=" + GL11Vars.fragShader);
        }
    }

    private static void createPipelineLayout() throws Exception {
        GL11Vars.log("createPipelineLayout");
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
            vkCreateDescriptorSetLayout(GL11Vars.device, dslci, null, dslPtr);
            GL11Vars.descriptorSetLayout = dslPtr.get(0);

            LongBuffer layoutPtrs = stack.mallocLong(1).put(0, GL11Vars.descriptorSetLayout);
            VkPipelineLayoutCreateInfo plci = VkPipelineLayoutCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                .setLayoutCount(1)
                .pSetLayouts(layoutPtrs);
            LongBuffer plPtr = stack.mallocLong(1);
            vkCreatePipelineLayout(GL11Vars.device, plci, null, plPtr);
            GL11Vars.pipelineLayout = plPtr.get(0);

            VkDescriptorPoolSize.Buffer sizes = VkDescriptorPoolSize.malloc(2);
            sizes.get(0).type(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER).descriptorCount(64);
            sizes.get(1).type(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER).descriptorCount(64);

            VkDescriptorPoolCreateInfo dpci = VkDescriptorPoolCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO)
                .maxSets(64)
                .pPoolSizes(sizes);
            LongBuffer dpPtr = stack.mallocLong(1);
            vkCreateDescriptorPool(GL11Vars.device, dpci, null, dpPtr);
            GL11Vars.descriptorPool = dpPtr.get(0);
            GL11Vars.log("  pipelineLayout=" + GL11Vars.pipelineLayout + " descriptorPool=" + GL11Vars.descriptorPool);
        }
    }

    private static void createSampler() throws Exception {
        GL11Vars.log("createSampler");
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
            vkCreateSampler(GL11Vars.device, sci, null, sPtr);
            GL11Vars.sampler = sPtr.get(0);
            GL11Vars.log("  sampler=" + GL11Vars.sampler);
        }
    }

    private static void createRenderPass() {
        GL11Vars.log("createRenderPass");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkAttachmentDescription.Buffer atts = VkAttachmentDescription.malloc(2);
            atts.get(0)
                .format(GL11Vars.swapchainFormat)
                .samples(VK_SAMPLE_COUNT_1_BIT)
                .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                .stencilStoreOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                .finalLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);
            atts.get(1)
                .format(VK_FORMAT_D32_SFLOAT)
                .samples(VK_SAMPLE_COUNT_1_BIT)
                .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                .storeOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                .stencilStoreOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                .finalLayout(VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL);

            VkAttachmentReference.Buffer colorRefs = VkAttachmentReference.malloc(1, stack);
            colorRefs.get(0).attachment(0).layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

            VkAttachmentReference depthRef = VkAttachmentReference.calloc(stack)
                .attachment(1).layout(VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL);

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
            int result = vkCreateRenderPass(GL11Vars.device, rpci, null, ptr);
            if (result != VK_SUCCESS) throw new RuntimeException("vkCreateRenderPass: " + result);
            GL11Vars.renderPass = ptr.get(0);

            GL11Vars.swapchainFramebuffers = new long[GL11Vars.swapchainImageCount];
            for (int i = 0; i < GL11Vars.swapchainImageCount; i++) {
                LongBuffer views = stack.mallocLong(2);
                views.put(0, GL11Vars.swapchainImageViews[i]);
                views.put(1, GL11Vars.depthView);
                VkFramebufferCreateInfo fci = VkFramebufferCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                    .renderPass(GL11Vars.renderPass)
                    .width(GL11Vars.width).height(GL11Vars.height).layers(1)
                    .pAttachments(views);
                LongBuffer fbPtr = stack.mallocLong(1);
                int fbResult = vkCreateFramebuffer(GL11Vars.device, fci, null, fbPtr);
                if (fbResult != VK_SUCCESS) throw new RuntimeException("vkCreateFramebuffer[" + i + "]: " + fbResult);
                GL11Vars.swapchainFramebuffers[i] = fbPtr.get(0);
            }
            GL11Vars.log("  renderPass=" + GL11Vars.renderPass + " framebuffers=" + GL11Vars.swapchainFramebuffers.length);
        }
    }

    private static void createIcon() {
        GL11Vars.log("createIcon");
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
            int ir = vkCreateImage(GL11Vars.device, ici, null, imgPtr);
            if (ir != VK_SUCCESS) throw new RuntimeException("vkCreateImage(icon): " + ir);
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
                .format(VK_FORMAT_R8G8B8_UNORM)
                .components(c -> c.r(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .g(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .b(VK_COMPONENT_SWIZZLE_IDENTITY)
                    .a(VK_COMPONENT_SWIZZLE_IDENTITY))
                .subresourceRange(r -> r.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    .levelCount(1).layerCount(1));
            LongBuffer viewPtr = stack.mallocLong(1);
            vkCreateImageView(GL11Vars.device, ivci, null, viewPtr);
            GL11Vars.iconImageView = viewPtr.get(0);
            GL11Vars.iconSampler = GL11Vars.sampler;
            GL11Vars.log("  iconImageView=" + GL11Vars.iconImageView);
        }
    }
}   