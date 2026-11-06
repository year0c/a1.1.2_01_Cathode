package io.github.year0c.cathode.mixin;

import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.util.GL11.GL11State;
import org.lwjgl.opengl.func.GL11.GL11GLFunc;
import org.lwjgl.opengl.func.GL11.GL11TextureFunc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@Mixin(GL11C.class)
public class GL11CMixin {

    @Overwrite
    public static int glGenTextures(int n, IntBuffer ids) {
        int first = GL11State.nextTextureId;
        for (int i = 0; i < n; i++) ids.put(i, GL11State.nextTextureId++);
        return first;
    }

    @Overwrite
    public static void glBindTexture(int target, int texture) {
        GL11State.boundTexture = texture;
    }

    @Overwrite
    public static void glDeleteTextures(int n, IntBuffer ids) {
        for (int i = 0; i < n;) GL11TextureFunc.deleteTexture(ids.get(i));
    }

    @Overwrite
    public static void glTexImage2D(int target, int level, int internalformat, int width, int height,
                                    int border, int format, int type, IntBuffer pixels) {
        int channels = (format == 0x1908) ? 4 : (format == 0x1907) ? 3 : (format == 0x8040) ? 2 : 1;
        int size = width * height * channels;
        byte[] data = new byte[size];
        for (int i = 0; i < size; i++) data[i] = (byte) pixels.get(i);
        GL11TextureFunc.uploadTexture(GL11State.boundTexture, width, height, format, type, data);
    }

    @Overwrite
    public static void glTexParameteri(int target, int pname, int param) {
        if (pname < GL11State.textureParams.length) GL11State.textureParams[pname] = param;
    }

    @Overwrite
    public static void glMultMatrixf(FloatBuffer m) { GL11GLFunc.glMultMatrixf(m); }

    @Overwrite
    public static void glLoadMatrixf(FloatBuffer m) { GL11GLFunc.glLoadMatrixf(m); }
}   