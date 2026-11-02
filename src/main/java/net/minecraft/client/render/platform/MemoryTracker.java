package net.minecraft.client.render.platform;

import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11C;
import org.lwjgl.system.MemoryStack;

public final class MemoryTracker {
    private MemoryTracker() {
    }

    public static int genTextures(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("count must be at least 1");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer textures = stack.mallocInt(count);
            GL11C.glGenTextures(textures);
            return textures.get(0);
        }
    }
}
