package io.github.year0c.cathode.vulkan;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import org.lwjgl.vulkan.*;

import io.github.year0c.cathode.vulkan.util.VulkanUtil;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.system.MemoryUtil.*;

public class WorldPipeline {

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

    private static final int UNIFORM_SIZE = 96; // mat4(64) + vec3(12) + float(4) + vec3(12) + float(4)

    public WorldPipeline(VkDevice device, VkPhysicalDevice physicalDevice, long renderPass) {
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
        VkDescriptorSetLayoutBinding.Buffer bindings = VkDescriptorSetLayoutBinding.calloc(3);

        bindings.get(0)
            .binding(0)
            .descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
            .descriptorCount(1)
            .stageFlags(VK_SHADER_STAGE_FRAGMENT_BIT);

        bindings.get(1)
            .binding(1)
            .descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
            .descriptorCount(1)
            .stageFlags(VK_SHADER_STAGE_FRAGMENT_BIT);

        bindings.get(2)
            .binding(2)
            .descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
            .descriptorCount(1)
            .stageFlags(VK_SHADER_STAGE_VERTEX_BIT | VK_SHADER_STAGE_FRAGMENT_BIT);

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
        VkVertexInputBindingDescription.Buffer vibs = VkVertexInputBindingDescription.calloc(1);
        vibs.get(0)
            .binding(0)
            .stride(44)
            .inputRate(VK_VERTEX_INPUT_RATE_VERTEX);

        VkVertexInputAttributeDescription.Buffer vias = VkVertexInputAttributeDescription.calloc(4);
        vias.get(0)
            .location(0)
            .binding(0)
            .format(VK_FORMAT_R32G32B32_SFLOAT)
            .offset(0);
        vias.get(1)
            .location(1)
            .binding(0)
            .format(VK_FORMAT_R32G32B32A32_SFLOAT)
            .offset(12);
        vias.get(2)
            .location(2)
            .binding(0)
            .format(VK_FORMAT_R32G32_SFLOAT)
            .offset(28);
        vias.get(3)
            .location(3)
            .binding(0)
            .format(VK_FORMAT_R32G32_SFLOAT)
            .offset(36);

        VkPipelineVertexInputStateCreateInfo vertexInput = VkPipelineVertexInputStateCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
            .pVertexBindingDescriptions(vibs)
            .pVertexAttributeDescriptions(vias);

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
            .cullMode(VK_CULL_MODE_BACK_BIT)
            .frontFace(VK_FRONT_FACE_COUNTER_CLOCKWISE)
            .lineWidth(1.0f);

        VkPipelineMultisampleStateCreateInfo msaa = VkPipelineMultisampleStateCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
            .rasterizationSamples(VK_SAMPLE_COUNT_1_BIT)
            .sampleShadingEnable(false);

        VkPipelineDepthStencilStateCreateInfo depthStencil = VkPipelineDepthStencilStateCreateInfo.calloc()
            .sType(VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO)
            .depthTestEnable(true)
            .depthWriteEnable(true)
            .depthCompareOp(VK_COMPARE_OP_LESS_OR_EQUAL)
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

        IntBuffer dynBuf = memAllocInt(3);
        dynBuf.put(0, VK_DYNAMIC_STATE_VIEWPORT);
        dynBuf.put(1, VK_DYNAMIC_STATE_SCISSOR);
        dynBuf.put(2, VK_DYNAMIC_STATE_LINE_WIDTH);
        dynBuf.limit(3);

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
            .module(compileShader("world_vert.spv"))
            .pName(entryName);

        stages.get(1)
            .sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
            .stage(VK_SHADER_STAGE_FRAGMENT_BIT)
            .module(compileShader("world_frag.spv"))
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
        vias.free();
        vibs.free();
        stages.free();
        infos.free();
    }

    private void createDescriptorPool() {
        VkDescriptorPoolSize.Buffer sizes = VkDescriptorPoolSize.calloc(2);
        sizes.get(0)
            .type(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
            .descriptorCount(2);
        sizes.get(1)
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
    }

    public void initDescriptorSet(long iconImageView, long iconSampler,
        long lightmapImageView, long lightmapSampler) {
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

        VkDescriptorImageInfo.Buffer iconInfos = VkDescriptorImageInfo.calloc(1);
        iconInfos.get(0)
            .imageView(iconImageView)
            .sampler(iconSampler)
            .imageLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);

        VkDescriptorImageInfo.Buffer lightmapInfos = VkDescriptorImageInfo.calloc(1);
        lightmapInfos.get(0)
            .imageView(lightmapImageView)
            .sampler(lightmapSampler)
            .imageLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);

        VkDescriptorBufferInfo.Buffer bufInfo = VkDescriptorBufferInfo.calloc(1);
        bufInfo.get(0)
            .buffer(uniformBuffer)
            .offset(0)
            .range(UNIFORM_SIZE);

        VkWriteDescriptorSet.Buffer writes = VkWriteDescriptorSet.calloc(3);
        writes.get(0)
            .sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
            .dstSet(descriptorSet)
            .dstBinding(0)
            .descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
            .descriptorCount(1)
            .pImageInfo(iconInfos);
        writes.get(1)
            .sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
            .dstSet(descriptorSet)
            .dstBinding(1)
            .descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
            .descriptorCount(1)
            .pImageInfo(lightmapInfos);
        writes.get(2)
            .sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
            .dstSet(descriptorSet)
            .dstBinding(2)
            .descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
            .descriptorCount(1)
            .pBufferInfo(bufInfo);

        vkUpdateDescriptorSets(device, writes, null);
        writes.free();
        iconInfos.free();
        lightmapInfos.free();
        bufInfo.free();
    }

    /**
     * Update the 96-byte uniform buffer.
     * Layout: mat4 viewProjection(64) | vec3 fogColor(12) | float fogDensity(4) | vec3 camPos(12) | float time(4)
     */
    public void updateUniforms(float[] mvp, float[] fogColor, float fogDensity,
                               float camX, float camY, float camZ, float time) {
        for (int i = 0; i < 16; i++)
            memPutFloat(uniformPtr + i * 4, mvp[i]);
        memPutFloat(uniformPtr + 64, fogColor[0]);
        memPutFloat(uniformPtr + 68, fogColor[1]);
        memPutFloat(uniformPtr + 72, fogColor[2]);
        memPutFloat(uniformPtr + 76, fogDensity);
        memPutFloat(uniformPtr + 80, camX);
        memPutFloat(uniformPtr + 84, camY);
        memPutFloat(uniformPtr + 88, camZ);
        memPutFloat(uniformPtr + 92, time);
    }

    public long getPipeline()       { return pipeline; }
    public long getPipelineLayout() { return pipelineLayout; }
    public long getDescriptorSet()  { return descriptorSet; }

    private long compileShader(String path) {
        byte[] spirv = loadSpirv(path);

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

    private byte[] loadSpirv(String path) {
        try (java.io.InputStream is = getClass().getResourceAsStream("/shaders/" + path)) {
            if (is == null) throw new RuntimeException("Missing shader: " + path);
            return is.readAllBytes();
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to load shader: " + path, e);
        }
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