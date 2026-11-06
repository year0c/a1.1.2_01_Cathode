package org.lwjglx.opengl;

public class DisplayMode {
    private final int width, height, bits, hz;

    public DisplayMode(int width, int height, int bits, int hz) {
        this.width = width; this.height = height; this.bits = bits; this.hz = hz;
    }
    public DisplayMode(int width, int height) { this(width, height, 32, 0); }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getBits() { return bits; }
    public int getFrequency() { return hz; }
}   