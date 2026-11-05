package io.github.year0c.cathode.vulkan;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import org.lwjgl.vulkan.*;

import io.github.year0c.cathode.vulkan.util.VulkanUtil;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.system.MemoryUtil.*;

public class SkyPipeline {

    private final VkDevice device;
    private final VkPhysicalDevice physicalDevice;
    private final long renderPass;

    private long descriptorSetLayout;
    private long pipelineLayout;
    private long pipeline;
    private long descriptorPool;
    private long descriptorSet;
    private long uniformBuffer;
    private long uniformMemory;
    private long uniformPtr;

    public SkyPipeline(VkDevice device, VkPhysicalDevice physicalDevice, long renderPass) {
        this.device = device;
        this.physicalDevice = physicalDevice;
        this.renderPass = renderPass;

        createDescriptorSetLayout();
        createPipelineLayout();
        createPipeline();
        createDescriptorPool();
        createUniformBuffer();
    }

    private void createDescriptorSetLayout() {
        VkDescriptorSetLayoutBinding.Buffer bindings = VkDescriptorSetLayoutBinding.calloc(1);
        bindings.get(0)
            .binding(0)
            .descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
            .descriptorCount(1)
            .stageFlags(VK_SHADER_STAGE_FRAGMENT_BIT);

        VkDescriptorSetLayoutCreateInfo info = VkDescriptorSetLayoutCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
            .pBindings(bindings);

        long[] out = new long[1];
        vkCreateDescriptorSetLayout(device, info, null, out);
        this.descriptorSetLayout = out[0];
        info.free();
        bindings.free();
    }

    private void createPipelineLayout() {
        LongBuffer layouts = memAllocLong(1);
        layouts.put(0, descriptorSetLayout);
        layouts.limit(1);

        VkPipelineLayoutCreateInfo info = VkPipelineLayoutCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
            .pSetLayouts(layouts);

        long[] out = new long[1];
        vkCreatePipelineLayout(device, info, null, out);
        this.pipelineLayout = out[0];
        info.free();
        memFree(layouts);
    }

    private void createPipeline() {
        VkPipelineVertexInputStateCreateInfo vertexInput = VkPipelineVertexInputStateCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO);

