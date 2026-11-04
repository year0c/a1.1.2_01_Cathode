package org.lwjglx.opengl;

import org.lwjglx.LWJGLException;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.sdl.SDLInit;
import org.lwjgl.sdl.SDLVideo;
import org.lwjgl.sdl.SDLEvents;
import org.lwjgl.sdl.SDL_Event;
import org.lwjgl.sdl.SDLError;
import org.lwjgl.sdl.SDL_Rect;
import org.lwjgl.sdl.SDLProperties;
import org.lwjgl.system.MemoryStack;

import java.awt.Canvas;
import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

public class Display {

    private static long window = 0;
    private static boolean created = false;
    private static boolean fullscreen = false;
    private static boolean resizable = false;
    private static int currentWidth = 854;
    private static int currentHeight = 480;
    private static DisplayMode currentMode;
    private static DisplayMode desktopMode;
    private static String title = "";
    private static long nativeHandle = 0;
    private static Canvas parentCanvas = null;
    private static final Set<Long> childWindows = new HashSet<>();

    // --- Window creation ---

    public static void create() throws LWJGLException {
        create(null, null);
    }

    public static void create(PixelFormat format) throws LWJGLException {
        create(format, null);
    }

    public static void create(PixelFormat format, ContextAttribs attribs) throws LWJGLException {
        if (created) return;

        if (!SDLInit.SDL_Init(SDLInit.SDL_INIT_VIDEO)) {
            throw new LWJGLException("SDL_Init failed: " + SDLError.SDL_GetError());
        }

        SDLVideo.SDL_GL_SetAttribute(SDLVideo.SDL_GL_CONTEXT_MAJOR_VERSION, 2);
        SDLVideo.SDL_GL_SetAttribute(SDLVideo.SDL_GL_CONTEXT_MINOR_VERSION, 1);
        SDLVideo.SDL_GL_SetAttribute(SDLVideo.SDL_GL_DOUBLEBUFFER, 1);
        if (format != null) {
            SDLVideo.SDL_GL_SetAttribute(SDLVideo.SDL_GL_RED_SIZE, format.getRedBits());
            SDLVideo.SDL_GL_SetAttribute(SDLVideo.SDL_GL_GREEN_SIZE, format.getGreenBits());
            SDLVideo.SDL_GL_SetAttribute(SDLVideo.SDL_GL_BLUE_SIZE, format.getBlueBits());
            SDLVideo.SDL_GL_SetAttribute(SDLVideo.SDL_GL_ALPHA_SIZE, format.getAlphaBits());
            SDLVideo.SDL_GL_SetAttribute(SDLVideo.SDL_GL_DEPTH_SIZE, format.getDepthBits());
            SDLVideo.SDL_GL_SetAttribute(SDLVideo.SDL_GL_STENCIL_SIZE, format.getStencilBits());
        }

        if (nativeHandle != 0) {
            int props = SDLProperties.SDL_CreateProperties();
            String os = System.getProperty("os.name", "").toLowerCase();
            if (os.contains("win")) {
                SDLProperties.SDL_SetPointerProperty(props, "SDL.window.win32.hwnd", nativeHandle);
            } else if (os.contains("mac")) {
                SDLProperties.SDL_SetPointerProperty(props, "SDL.window.cocoa.view", nativeHandle);
            } else {
                SDLProperties.SDL_SetPointerProperty(props, "SDL.window.x11.id", nativeHandle);
            }
            SDLProperties.SDL_SetBooleanProperty(props, "SDL_PROP_WINDOW_CREATE_OPENGL_BOOLEAN", true);
            window = SDLVideo.SDL_CreateWindowWithProperties(props);
            SDLProperties.SDL_DestroyProperties(props);

            if (window == 0) {
                SDLInit.SDL_Quit();
                throw new LWJGLException("SDL_CreateWindowWithProperties failed: " + SDLError.SDL_GetError());
            }
        } else {
            long flags = SDLVideo.SDL_WINDOW_OPENGL | SDLVideo.SDL_WINDOW_HIDDEN;
            if (resizable) flags |= SDLVideo.SDL_WINDOW_RESIZABLE;
            if (fullscreen) flags |= SDLVideo.SDL_WINDOW_FULLSCREEN;

            window = SDLVideo.SDL_CreateWindow(title, currentWidth, currentHeight, (int) flags);
            if (window == 0) {
                SDLInit.SDL_Quit();
                throw new LWJGLException("SDL_CreateWindow failed: " + SDLError.SDL_GetError());
            }
        }

        long context = SDLVideo.SDL_GL_CreateContext(window);
        if (context == 0) {
            SDLVideo.SDL_DestroyWindow(window);
            SDLInit.SDL_Quit();
            throw new LWJGLException("SDL_GL_CreateContext failed: " + SDLError.SDL_GetError());
        }

        SDLVideo.SDL_GL_MakeCurrent(window, context);
        if (SDLVideo.SDL_GL_GetCurrentContext() == 0) {
            SDLVideo.SDL_DestroyWindow(window);
            SDLInit.SDL_Quit();
            throw new LWJGLException("GL context not current after MakeCurrent");
        }

        GL.createCapabilities();
        SDLVideo.SDL_GL_SetSwapInterval(1);
        if (nativeHandle == 0) {
            SDLVideo.SDL_ShowWindow(window);
        }
        created = true;
    }

