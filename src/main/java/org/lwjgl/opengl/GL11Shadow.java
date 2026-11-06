package org.lwjgl.opengl;

import org.lwjgl.opengl.util.GL11.GL11Vars;
import org.lwjgl.opengl.func.GL11.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GL11Shadow {

    // Debug toggle — set to false to silence all [GL11] prints
    public static void setDebug(boolean enabled) {
        GL11Vars.debug = enabled;
    }

    // --- Init / Frame ---

    public static void initVulkan(long sdlWindow, int w, int h) throws Exception {
        GL11InitFunc.initVulkan(sdlWindow, w, h);
    }

    public static void beginFrame() throws Exception {
        GL11FrameFunc.beginFrame();
    }

    public static void endFrame() throws Exception {
        GL11FrameFunc.endFrame();
    }

    public static boolean isInitialized() {
        return GL11UtilFunc.isInitialized();
    }

    // --- Matrix ---

    public static void glMatrixMode(int mode) { GL11GLFunc.glMatrixMode(mode); }
    public static void glLoadIdentity() { GL11GLFunc.glLoadIdentity(); }
    public static void glLoadMatrixf(FloatBuffer m) { GL11GLFunc.glLoadMatrixf(m); }
    public static void glMultMatrixf(FloatBuffer m) { GL11GLFunc.glMultMatrixf(m); }
    public static void glTranslatef(float x, float y, float z) { GL11GLFunc.glTranslatef(x, y, z); }
    public static void glRotatef(float angle, float x, float y, float z) { GL11GLFunc.glRotatef(angle, x, y, z); }
    public static void glScalef(float x, float y, float z) { GL11GLFunc.glScalef(x, y, z); }
    public static void glPushMatrix() { GL11GLFunc.glPushMatrix(); }
    public static void glPopMatrix() { GL11GLFunc.glPopMatrix(); }
    public static void glOrtho(double l, double r, double b, double t, double n, double f) { GL11GLFunc.glOrtho(l, r, b, t, n, f); }
    public static void glFrustum(double l, double r, double b, double t, double n, double f) { GL11GLFunc.glFrustum(l, r, b, t, n, f); }
    public static void glPerspective(double fovy, double aspect, double n, double f) { GL11GLFunc.glPerspective(fovy, aspect, n, f); }

    // --- State ---

    public static void glEnable(int cap) { GL11GLFunc.glEnable(cap); }
    public static void glDisable(int cap) { GL11GLFunc.glDisable(cap); }
    public static boolean glIsEnabled(int cap) { return GL11GLFunc.glIsEnabled(cap); }
    public static void glBlendFunc(int s, int d) { GL11GLFunc.glBlendFunc(s, d); }
    public static void glDepthFunc(int f) { GL11GLFunc.glDepthFunc(f); }
    public static void glDepthMask(boolean m) { GL11GLFunc.glDepthMask(m); }
    public static void glCullFace(int m) { GL11GLFunc.glCullFace(m); }
    public static void glAlphaFunc(int f, float r) { GL11GLFunc.glAlphaFunc(f, r); }
    public static void glFogf(int p, float v) { GL11GLFunc.glFogf(p, v); }
    public static void glFogfv(int p, FloatBuffer v) { GL11GLFunc.glFogfv(p, v); }
    public static void glViewport(int x, int y, int w, int h) { GL11GLFunc.glViewport(x, y, w, h); }
    public static void glScissor(int x, int y, int w, int h) { GL11GLFunc.glScissor(x, y, w, h); }
    public static void glClearColor(float r, float g, float b, float a) { GL11GLFunc.glClearColor(r, g, b, a); }
    public static void glClearDepth(double d) { GL11GLFunc.glClearDepth(d); }
    public static void glClear(int mask) { GL11GLFunc.glClear(mask); }
    public static void glPolygonOffset(float f, float u) { GL11GLFunc.glPolygonOffset(f, u); }
    public static String glGetString(int n) { return GL11GLFunc.glGetString(n); }
    public static void glGetIntegerv(int p, IntBuffer v) { GL11GLFunc.glGetIntegerv(p, v); }
    public static void glGetFloatv(int p, FloatBuffer v) { GL11GLFunc.glGetFloatv(p, v); }

    // --- Textures ---

    public static void glGenTextures(int n, IntBuffer ids) { GL11GLFunc.glGenTextures(n, ids); }
    public static void glBindTexture(int t, int id) { GL11GLFunc.glBindTexture(t, id); }
    public static void glDeleteTextures(int n, IntBuffer ids) { GL11GLFunc.glDeleteTextures(n, ids); }
    public static void glTexImage2D(int t, int l, int ifmt, int w, int h, int b, int fmt, int type, java.nio.ByteBuffer d) { GL11GLFunc.glTexImage2D(t, l, ifmt, w, h, b, fmt, type, d); }
    public static void glTexImage2D(int t, int l, int ifmt, int w, int h, int b, int fmt, int type, byte[] d) { GL11GLFunc.glTexImage2D(t, l, ifmt, w, h, b, fmt, type, d); }
    public static void glTexParameteri(int t, int p, int v) { GL11GLFunc.glTexParameteri(t, p, v); }

    // --- Immediate Mode ---

    public static void glBegin(int mode) { GL11GLFunc.glBegin(mode); }
    public static void glEnd() { GL11GLFunc.glEnd(); }
    public static void glVertex3f(float x, float y, float z) { GL11GLFunc.glVertex3f(x, y, z); }
    public static void glVertex2f(float x, float y) { GL11GLFunc.glVertex2f(x, y); }
    public static void glVertex4f(float x, float y, float z, float w) { GL11GLFunc.glVertex4f(x, y, z, w); }
    public static void glColor3f(float r, float g, float b) { GL11GLFunc.glColor3f(r, g, b); }
    public static void glColor4f(float r, float g, float b, float a) { GL11GLFunc.glColor4f(r, g, b, a); }
    public static void glColor3ub(byte r, byte g, byte b) { GL11GLFunc.glColor3ub(r, g, b); }
    public static void glColor4ub(byte r, byte g, byte b, byte a) { GL11GLFunc.glColor4ub(r, g, b, a); }
    public static void glTexCoord2f(float u, float v) { GL11GLFunc.glTexCoord2f(u, v); }
    public static void glTexCoord2d(double u, double v) { GL11GLFunc.glTexCoord2d(u, v); }
    public static void glNormal3f(float x, float y, float z) { GL11GLFunc.glNormal3f(x, y, z); }

    // --- Client State (no-op) ---

    public static void glClientActiveTexture(int w) { }
    public static void glActiveTexture(int w) { }
    public static void glClientVertexPointer(int s, int t, int st, FloatBuffer b) { }
    public static void glClientColorPointer(int s, int t, int st, FloatBuffer b) { }
    public static void glClientTexCoordPointer(int s, int t, int st, FloatBuffer b) { }
    public static void glClientNormalPointer(int t, int st, FloatBuffer b) { }
    public static void glArrayElement(int i) { }
    public static void glEnableClientState(int c) { }
    public static void glDisableClientState(int c) { }

    // --- Draw ---

    public static void glDrawArrays(int m, int f, int c) { }
    public static void glDrawElements(int m, int c, int t, IntBuffer i) { }

    // --- Misc ---

    public static void glFinish() { GL11GLFunc.glFinish(); }
    public static void glFlush() { }
    public static void glHint(int t, int m) { }
    public static void glPixelStorei(int p, int v) { }
    public static void glShadeModel(int m) { }
    public static void glLineWidth(float w) { }
    public static void glPointSize(float s) { }
    public static void glFrontFace(int m) { }
    public static void glLogicOp(int o) { }

    static {
        System.out.println("[GL11-SHADOW] class loaded");
    }
}   