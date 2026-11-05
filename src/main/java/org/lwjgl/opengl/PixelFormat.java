package org.lwjgl.opengl;

public class PixelFormat {
    private final int depthBits;
    private final int stencilBits;

    public PixelFormat() {
        this(24, 0);
    }

    public PixelFormat(int depthBits, int stencilBits) {
        this.depthBits = depthBits;
        this.stencilBits = stencilBits;
    }

    public int getDepthBits() { return depthBits; }
    public int getStencilBits() { return stencilBits; }

    public PixelFormat withDepthBits(int depthBits) {
        return new PixelFormat(depthBits, this.stencilBits);
    }

    public PixelFormat withStencilBits(int stencilBits) {
        return new PixelFormat(this.depthBits, stencilBits);
    }

    @Override
    public String toString() {
        return "sRGB " + depthBits + "-bit depth" + (stencilBits > 0 ? " " + stencilBits + "-bit stencil" : "");
    }
}   