    // --- AWT handoff ---

    public static void setParent(Canvas parent) throws LWJGLException {
        parentCanvas = parent;
        nativeHandle = extractNativeHandle(parent);

        if (parent.getParent() instanceof java.awt.Frame f) {
            String os = System.getProperty("os.name", "").toLowerCase();
            if (os.contains("mac")) {
                f.setTitle(" ");
            } else {
                f.setUndecorated(true);
            }
            f.setBackground(java.awt.Color.BLACK);
            f.setResizable(true);
        }
    }

    public static Canvas getParent() {
        return parentCanvas;
    }

    private static long extractNativeHandle(Canvas canvas) {
        try {
            Field peerField = canvas.getClass().getDeclaredField("peer");
            peerField.setAccessible(true);
            Object peer = peerField.get(canvas);
            if (peer != null) {
                try {
                    Field handleField = peer.getClass().getDeclaredField("handle");
                    handleField.setAccessible(true);
                    long h = handleField.getLong(peer);
                    if (h != 0) return h;
                } catch (NoSuchFieldException ignored) {}
            }
        } catch (Exception ignored) {}
        return 0;
    }

    // --- Frame loop ---

    public static void update() {
        if (window != 0) {
            SDLEvents.SDL_PumpEvents();
            SDLVideo.SDL_GL_SwapWindow(window);
        }
    }

    public static void update(boolean processMessages) { update(); }
    public static void swapBuffers() { if (window != 0) SDLVideo.SDL_GL_SwapWindow(window); }
    public static void processMessages() { if (window != 0) SDLEvents.SDL_PumpEvents(); }
    public static void sync(int fps) {}

    // --- Window state ---

    public static void destroy() {
        for (long child : childWindows) SDLVideo.SDL_DestroyWindow(child);
        childWindows.clear();
        if (window != 0) {
            if (nativeHandle == 0) SDLVideo.SDL_DestroyWindow(window);
            window = 0;
        }
        if (created) {
            SDLInit.SDL_Quit();
            created = false;
        }
        nativeHandle = 0;
        parentCanvas = null;
    }

