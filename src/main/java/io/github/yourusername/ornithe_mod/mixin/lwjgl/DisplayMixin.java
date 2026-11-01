package io.github.yourusername.ornithe_mod.mixin.lwjgl;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.sdl.SDLInit;
import org.lwjgl.sdl.SDLVideo;
import org.lwjgl.sdl.SDLEvents;
import org.lwjgl.sdl.SDL_Event;
import org.lwjgl.sdl.SDLError;   
import org.lwjgl.sdl.SDL_Rect;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.awt.Canvas;
import java.nio.IntBuffer;

@Mixin(Display.class)
public abstract class DisplayMixin {

    private static long window = 0;
    private static boolean created = false;
    private static boolean fullscreen = false;
    private static boolean resizable = false;
    private static int currentWidth = 854;
    private static int currentHeight = 480;
    private static DisplayMode currentMode;
    private static DisplayMode desktopMode;
    private static String title = "";

    // --- Window creation ---

    @Overwrite
    public static void create() throws LWJGLException {
        create(null, null);
    }

    @Overwrite
    public static void create(PixelFormat format) throws LWJGLException {
        create(format, null);
    }

    @Overwrite
    public static void create(PixelFormat format, ContextAttribs attribs) throws LWJGLException {
        if (created) return;

        if (!SDLInit.SDL_Init(SDLInit.SDL_INIT_VIDEO)) {
            throw new LWJGLException("SDL_Init failed: " + SDLError.SDL_GetError());
        }   

        long flags = SDLVideo.SDL_WINDOW_OPENGL | SDLVideo.SDL_WINDOW_HIDDEN;
        if (resizable) flags |= SDLVideo.SDL_WINDOW_RESIZABLE;
        if (fullscreen) flags |= SDLVideo.SDL_WINDOW_FULLSCREEN;

        window = SDLVideo.SDL_CreateWindow(title, currentWidth, currentHeight, (int) flags);

        if (window == 0) {
            SDLInit.SDL_Quit();
            throw new LWJGLException("SDL_CreateWindow failed: " + SDLError.SDL_GetError()   );
        }

        long context = SDLVideo.SDL_GL_CreateContext(window);
        if (context == 0) {
            SDLVideo.SDL_DestroyWindow(window);
            SDLInit.SDL_Quit();
            throw new LWJGLException("SDL_GL_CreateContext failed: " + SDLError.SDL_GetError()   );
        }

        org.lwjgl.opengl.GL.createCapabilities();
        SDLVideo.SDL_GL_SetSwapInterval(1);
        SDLVideo.SDL_ShowWindow(window);
        created = true;
    }

    // --- AWT bypass ---

    @Overwrite
    public static void setParent(Canvas parent) throws LWJGLException {
        // NO-OP: de-AWT'd
    }

    @Overwrite
    public static Canvas getParent() {
        return null;
    }

    // --- Frame loop ---

    @Overwrite
    public static void update() {
        if (window != 0) {
            SDLEvents.SDL_PumpEvents();
            SDLVideo.SDL_GL_SwapWindow(window);
        }
    }

    @Overwrite
    public static void update(boolean processMessages) {
        update();
    }

    @Overwrite
    public static void swapBuffers() {
        if (window != 0) SDLVideo.SDL_GL_SwapWindow(window);
    }

    @Overwrite
    public static void processMessages() {
        if (window != 0) SDLEvents.SDL_PumpEvents();
    }

    @Overwrite
    public static void sync(int fps) {
        // Handled by SDL_GL_SetSwapInterval
    }

    // --- Window state ---

    @Overwrite
    public static void destroy() {
        if (window != 0) {
            SDLVideo.SDL_DestroyWindow(window);
            window = 0;
        }
        if (created) {
            SDLInit.SDL_Quit();
            created = false;
        }
    }

    @Overwrite
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

    @Overwrite
    public static void setTitle(String newTitle) {
        title = newTitle;
        if (window != 0) SDLVideo.SDL_SetWindowTitle(window, newTitle);
    }

    @Overwrite
    public static String getTitle() {
        return title;
    }

    @Overwrite
    public static void setVSyncEnabled(boolean enabled) {
        SDLVideo.SDL_GL_SetSwapInterval(enabled ? 1 : 0);
    }

