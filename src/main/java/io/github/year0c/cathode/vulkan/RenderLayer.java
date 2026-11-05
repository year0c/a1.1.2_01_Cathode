package io.github.year0c.cathode.vulkan;

public enum RenderLayer {
    SOLID,
    TRANSLUCENT;

    public static RenderLayer getLayer(int blockId) {
        switch (blockId) {
            case 8:   // flowing water
            case 9:   // still water
            case 10:  // flowing lava
            case 11:  // still lava
                return TRANSLUCENT;
            default:
                return SOLID;
        }
    }
}   