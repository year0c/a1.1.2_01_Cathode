package io.github.year0c.cathode.mixin;

import org.lwjglx.LWJGLException;
import org.lwjglx.opengl.DisplayMode;
import org.lwjglx.opengl.PixelFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import pl.tomgirl.pylon.window.DisplaySdl;

import java.awt.Canvas;
import java.nio.ByteBuffer;

@Mixin(targets = "org.lwjglx.opengl.Display")
public class DisplayMixin {

    @Overwrite
    public static void create() throws LWJGLException {
        DisplaySdl.instance().create(new PixelFormat());
    }

    @Overwrite
    public static void create(PixelFormat format) throws LWJGLException {
        DisplaySdl.instance().create(format);
    }

    @Overwrite
    public static void update() {
        DisplaySdl.instance().update();
    }

    @Overwrite
    public static void swapBuffers() {
        DisplaySdl.instance().swapBuffers();
    }

    @Overwrite
    public static boolean isCloseRequested() {
        return DisplaySdl.instance().isCloseRequested();
    }

    @Overwrite
    public static void setTitle(String title) {
        DisplaySdl.instance().setTitle(title);
    }

    @Overwrite
    public static String getTitle() {
        return DisplaySdl.instance().getTitle();
    }

    @Overwrite
    public static void setDisplayMode(DisplayMode mode) {
        DisplaySdl.instance().setDisplayMode(mode);
    }

    @Overwrite
    public static DisplayMode getDisplayMode() {
        return DisplaySdl.instance().getDisplayMode();
    }

    @Overwrite
    public static DisplayMode getDesktopDisplayMode() {
        return DisplaySdl.instance().getDesktopDisplayMode();
    }

    @Overwrite
    public static DisplayMode[] getAvailableDisplayModes() {
        return DisplaySdl.instance().getAvailableDisplayModes();
    }

    @Overwrite
    public static void setFullscreen(boolean fullscreen) {
        DisplaySdl.instance().setFullscreen(fullscreen);
    }

    @Overwrite
    public static boolean isFullscreen() {
        return DisplaySdl.instance().isFullscreen();
    }

    @Overwrite
    public static void destroy() {
        DisplaySdl.instance().destroy();
    }

    @Overwrite
    public static boolean isCreated() {
        return DisplaySdl.instance().isCreated();
    }

    @Overwrite
    public static boolean isActive() {
        return DisplaySdl.instance().isActive();
    }

    @Overwrite
    public static int getWidth() {
        return DisplaySdl.instance().getWidth();
    }

    @Overwrite
    public static int getHeight() {
        return DisplaySdl.instance().getHeight();
    }

    @Overwrite
    public static void setResizable(boolean resizable) {
        DisplaySdl.instance().setResizable(resizable);
    }

    @Overwrite
    public static void setParent(Canvas canvas) {
        // no-op
    }

    @Overwrite
    public static int setIcon(ByteBuffer[] icons) {
        return DisplaySdl.instance().setIcon(icons);
    }

    @Overwrite
    public static void setLocation(int x, int y) {
        DisplaySdl.instance().setLocation(x, y);
    }

    @Overwrite
    public static boolean wasResized() {
        return DisplaySdl.instance().wasResized();
    }

    @Overwrite
    public static boolean isVisible() {
        return DisplaySdl.instance().isVisible();
    }
}   