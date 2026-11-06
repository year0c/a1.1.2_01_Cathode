package org.lwjgl.opengl.func.GL11;

import org.lwjgl.opengl.util.GL11.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.FloatBuffer;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.opengl.util.GL11.GL11Constants.*;

public class GL11UtilFunc {

    public static float[] currentMatrix() {
        switch (GL11State.currentMatrixMode) {
            case GL_PROJECTION: return GL11State.projectionStack[GL11State.projectionSp];
            case GL_TEXTURE: return GL11State.textureStack[GL11State.textureSp];
            default: return GL11State.modelviewStack[GL11State.modelviewSp];
        }
    }

    public static void identity(float[] m) {
        java.util.Arrays.fill(m, 0f);
        m[0] = m[5] = m[10] = m[15] = 1f;
    }

    public static void multiplyMatrices(float[] a, float[] b, float[] result) {
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

    public static void multiplyMatrices(float[] a, FloatBuffer b, float[] result) {
        float[] bArr = new float[16];
        for (int i = 0; i < 16; i++) bArr[i] = b.get(i);
        multiplyMatrices(a, bArr, result);
    }

    public static int findMemoryType(int typeBits, int props) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkPhysicalDeviceMemoryProperties memProps = VkPhysicalDeviceMemoryProperties.calloc(stack);
            vkGetPhysicalDeviceMemoryProperties(GL11Vars.physicalDevice, memProps);
            for (int i = 0; i < memProps.memoryTypeCount(); i++) {
                if ((typeBits & (1 << i)) != 0 &&
                    (memProps.memoryTypes().get(i).propertyFlags() & props) == props) {
                    return i;
                }
            }
        }
        throw new RuntimeException("No suitable memory type found for bits=0x" + Integer.toHexString(typeBits));
    }

    public static byte[] loadSpirV(String name) throws Exception {
        GL11Vars.log("loadSpirV: " + name);
        java.io.InputStream is = GL11UtilFunc.class.getResourceAsStream("/shaders/" + name);
        if (is == null) is = GL11UtilFunc.class.getResourceAsStream(name);
        if (is == null) throw new RuntimeException("Shader not found: " + name);
        byte[] data = is.readAllBytes();
        is.close();
        GL11Vars.log("  loaded " + data.length + " bytes");
        return data;
    }

    public static boolean isInitialized() {
        return GL11Vars.initialized;
    }
}   