package org.lwjgl.opengl;

import org.lwjgl.opengl.DisplayMode;

public class DisplayMode {
    private final int width;
    private final int height;
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
        if (this == o) return true;
        if (!(o instanceof DisplayMode)) return false;
        DisplayMode that = (DisplayMode) o;
        return width == that.width && height == that.height && frequency == that.frequency;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        result = 31 * result + frequency;
        return result;
    }   

    @Override
    public String toString() {
        return width + "x" + height + "x" + frequency + "Hz";
    }
}   