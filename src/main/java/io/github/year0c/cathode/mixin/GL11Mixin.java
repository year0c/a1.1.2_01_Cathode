package io.github.year0c.cathode.mixin;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.util.GL11.GL11State;
import org.lwjgl.opengl.util.GL11.GL11Vars;
import org.lwjgl.opengl.func.GL11.GL11GLFunc;
import org.lwjgl.opengl.func.GL11.GL11FrameFunc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@Mixin(GL11.class)
public class GL11Mixin {

    // --- Frame boundary ---

    @Overwrite
    public static void glClear(int mask) {
        GL11GLFunc.glClear(mask);
        if (!GL11State.inBegin && GL11State.vertexCount == 0) {
            try { GL11FrameFunc.beginFrame(); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    @Overwrite
    public static void glFlush() {
        if (!GL11Vars.initialized) return;
        try { GL11FrameFunc.endFrame(); } catch (Exception e) { e.printStackTrace(); }
    }

    @Overwrite
    public static void glFinish() { }

    // --- Immediate mode ---

    @Overwrite
    public static void glBegin(int mode) { GL11GLFunc.glBegin(mode); }

    @Overwrite
    public static void glEnd() { GL11GLFunc.glEnd(); }

    @Overwrite
    public static void glVertex3f(float x, float y, float z) { GL11GLFunc.glVertex3f(x, y, z); }

    @Overwrite
    public static void glVertex2f(float x, float y) { GL11GLFunc.glVertex2f(x, y); }

    @Overwrite
    public static void glColor3f(float r, float g, float b) { GL11GLFunc.glColor3f(r, g, b); }

    @Overwrite
    public static void glColor4f(float r, float g, float b, float a) { GL11GLFunc.glColor4f(r, g, b, a); }

    @Overwrite
    public static void glColor3ub(byte r, byte g, byte b) { GL11GLFunc.glColor3ub(r, g, b); }

    @Overwrite
    public static void glColor4ub(byte r, byte g, byte b, byte a) { GL11GLFunc.glColor4ub(r, g, b, a); }

    @Overwrite
    public static void glTexCoord2f(float u, float v) { GL11GLFunc.glTexCoord2f(u, v); }

    @Overwrite
    public static void glNormal3f(float x, float y, float z) { GL11GLFunc.glNormal3f(x, y, z); }

    // --- Matrix ---

    @Overwrite
    public static void glMatrixMode(int mode) { GL11GLFunc.glMatrixMode(mode); }

    @Overwrite
    public static void glLoadIdentity() { GL11GLFunc.glLoadIdentity(); }

    @Overwrite
    public static void glTranslatef(float x, float y, float z) { GL11GLFunc.glTranslatef(x, y, z); }

    @Overwrite
    public static void glRotatef(float angle, float x, float y, float z) { GL11GLFunc.glRotatef(angle, x, y, z); }

    @Overwrite
    public static void glScalef(float x, float y, float z) { GL11GLFunc.glScalef(x, y, z); }

    @Overwrite
    public static void glPushMatrix() { GL11GLFunc.glPushMatrix(); }

    @Overwrite
    public static void glPopMatrix() { GL11GLFunc.glPopMatrix(); }

    @Overwrite
    public static void glOrtho(double l, double r, double b, double t, double n, double f) { GL11GLFunc.glOrtho(l, r, b, t, n, f); }

    // --- State ---

    @Overwrite
    public static void glEnable(int cap) { GL11GLFunc.glEnable(cap); }

    @Overwrite
    public static void glDisable(int cap) { GL11GLFunc.glDisable(cap); }

    @Overwrite
    public static void glBlendFunc(int s, int d) { GL11GLFunc.glBlendFunc(s, d); }

    @Overwrite
    public static void glDepthFunc(int f) { GL11GLFunc.glDepthFunc(f); }

    @Overwrite
    public static void glDepthMask(boolean m) { GL11GLFunc.glDepthMask(m); }

    @Overwrite
    public static void glCullFace(int m) { GL11GLFunc.glCullFace(m); }

    @Overwrite
    public static void glAlphaFunc(int f, float r) { GL11GLFunc.glAlphaFunc(f, r); }

    @Overwrite
    public static void glFogf(int p, float v) { GL11GLFunc.glFogf(p, v); }

    @Overwrite
    public static void glViewport(int x, int y, int w, int h) { GL11GLFunc.glViewport(x, y, w, h); }

    @Overwrite
    public static void glScissor(int x, int y, int w, int h) { GL11GLFunc.glScissor(x, y, w, h); }

    @Overwrite
    public static void glClearColor(float r, float g, float b, float a) { GL11GLFunc.glClearColor(r, g, b, a); }

    @Overwrite
    public static void glClearDepth(double d) { GL11GLFunc.glClearDepth(d); }

    // --- Query ---

    @Overwrite
    public static String glGetString(int n) { return GL11GLFunc.glGetString(n); }

    @Overwrite
    public static void glGetIntegerv(int p, IntBuffer v) { GL11GLFunc.glGetIntegerv(p, v); }

    @Overwrite
    public static void glGetFloatv(int p, FloatBuffer v) { GL11GLFunc.glGetFloatv(p, v); }
}   