        VkPipelineInputAssemblyStateCreateInfo inputAssembly = VkPipelineInputAssemblyStateCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO)
            .topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST);

        VkPipelineViewportStateCreateInfo viewportState = VkPipelineViewportStateCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO)
            .viewportCount(1)
            .scissorCount(1);

        VkPipelineRasterizationStateCreateInfo raster = VkPipelineRasterizationStateCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO)
            .depthClampEnable(false)
            .rasterizerDiscardEnable(false)
            .polygonMode(VK_POLYGON_MODE_FILL)
            .cullMode(VK_CULL_MODE_NONE)
            .frontFace(VK_FRONT_FACE_COUNTER_CLOCKWISE)
            .lineWidth(1.0f);

        VkPipelineMultisampleStateCreateInfo msaa = VkPipelineMultisampleStateCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
            .rasterizationSamples(VK_SAMPLE_COUNT_1_BIT)
            .sampleShadingEnable(false);

        VkPipelineDepthStencilStateCreateInfo depthStencil = VkPipelineDepthStencilStateCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO)
            .depthTestEnable(false)
            .depthWriteEnable(false)
            .depthCompareOp(VK_COMPARE_OP_ALWAYS)
            .depthBoundsTestEnable(false)
            .stencilTestEnable(false);

        VkPipelineColorBlendAttachmentState.Buffer attachments = VkPipelineColorBlendAttachmentState.calloc(1);
        attachments.get(0)
            .colorWriteMask(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT);

        VkPipelineColorBlendStateCreateInfo blend = VkPipelineColorBlendStateCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO)
            .logicOpEnable(false)
            .logicOp(VK_LOGIC_OP_COPY)
            .pAttachments(attachments);

        IntBuffer dynBuf = memAllocInt(2);
        dynBuf.put(0, VK_DYNAMIC_STATE_VIEWPORT);
        dynBuf.put(1, VK_DYNAMIC_STATE_SCISSOR);
        dynBuf.limit(2);

        VkPipelineDynamicStateCreateInfo dynamic = VkPipelineDynamicStateCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO)
            .pDynamicStates(dynBuf);

        ByteBuffer entryName = ByteBuffer.allocateDirect(5);
        entryName.put("main".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        entryName.put((byte) 0);
        entryName.position(0);

        VkPipelineShaderStageCreateInfo.Buffer stages = VkPipelineShaderStageCreateInfo.calloc(2);

        stages.get(0)
            .sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
            .stage(VK_SHADER_STAGE_VERTEX_BIT)
            .module(compileShader("sky_vert.spv"))
            .pName(entryName);

        stages.get(1)
            .sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
            .stage(VK_SHADER_STAGE_FRAGMENT_BIT)
            .module(compileShader("sky_frag.spv"))
            .pName(entryName);

        VkGraphicsPipelineCreateInfo.Buffer infos = VkGraphicsPipelineCreateInfo.calloc(1);
        infos.get(0)
            .sType(VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO)
            .pStages(stages)
            .pVertexInputState(vertexInput)
            .pInputAssemblyState(inputAssembly)
            .pViewportState(viewportState)
            .pRasterizationState(raster)
            .pMultisampleState(msaa)
            .pDepthStencilState(depthStencil)
            .pColorBlendState(blend)
            .pDynamicState(dynamic)
            .layout(pipelineLayout)
            .renderPass(renderPass)
            .subpass(0);

        long[] out = new long[1];
        vkCreateGraphicsPipelines(device, VK_NULL_HANDLE, infos, null, out);
        this.pipeline = out[0];

        vkDestroyShaderModule(device, stages.get(0).module(), null);
        vkDestroyShaderModule(device, stages.get(1).module(), null);

        memFree(dynBuf);
        dynamic.free();
        blend.free();
        attachments.free();
        depthStencil.free();
        msaa.free();
        raster.free();
        viewportState.free();
        inputAssembly.free();
        vertexInput.free();
        stages.free();
        infos.free();
    }

    private void createDescriptorPool() {
        VkDescriptorPoolSize.Buffer sizes = VkDescriptorPoolSize.calloc(1);
        sizes.get(0)
            .type(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
            .descriptorCount(1);

        VkDescriptorPoolCreateInfo info = VkDescriptorPoolCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO)
            .maxSets(1)
            .pPoolSizes(sizes);

        long[] out = new long[1];
        vkCreateDescriptorPool(device, info, null, out);
        this.descriptorPool = out[0];
        info.free();
        sizes.free();
    }

    private static final int UNIFORM_SIZE = 16 * 4;

    private void createUniformBuffer() {
        VkBufferCreateInfo bufInfo = VkBufferCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
            .size(UNIFORM_SIZE)
            .usage(VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT)
            .sharingMode(VK_SHARING_MODE_EXCLUSIVE);

        long[] bufOut = new long[1];
        vkCreateBuffer(device, bufInfo, null, bufOut);
        this.uniformBuffer = bufOut[0];
        bufInfo.free();

        VkMemoryRequirements req = VkMemoryRequirements.calloc();
        vkGetBufferMemoryRequirements(device, uniformBuffer, req);

        int memType = VulkanUtil.findMemoryType(physicalDevice, req.memoryTypeBits(),
            VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);

        VkMemoryAllocateInfo alloc = VkMemoryAllocateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
            .allocationSize(req.size())
            .memoryTypeIndex(memType);

        long[] memOut = new long[1];
        vkAllocateMemory(device, alloc, null, memOut);
        this.uniformMemory = memOut[0];
        alloc.free();
        req.free();

        vkBindBufferMemory(device, uniformBuffer, uniformMemory, 0);

        try (org.lwjgl.system.MemoryStack stack = org.lwjgl.system.MemoryStack.stackPush()) {
            org.lwjgl.PointerBuffer ptr = stack.callocPointer(1);
            vkMapMemory(device, uniformMemory, 0, UNIFORM_SIZE, 0, ptr);
            this.uniformPtr = ptr.get(0);
        }

        LongBuffer layouts = memAllocLong(1);
        layouts.put(0, descriptorSetLayout);
        layouts.limit(1);

        VkDescriptorSetAllocateInfo allocInfo = VkDescriptorSetAllocateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO)
            .descriptorPool(descriptorPool)
            .pSetLayouts(layouts);

        long[] setOut = new long[1];
        vkAllocateDescriptorSets(device, allocInfo, setOut);
        this.descriptorSet = setOut[0];
        allocInfo.free();
        memFree(layouts);

        VkDescriptorBufferInfo.Buffer descBufInfo = VkDescriptorBufferInfo.calloc(1);
        descBufInfo.get(0)
            .buffer(uniformBuffer)
            .offset(0)
            .range(UNIFORM_SIZE);

        VkWriteDescriptorSet.Buffer writes = VkWriteDescriptorSet.calloc(1);
        writes.get(0)
            .sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
            .dstSet(descriptorSet)
            .dstBinding(0)
            .descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
            .descriptorCount(1)
            .pBufferInfo(descBufInfo);

        vkUpdateDescriptorSets(device, writes, null);
        writes.free();
        descBufInfo.free();
    }

    public void updateUniforms(float[] zenith, float[] horizon, float[] sunDir,
                               float starAlpha, float camX, float camY, float camZ,
                               float fov, float aspect) {
        memPutFloat(uniformPtr + 0, zenith[0]);
        memPutFloat(uniformPtr + 4, zenith[1]);
        memPutFloat(uniformPtr + 8, zenith[2]);
        memPutFloat(uniformPtr + 12, horizon[0]);
        memPutFloat(uniformPtr + 16, horizon[1]);
        memPutFloat(uniformPtr + 20, horizon[2]);
        memPutFloat(uniformPtr + 24, sunDir[0]);
        memPutFloat(uniformPtr + 28, sunDir[1]);
        memPutFloat(uniformPtr + 32, sunDir[2]);
        memPutFloat(uniformPtr + 36, starAlpha);
        memPutFloat(uniformPtr + 40, camX);
        memPutFloat(uniformPtr + 44, camY);
        memPutFloat(uniformPtr + 48, camZ);
        memPutFloat(uniformPtr + 52, fov);
        memPutFloat(uniformPtr + 56, aspect);
    }

    public long getPipeline()       { return pipeline; }
    public long getPipelineLayout() { return pipelineLayout; }
    public long getDescriptorSet()  { return descriptorSet; }

    private long compileShader(String path) {
        byte[] spirv;
        try (java.io.InputStream is = getClass().getResourceAsStream("/shaders/" + path)) {
            if (is == null) throw new RuntimeException("Missing shader: " + path);
            spirv = is.readAllBytes();
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to load shader: " + path, e);
        }

        ByteBuffer code = ByteBuffer.allocateDirect(spirv.length);
        code.put(spirv);
        code.position(0);
        code.limit(spirv.length);

        VkShaderModuleCreateInfo info = VkShaderModuleCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
            .pCode(code);

        long[] out = new long[1];
        vkCreateShaderModule(device, info, null, out);
        info.free();
        return out[0];
    }

    public void close() {
        vkDestroyPipeline(device, pipeline, null);
        vkDestroyDescriptorPool(device, descriptorPool, null);
        vkDestroyPipelineLayout(device, pipelineLayout, null);
        vkDestroyDescriptorSetLayout(device, descriptorSetLayout, null);
        vkUnmapMemory(device, uniformMemory);
        vkFreeMemory(device, uniformMemory, null);
        vkDestroyBuffer(device, uniformBuffer, null);
    }
}   