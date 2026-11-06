package org.lwjglx.opengl;

import java.awt.Canvas;
import java.nio.ByteBuffer;
import org.lwjglx.LWJGLException;

public class Display {
    public static void create() throws LWJGLException {}
    public static void create(PixelFormat format) throws LWJGLException {}
    public static void update() {}
    public static void swapBuffers() {}
    public static boolean isCloseRequested() { return false; }
    public static void setTitle(String title) {}
    public static String getTitle() { return ""; }
    public static void setDisplayMode(DisplayMode mode) {}
    public static DisplayMode getDisplayMode() { return null; }
    public static DisplayMode getDesktopDisplayMode() { return null; }
    public static DisplayMode[] getAvailableDisplayModes() { return new DisplayMode[0]; }
    public static void setFullscreen(boolean fullscreen) {}
    public static boolean isFullscreen() { return false; }
    public static void destroy() {}
    public static boolean isCreated() { return false; }
    public static boolean isActive() { return false; }
    public int getWidth() { return 0; }
    public static int getHeight() { return 0; }
    public static void setResizable(boolean resizable) {}
    public static void setParent(Canvas canvas) {}
    public static int setIcon(ByteBuffer[] icons) { return 0; }
    public static void setLocation(int x, int y) {}
    public static boolean wasResized() { return false; }
    public static boolean isVisible() { return true; }
}   