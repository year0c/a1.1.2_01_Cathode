package org.lwjgl.opengl;

public class PixelFormat {
    private int redBits = 8;
    private int greenBits = 8;
    private int blueBits = 8;
    private int alphaBits = 8;
    private int depthBits = 16;
    private int stencilBits = 8;
    private int samples = 0;

    public PixelFormat() {}

    public PixelFormat withRedBits(int bits) { redBits = bits; return this; }
    public PixelFormat withGreenBits(int bits) { greenBits = bits; return this; }
    public PixelFormat withBlueBits(int bits) { blueBits = bits; return this; }
    public PixelFormat withAlphaBits(int bits) { alphaBits = bits; return this; }
    public PixelFormat withDepthBits(int bits) { depthBits = bits; return this; }
    public PixelFormat withStencilBits(int bits) { stencilBits = bits; return this; }
    public PixelFormat withSamples(int samples) { this.samples = samples; return this; }

    public int getRedBits() { return redBits; }
    public int getGreenBits() { return greenBits; }
    public int getBlueBits() { return blueBits; }
    public int getAlphaBits() { return alphaBits; }
    public int getDepthBits() { return depthBits; }
    public int getStencilBits() { return stencilBits; }
    public int getSamples() { return samples; }
}   