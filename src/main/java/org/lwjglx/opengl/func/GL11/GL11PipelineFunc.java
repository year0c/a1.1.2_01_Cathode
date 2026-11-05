package org.lwjglx.opengl.func.GL11;

import org.lwjglx.opengl.util.GL11.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjglx.opengl.util.GL11.GL11Constants.*;

public class GL11PipelineFunc {

    public static long getOrCreatePipeline(int topology) {
        int cull = 0;
        if (GL11State.enabled[GL_CULL_FACE]) {
            if (GL11State.cullFaceMode == GL_BACK) cull = 1;
            else if (GL11State.cullFaceMode == GL_FRONT) cull = 2;
            else cull = 3;
        }
        int blend = 0;
        if (GL11State.enabled[GL_BLEND]) {
            blend = (GL11State.blendSrc << 8) | GL11State.blendDst;
        }
        int depthTest = GL11State.enabled[GL_DEPTH_TEST] ? 1 : 0;
        int depthWrite = depthTest == 1 && GL11State.depthMask ? 1 : 0;
        int depthFuncCode = depthTest == 1 ? GL11State.depthFunc : 0;

        long key = ((long)topology << 48) | ((long)cull << 40) | ((long)blend << 24)
                 | ((long)depthTest << 20) | ((long)depthWrite << 16) | depthFuncCode;

        Long cached = GL11Vars.pipelineCache.get(key);
        if (cached != null) return cached;

        GL11Vars.log("createPipeline: topology=" + topology + " cull=" + cull + " blend=" + blend
            + " depthTest=" + depthTest + " depthWrite=" + depthWrite);
        long pipeline = createPipeline(topology, cull, blend, depthTest, depthWrite);
        GL11Vars.pipelineCache.put(key, pipeline);
        GL11Vars.log("  pipeline=" + pipeline);
        return pipeline;
    }

    private static long createPipeline(int topology, int cull, int blend, int depthTest, int depthWrite) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int stride = GL11State.FLOATS_PER_VERTEX * 4;
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
            vps.get(0).x(GL11State.viewportX).y(GL11State.viewportY)
                .width(GL11State.viewportW).height(GL11State.viewportH).minDepth(0f).maxDepth(1f);

            VkRect2D.Buffer scissors = VkRect2D.malloc(1);
            VkOffset2D scOff = VkOffset2D.calloc(stack).x(GL11State.scissorX).y(GL11State.scissorY);
            scissors.get(0).offset(scOff);
            VkExtent2D scExt = VkExtent2D.calloc(stack).width(GL11State.scissorW).height(GL11State.scissorH);
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
                switch (GL11State.depthFunc) {
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
                .module(GL11Vars.vertShader)
                .pName(stack.UTF8("main"));
            stages.get(1)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                .stage(VK_SHADER_STAGE_FRAGMENT_BIT)
                .module(GL11Vars.fragShader)
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
                .layout(GL11Vars.pipelineLayout);

            LongBuffer pipePtr = stack.mallocLong(1);
            int result = vkCreateGraphicsPipelines(GL11Vars.device, VK_NULL_HANDLE, gpcis, null, pipePtr);
            if (result != VK_SUCCESS) throw new RuntimeException("vkCreateGraphicsPipelines: " + result);
            return pipePtr.get(0);
        }
    }

    public static int glBlendFactorToVk(int glFactor) {
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
}   