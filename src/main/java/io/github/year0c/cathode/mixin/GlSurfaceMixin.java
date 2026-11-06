package io.github.year0c.cathode.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import pl.tomgirl.pylon.window.DisplaySdl;
import pl.tomgirl.pylon.window.GlSurface;

import static org.lwjgl.sdl.SDLError.SDL_GetError;
import static org.lwjgl.sdl.SDLProperties.*;
import static org.lwjgl.sdl.SDLVideo.*;

@Mixin(GlSurface.class)
public class GlSurfaceMixin {

    private long window;

    @Overwrite
    public long createWindow(String title, int width, int height, boolean resizable) {
        if (window != 0) {
            throw new IllegalStateException("Surface has already been created");
        }
        try {
            pl.tomgirl.pylon.window.MainThread.run(() -> createSdlWindow(title, width, height, resizable));
            return window;
        } catch (RuntimeException | Error throwable) {
            destroy();
            throw throwable;
        }
    }

    private void createSdlWindow(String title, int width, int height, boolean resizable) {
        int properties = (int) check(SDL_CreateProperties());
        try {
            check(SDL_SetNumberProperty(properties, SDL_PROP_WINDOW_CREATE_X_NUMBER, SDL_WINDOWPOS_CENTERED));
            check(SDL_SetNumberProperty(properties, SDL_PROP_WINDOW_CREATE_Y_NUMBER, SDL_WINDOWPOS_CENTERED));
            check(SDL_SetNumberProperty(properties, SDL_PROP_WINDOW_CREATE_WIDTH_NUMBER, width));
            check(SDL_SetNumberProperty(properties, SDL_PROP_WINDOW_CREATE_HEIGHT_NUMBER, height));
            check(SDL_SetStringProperty(properties, SDL_PROP_WINDOW_CREATE_TITLE_STRING, title));
            check(SDL_SetBooleanProperty(properties, SDL_PROP_WINDOW_CREATE_HIDDEN_BOOLEAN, true));
            check(SDL_SetBooleanProperty(properties, SDL_PROP_WINDOW_CREATE_RESIZABLE_BOOLEAN, resizable));
            check(SDL_SetBooleanProperty(properties, SDL_PROP_WINDOW_CREATE_HIGH_PIXEL_DENSITY_BOOLEAN, DisplaySdl.instance().isHighPixelDensity()));
            window = check(SDL_CreateWindowWithProperties(properties));
        } finally {
            SDL_DestroyProperties(properties);
        }
    }

    @Overwrite
    public void makeCurrent() { }

    @Overwrite
    public void releaseCurrent() { }

    @Overwrite
    public void swapBuffers() { }

    @Overwrite
    public void setVSyncEnabled(boolean enabled) { }

    @Overwrite
    public void destroy() {
        if (window != 0) {
            SDL_DestroyWindow(window);
            window = 0;
        }
    }

    private static boolean check(boolean success) {
        if (!success) throw new IllegalStateException("SDL error: " + SDL_GetError());
        return success;
    }

    private static long check(long pointer) {
        if (pointer == 0) throw new IllegalStateException("SDL error: " + SDL_GetError());
        return pointer;
    }
}   