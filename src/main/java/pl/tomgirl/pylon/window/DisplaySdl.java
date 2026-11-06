package pl.tomgirl.pylon.window;

import org.lwjglx.LWJGLException;
import org.lwjglx.opengl.DisplayMode;
import org.lwjglx.opengl.PixelFormat;
import org.lwjgl.sdl.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.opengl.GL;
import java.lang.reflect.Constructor;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.sdl.SDLError.SDL_GetError;
import static org.lwjgl.sdl.SDLVideo.*;
import static org.lwjgl.system.MemoryStack.stackPush;

import static org.lwjgl.sdl.SDLEvents.*;
import static org.lwjgl.sdl.SDLInit.*;
import static org.lwjgl.sdl.SDLPixels.*;
import static org.lwjgl.sdl.SDLSurface.*;   

public class DisplaySdl {

    private static final DisplaySdl INSTANCE = new DisplaySdl();

    private boolean videoInitialized;
    private SDL_Event event;

    private long handle = -1L;
    private String title = "Minecraft";
    private ByteBuffer[] cachedIcons;

    private int width = 854;
    private int height = 480;
    private int framebufferWidth = 854;
    private int framebufferHeight = 480;

    private boolean windowResized = true;
    private boolean resizePending;
    private boolean resizeChecked;
    private boolean minimized;
    private boolean fullscreen;
    private boolean focused;
    private boolean closeRequested;

    public static DisplaySdl instance() {
        return INSTANCE;
    }

    public long getHandle() {
        return handle;
    }

    public boolean isCloseRequested() {
        return closeRequested;
    }

    private SDL_Event getEvent() {
        if (event == null) {
            event = SDL_Event.calloc();
        }
        return event;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        if (isCreated()) {
            SDL_SetWindowTitle(handle, title);
        }
    }

    private static final Constructor<@org.jetbrains.annotations.NotNull DisplayMode> DISPLAY_MODE_CTOR;   

