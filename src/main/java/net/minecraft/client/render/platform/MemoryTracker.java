package net.minecraft.client.render.platform;

public final class MemoryTracker {
    private static int nextId = 1;

    private MemoryTracker() {}

    public static int genTextures(int count) {
        if (count < 1) throw new IllegalArgumentException("count must be at least 1");
        return nextId++;
    }
}   