    public static boolean isCloseRequested() {
        if (window == 0) return false;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            SDL_Event event = SDL_Event.malloc(stack);
            while (SDLEvents.SDL_PollEvent(event)) {
                if (event.type() == SDLEvents.SDL_EVENT_QUIT) return true;
            }
        }
        return false;
    }

    public static void setTitle(String newTitle) {
        title = newTitle;
        if (window != 0) SDLVideo.SDL_SetWindowTitle(window, newTitle);
        if (parentCanvas != null && parentCanvas.getParent() instanceof java.awt.Frame f) {
            f.setTitle(newTitle);
        }
    }

    public static String getTitle() { return title; }
    public static void setVSyncEnabled(boolean enabled) { SDLVideo.SDL_GL_SetSwapInterval(enabled ? 1 : 0); }
    public static void setSwapInterval(int value) { SDLVideo.SDL_GL_SetSwapInterval(value); }
    public static void setResizable(boolean res) { resizable = res; }
    public static boolean isResizable() { return resizable; }

    public static void setFullscreen(boolean fs) throws LWJGLException {
        fullscreen = fs;
        if (window != 0) SDLVideo.SDL_SetWindowFullscreen(window, fs);
    }

    public static boolean isFullscreen() { return fullscreen; }

    // --- Display modes ---

    public static void setDisplayMode(DisplayMode mode) throws LWJGLException {
        currentMode = mode;
        currentWidth = mode.getWidth();
        currentHeight = mode.getHeight();
        if (window != 0) SDLVideo.SDL_SetWindowSize(window, currentWidth, currentHeight);
        if (parentCanvas != null && parentCanvas.getParent() instanceof java.awt.Frame f) {
            f.setSize(currentWidth, currentHeight);
        }
    }

    public static void setDisplayModeAndFullscreen(DisplayMode mode) throws LWJGLException { setDisplayMode(mode); }
    public static DisplayMode getDisplayMode() { return currentMode; }

    public static DisplayMode getDesktopDisplayMode() {
        if (desktopMode == null) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                SDL_Rect rect = SDL_Rect.malloc(stack);
                if (SDLVideo.SDL_GetDisplayBounds(0, rect)) {
                    desktopMode = new DisplayMode(rect.w(), rect.h());
                } else {
                    desktopMode = new DisplayMode(1920, 1080);
                }
            }
            if (desktopMode == null) desktopMode = new DisplayMode(1920, 1080);
        }
        return desktopMode;
    }

    public static DisplayMode[] getAvailableDisplayModes() throws LWJGLException {
        return new DisplayMode[] { getDesktopDisplayMode() };
    }

    // --- Window properties ---

    public static int getWidth() {
        if (window != 0) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                SDLVideo.SDL_GetWindowSize(window, w, h);
                return w.get(0);
            }
        }
        return currentWidth;
    }

    public static int getHeight() {
        if (window != 0) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                SDLVideo.SDL_GetWindowSize(window, w, h);
                return h.get(0);
            }
        }
        return currentHeight;
    }

    public static int getX() {
        if (window != 0) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer x = stack.mallocInt(1);
                IntBuffer y = stack.mallocInt(1);
                SDLVideo.SDL_GetWindowPosition(window, x, y);
                return x.get(0);
            }
        }
        return 0;
    }

    public static int getY() {
        if (window != 0) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer x = stack.mallocInt(1);
                IntBuffer y = stack.mallocInt(1);
                SDLVideo.SDL_GetWindowPosition(window, x, y);
                return y.get(0);
            }
        }
        return 0;
    }

    public static void setLocation(int x, int y) {
        if (window != 0) SDLVideo.SDL_SetWindowPosition(window, x, y);
    }

    public static boolean isActive() {
        if (window == 0) return false;
        return (SDLVideo.SDL_GetWindowFlags(window) & SDLVideo.SDL_WINDOW_INPUT_FOCUS) != 0;
    }

    public static boolean isVisible() {
        if (window == 0) return false;
        if (nativeHandle != 0) return true;
        return (SDLVideo.SDL_GetWindowFlags(window) & SDLVideo.SDL_WINDOW_HIDDEN) == 0;
    }

    public static boolean isCreated() { return created; }
    public static boolean isCurrent() { return SDLVideo.SDL_GL_GetCurrentContext() != 0; }

    public static void makeCurrent() throws LWJGLException {
        if (window != 0) {
            long ctx = SDLVideo.SDL_GL_GetCurrentContext();
            if (ctx == 0) ctx = SDLVideo.SDL_GL_CreateContext(window);
            SDLVideo.SDL_GL_MakeCurrent(window, ctx);
        }
    }

    public static float getPixelScaleFactor() {
        if (window != 0) return SDLVideo.SDL_GetWindowDisplayScale(window);
        return 1.0f;
    }

    public static void setInitialBackground(float r, float g, float b) {
        GL11C.glClearColor(r, g, b, 1.0f);
    }

    public static boolean wasResized() { return false; }
    public static Drawable getDrawable() { return null; }
    public static String getVersion() { return "LWJGL 3.4.1 (SDL3 shim)"; }
}   