    static {
        try {
            DISPLAY_MODE_CTOR = DisplayMode.class.getDeclaredConstructor(int.class, int.class, int.class, int.class);
            DISPLAY_MODE_CTOR.setAccessible(true);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static DisplayMode createDisplayMode(int w, int h, int bpp, int hz) {
        try {
            return DISPLAY_MODE_CTOR.newInstance(w, h, bpp, hz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setDisplayMode(DisplayMode mode) {
        width = mode.getWidth();
        height = mode.getHeight();
        framebufferWidth = width;
        framebufferHeight = height;
        windowResized = true;
        resizePending = true;
    }

    public int getWidth() {
        return framebufferWidth;
    }

    public int getHeight() {
        return framebufferHeight;
    }

    public DisplayMode getDisplayMode() {
        return createDisplayMode(framebufferWidth, framebufferHeight, 24, 60);
    }   

    public void setFullscreen(boolean fullscreen) {
        if (!isCreated()) {
            this.fullscreen = fullscreen;
            return;
        }
        if (!SDL_SetWindowFullscreen(handle, fullscreen)) {
            System.err.println("Failed to set fullscreen: " + SDL_GetError());
            return;
        }
        this.fullscreen = fullscreen;
        windowResized = true;
        resizePending = true;
        pollEvents();
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public DisplayMode getDesktopDisplayMode() {
        int display = SDL_GetPrimaryDisplay();
        SDL_DisplayMode mode = SDL_GetDesktopDisplayMode(display);
        if (mode == null) {
            return createDisplayMode(1024, 768, 24, 60);
        }
        return createDisplayMode(mode.w(), mode.h(), 32, (int) mode.refresh_rate());
    }

    public DisplayMode[] getAvailableDisplayModes() {
        int display = SDL_GetPrimaryDisplay();
        var buf = SDL_GetFullscreenDisplayModes(display);
        if (buf == null) return new DisplayMode[0];

        DisplayMode[] modes = new DisplayMode[buf.limit()];
        for (int i = 0; i < modes.length; i++) {
            long m = buf.get(i);
            modes[i] = createDisplayMode(
                SDL_DisplayMode.nw(m), SDL_DisplayMode.nh(m),
                32, Math.round(SDL_DisplayMode.nrefresh_rate(m))
            );
        }
        org.lwjgl.sdl.SDLStdinc.SDL_free(buf);
        return modes;
    }

    public int setIcon(ByteBuffer[] icons) {
        cachedIcons = icons;
        if (!isCreated() || icons == null || icons.length == 0) return 0;

        ByteBuffer icon = icons[0];
        int size = (int) Math.sqrt(icon.remaining() / 4);
        if (size == 0 || size * size * 4 != icon.remaining()) {
            throw new IllegalArgumentException("Icon must be a square RGBA image");
        }

        ByteBuffer pixels = MemoryUtil.memAlloc(icon.remaining());
        try {
            pixels.put(icon.duplicate()).flip();
            try (SDL_Surface surface = SDL_CreateSurfaceFrom(size, size, SDL_PIXELFORMAT_RGBA32, pixels, size * 4)) {
                if (surface != null && !SDL_SetWindowIcon(handle, surface)) {
                    System.err.println("Failed to set icon: " + SDL_GetError());
                }
            }
        } finally {
            MemoryUtil.memFree(pixels);
        }
        return 1;
    }

    public void update() {
        swapBuffers();
        processMessages();
    }

    public void processMessages() {
        windowResized = resizePending || (windowResized && !resizeChecked);
        resizePending = false;
        resizeChecked = false;
        pollEvents();
    }

    private void pollEvents() {
        SDL_Event ev = getEvent();
        while (SDL_PollEvent(ev)) {
            switch (ev.type()) {
                case SDL_EVENT_QUIT:
                case SDL_EVENT_WINDOW_CLOSE_REQUESTED:
                    closeRequested = true;
                    break;
                case SDL_EVENT_WINDOW_FOCUS_GAINED:
                    focused = true;
                    break;
                case SDL_EVENT_WINDOW_FOCUS_LOST:
                    focused = false;
                    break;
                case SDL_EVENT_WINDOW_SHOWN:
                case SDL_EVENT_WINDOW_RESTORED:
                case SDL_EVENT_WINDOW_MAXIMIZED:
                    minimized = false;
                    break;
                case SDL_EVENT_WINDOW_HIDDEN:
                case SDL_EVENT_WINDOW_MINIMIZED:
                    minimized = true;
                    break;
                case SDL_EVENT_WINDOW_RESIZED:
                    resizeCallback(handle, ev.window().data1(), ev.window().data2());
                    break;
                case SDL_EVENT_WINDOW_PIXEL_SIZE_CHANGED:
                    onFramebufferResize(ev.window().data1(), ev.window().data2());
                    break;
                case SDL_EVENT_WINDOW_ENTER_FULLSCREEN:
                    fullscreen = true;
                    windowResized = true;
                    break;
                case SDL_EVENT_WINDOW_LEAVE_FULLSCREEN:
                    fullscreen = false;
                    windowResized = true;
                    break;
            }
        }
    }

    public synchronized void initializeVideo() {
        if (videoInitialized) return;

        if (!SDL_Init(SDL_INIT_VIDEO)) {
            throw new IllegalStateException("Unable to initialize SDL: " + SDL_GetError());
        }
        videoInitialized = true;
    }

    public void create(PixelFormat format) throws LWJGLException {
        if (isCreated()) {
            throw new IllegalStateException("Display has already been created");
        }

        initializeVideo();

        SDL_GL_SetAttribute(SDL_GL_CONTEXT_MAJOR_VERSION, 2);
        SDL_GL_SetAttribute(SDL_GL_CONTEXT_MINOR_VERSION, 1);
        SDL_GL_SetAttribute(SDL_GL_DOUBLEBUFFER, 1);
        SDL_GL_SetAttribute(SDL_GL_DEPTH_SIZE, 24);

        long flags = SDL_WINDOW_OPENGL | SDL_WINDOW_RESIZABLE;   
        handle = SDL_CreateWindow(title, width, height, flags);
        if (handle == 0) {
            throw new LWJGLException("SDL_CreateWindow failed: " + SDL_GetError());
        }

        long context = SDL_GL_CreateContext(handle);
        if (context == 0) {
            SDL_DestroyWindow(handle);
            handle = -1;
            throw new LWJGLException("SDL_GL_CreateContext failed: " + SDL_GetError());
        }

        SDL_GL_MakeCurrent(handle, context);
        SDL_GL_SetSwapInterval(1);

        if (fullscreen) {
            SDL_SetWindowFullscreen(handle, true);
        }

        try (MemoryStack ms = stackPush()) {
            IntBuffer w = ms.mallocInt(1);
            IntBuffer h = ms.mallocInt(1);
            SDL_GetWindowSizeInPixels(handle, w, h);
            framebufferWidth = Math.max(1, w.get(0));
            framebufferHeight = Math.max(1, h.get(0));
        }

        resizePending = true;

        SDL_ShowWindow(handle);
        SDL_RaiseWindow(handle);
        SDL_PumpEvents();
        focused = (SDL_GetWindowFlags(handle) & SDL_WINDOW_INPUT_FOCUS) != 0;

        System.out.println("(SDL) Created.");

        GL.createCapabilities();

        if (cachedIcons != null) {
            setIcon(cachedIcons);
        }
    }

    private void onFramebufferResize(int w, int h) {
        if (w == 0 || h == 0) {
            minimized = true;
            return;
        }
        minimized = false;
        if (framebufferWidth != w || framebufferHeight != h) {
            framebufferWidth = w;
            framebufferHeight = h;
            windowResized = true;
        }
    }

    public void destroy() {
        if (handle > 0) {
            SDL_GL_DestroyContext(handle);
            SDL_DestroyWindow(handle);
        }
        handle = -1;
        if (SDL_WasInit(SDL_INIT_VIDEO) != 0) {
            SDL_QuitSubSystem(SDL_INIT_VIDEO);
        }
        SDL_Quit();
        if (event != null) {
            event.free();
            event = null;
        }
        videoInitialized = false;
        closeRequested = false;
        fullscreen = false;
        minimized = false;
    }

    public boolean isCreated() {
        return handle > 0;
    }

    public boolean isActive() {
        return focused;
    }

    public void setResizable(boolean resizable) {
        if (isCreated()) {
            SDL_SetWindowResizable(handle, resizable);
        }
    }

    public void setLocation(int x, int y) {
        if (isCreated()) {
            SDL_SetWindowPosition(handle, x, y);
        }
    }

    public boolean wasResized() {
        resizeChecked = true;
        return windowResized;
    }

    public boolean isVisible() {
        return !minimized;
    }

    private void resizeCallback(long window, int w, int h) {
        if (window == handle) {
            windowResized = true;
            width = w;
            height = h;
        }
    }

    public void makeCurrent() {
        SDL_GL_MakeCurrent(handle, SDL_GL_GetCurrentContext());
    }

    public void swapBuffers() {
        SDL_GL_SwapWindow(handle);
    }
}   