package org.lwjgl.opengl;

public class DisplayMode {
    private final int width, height;
    private final int frequency;

    public DisplayMode(int width, int height) {
        this(width, height, 0);
    }

    public DisplayMode(int width, int height, int frequency) {
        this.width = width;
        this.height = height;
        this.frequency = frequency;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getFrequency() { return frequency; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DisplayMode)) return false;
        DisplayMode m = (DisplayMode) o;
        return width == m.width && height == m.height && frequency == m.frequency;
    }

    @Override
    public int hashCode() {
        return width * 31 + height * 31 + frequency;
    }

    @Override
    public String toString() {
        return width + "x" + height + "x" + frequency + "Hz";
    }
}   