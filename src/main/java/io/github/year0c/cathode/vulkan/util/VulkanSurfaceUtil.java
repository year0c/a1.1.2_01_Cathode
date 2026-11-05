package io.github.year0c.cathode.vulkan.util;

import pl.tomgirl.pylon.window.GpuSurface;
import org.lwjgl.sdl.SDLVideo;
import org.lwjgl.opengl.GL11;
import org.lwjgl.sdl.SDLError;

public class VulkanSurfaceUtil implements GpuSurface {

    private long window = -1L;

    @Override
    public long createWindow(String title, int width, int height, boolean resizable) {
        long flags = SDLVideo.SDL_WINDOW_VULKAN | SDLVideo.SDL_WINDOW_HIDDEN;
        if (resizable) flags |= SDLVideo.SDL_WINDOW_RESIZABLE;

        window = SDLVideo.SDL_CreateWindow(title, width, height, (int) flags);
        if (window == 0) {
            throw new RuntimeException("SDL_CreateWindow failed: " + SDLError.SDL_GetError());
        }

        try {
            GL11.initVulkan(window, width, height);
        } catch (Exception e) {
            SDLVideo.SDL_DestroyWindow(window);
            window = -1L;
            throw new RuntimeException("Vulkan init failed", e);
        }

        return window;
    }

    @Override
    public void swapBuffers() {
        try {
            org.lwjgl.opengl.func.GL11.GL11FrameFunc.endFrame();
        } catch (Exception ignored) {}
    }

    @Override
    public void makeCurrent() { /* no-op for Vulkan */ }

    @Override
    public void releaseCurrent() { /* no-op for Vulkan */ }

    @Override
    public void setVSyncEnabled(boolean enabled) { /* handled by swapchain present mode */ }

    @Override
    public void destroy() {
        // Destroy Vulkan resources here (instance, device, swapchain, etc.)
        if (window > 0) {
            SDLVideo.SDL_DestroyWindow(window);
            window = -1L;
        }
    }
}