    @Overwrite
    public static void setSwapInterval(int value) {
        SDLVideo.SDL_GL_SetSwapInterval(value);
    }

    @Overwrite
    public static void setResizable(boolean res) {
        resizable = res;
    }

    @Overwrite
    public static boolean isResizable() {
        return resizable;
    }

    @Overwrite
    public static void setFullscreen(boolean fs) throws LWJGLException {
        fullscreen = fs;
        if (window != 0) {
            SDLVideo.SDL_SetWindowFullscreen(window, fs);
        }
    }

    @Overwrite
    public static boolean isFullscreen() {
        return fullscreen;
    }

    // --- Display modes ---

    @Overwrite
    public static void setDisplayMode(DisplayMode mode) throws LWJGLException {
        currentMode = mode;
        currentWidth = mode.getWidth();
        currentHeight = mode.getHeight();
        if (window != 0) {
            SDLVideo.SDL_SetWindowSize(window, currentWidth, currentHeight);
        }
    }

    @Overwrite
    public static void setDisplayModeAndFullscreen(DisplayMode mode) throws LWJGLException {
        setDisplayMode(mode);
    }

    @Overwrite
    public static DisplayMode getDisplayMode() {
        return currentMode;
    }

    @Overwrite
    public static DisplayMode getDesktopDisplayMode() {
        if (desktopMode == null) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                SDL_Rect rect = SDL_Rect.malloc(stack);
                if (SDLVideo.SDL_GetDisplayBounds(0, rect) == true) {
                    desktopMode = new DisplayMode(rect.w(), rect.h());
                } else {
                    desktopMode = new DisplayMode(1920, 1080);
                }
            }
            if (desktopMode == null) desktopMode = new DisplayMode(1920, 1080);
        }
        return desktopMode;
    }

    @Overwrite
    public static DisplayMode[] getAvailableDisplayModes() throws LWJGLException {
        return new DisplayMode[] { getDesktopDisplayMode() };
    }

    // --- Window properties ---

    @Overwrite
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

    @Overwrite
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

    @Overwrite
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

    @Overwrite
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

    @Overwrite
    public static void setLocation(int x, int y) {
        if (window != 0) SDLVideo.SDL_SetWindowPosition(window, x, y);
    }

    @Overwrite
    public static boolean isActive() {
        if (window == 0) return false;
        long flags = SDLVideo.SDL_GetWindowFlags(window);
        return (flags & SDLVideo.SDL_WINDOW_INPUT_FOCUS) != 0;
    }

    @Overwrite
    public static boolean isVisible() {
        if (window == 0) return false;
        long flags = SDLVideo.SDL_GetWindowFlags(window);
        return (flags & SDLVideo.SDL_WINDOW_HIDDEN) == 0;
    }   

    @Overwrite
    public static boolean isCreated() {
        return created;
    }

    @Overwrite
    public static boolean isCurrent() {
        return SDLVideo.SDL_GL_GetCurrentContext() != 0;
    }

    @Overwrite
    public static void makeCurrent() throws LWJGLException {
        if (window != 0) {
            long ctx = SDLVideo.SDL_GL_GetCurrentContext();
            if (ctx == 0) {
                ctx = SDLVideo.SDL_GL_CreateContext(window);
            }
            SDLVideo.SDL_GL_MakeCurrent(window, ctx);
        }
    }

    @Overwrite
    public static float getPixelScaleFactor() {
        if (window != 0) {
            return SDLVideo.SDL_GetWindowDisplayScale(window);
        }
        return 1.0f;
    }   

    @Overwrite
    public static void setInitialBackground(float r, float g, float b) {
        org.lwjgl.opengl.GL11.glClearColor(r, g, b, 1.0f);
    }

    @Overwrite
    public static boolean wasResized() {
        return false;
    }

    @Overwrite
    public static org.lwjgl.opengl.Drawable getDrawable() {
        return null;
    }

    @Overwrite
    public static String getVersion() {
        return "LWJGL 3.4.1 (SDL3 shim)";
    }

    @Overwrite
    public static String getDriverVersion() {
        return org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_VERSION);
    }
}   