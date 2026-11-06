package org.lwjgl.opengl.func.GL11;

import org.lwjgl.opengl.util.GL11.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.opengl.util.GL11.GL11Constants.*;

public class GL11GLFunc {

    // --- Matrix ---

    public static void glMatrixMode(int mode) {
        GL11State.currentMatrixMode = mode;
    }

    public static void glLoadIdentity() {
        float[] m = GL11UtilFunc.currentMatrix();
        GL11UtilFunc.identity(m);
    }

    public static void glLoadMatrixf(FloatBuffer m) {
        float[] cur = GL11UtilFunc.currentMatrix();
        for (int i = 0; i < 16; i++) cur[i] = m.get(i);
    }

    public static void glMultMatrixf(FloatBuffer m) {
        float[] cur = GL11UtilFunc.currentMatrix();
        float[] result = new float[16];
        GL11UtilFunc.multiplyMatrices(cur, m, result);
        System.arraycopy(result, 0, cur, 0, 16);
    }

    public static void glTranslatef(float x, float y, float z) {
        float[] t = {1,0,0,0, 0,1,0,0, 0,0,1,0, x,y,z,1};
        float[] cur = GL11UtilFunc.currentMatrix();
        float[] result = new float[16];
        GL11UtilFunc.multiplyMatrices(cur, t, result);
        System.arraycopy(result, 0, cur, 0, 16);
    }

    public static void glRotatef(float angle, float x, float y, float z) {
        float rad = (float) Math.toRadians(angle);
        float c = (float) Math.cos(rad);
        float s = (float) Math.sin(rad);
        float t = 1.0f - c;
        float len = (float) Math.sqrt(x*x + y*y + z*z);
        if (len < 1e-6f) return;
        x /= len; y /= len; z /= len;
        float[] r = {
            t*x*x+c,   t*x*y+s*z, t*x*z-s*y, 0,
            t*x*y-s*z, t*y*y+c,   t*y*z+s*x, 0,
            t*x*z+s*y, t*y*z-s*x, t*z*z+c,   0,
            0,         0,         0,         1
        };
        float[] cur = GL11UtilFunc.currentMatrix();
        float[] result = new float[16];
        GL11UtilFunc.multiplyMatrices(cur, r, result);
        System.arraycopy(result, 0, cur, 0, 16);
    }

    public static void glScalef(float x, float y, float z) {
        float[] s = {x,0,0,0, 0,y,0,0, 0,0,z,0, 0,0,0,1};
        float[] cur = GL11UtilFunc.currentMatrix();
        float[] result = new float[16];
        GL11UtilFunc.multiplyMatrices(cur, s, result);
        System.arraycopy(result, 0, cur, 0, 16);
    }

    public static void glPushMatrix() {
        if (GL11State.currentMatrixMode == GL_MODELVIEW) {
            if (GL11State.modelviewSp < 31) {
                GL11State.modelviewSp++;
                System.arraycopy(GL11State.modelviewStack[GL11State.modelviewSp-1], 0, GL11State.modelviewStack[GL11State.modelviewSp], 0, 16);
            }
        } else if (GL11State.currentMatrixMode == GL_PROJECTION) {
            if (GL11State.projectionSp < 31) {
                GL11State.projectionSp++;
                System.arraycopy(GL11State.projectionStack[GL11State.projectionSp-1], 0, GL11State.projectionStack[GL11State.projectionSp], 0, 16);
            }
        } else if (GL11State.currentMatrixMode == GL_TEXTURE) {
            if (GL11State.textureSp < 31) {
                GL11State.textureSp++;
                System.arraycopy(GL11State.textureStack[GL11State.textureSp-1], 0, GL11State.textureStack[GL11State.textureSp], 0, 16);
            }
        }
    }

    public static void glPopMatrix() {
        if (GL11State.currentMatrixMode == GL_MODELVIEW) {
            if (GL11State.modelviewSp > 0) GL11State.modelviewSp--;
        } else if (GL11State.currentMatrixMode == GL_PROJECTION) {
            if (GL11State.projectionSp > 0) GL11State.projectionSp--;
        } else if (GL11State.currentMatrixMode == GL_TEXTURE) {
            if (GL11State.textureSp > 0) GL11State.textureSp--;
        }
    }

    public static void glOrtho(double left, double right, double bottom, double top, double zNear, double zFar) {
        float[] m = {
            (float)(2.0 / (right - left)), 0, 0, (float)(-(right + left) / (right - left)),
            0, (float)(2.0 / (top - bottom)), 0, (float)(-(top + bottom) / (top - bottom)),
            0, 0, (float)(-2.0 / (zFar - zNear)), (float)(-(zFar + zNear) / (zFar - zNear)),
            0, 0, 0, 1
        };
        float[] cur = GL11UtilFunc.currentMatrix();
        float[] result = new float[16];
        GL11UtilFunc.multiplyMatrices(cur, m, result);
        System.arraycopy(result, 0, cur, 0, 16);
    }

    public static void glFrustum(double left, double right, double bottom, double top, double zNear, double zFar) {
        float[] m = {
            (float)(2.0 * zNear / (right - left)), 0, (float)((right + left) / (right - left)), 0,
            0, (float)(2.0 * zNear / (top - bottom)), 0, (float)((top + bottom) / (top - bottom)), 0,
            0, 0, (float)(-(zFar + zNear) / (zFar - zNear)), (float)(-2.0 * zFar * zNear / (zFar - zNear)),
            0, 0, -1, 0
        };
        float[] cur = GL11UtilFunc.currentMatrix();
        float[] result = new float[16];
        GL11UtilFunc.multiplyMatrices(cur, m, result);
        System.arraycopy(result, 0, cur, 0, 16);
    }

    public static void glPerspective(double fovy, double aspect, double zNear, double zFar) {
        double theta = Math.toRadians(fovy);
        double f = 1.0 / Math.tan(theta / 2.0);
        float[] m = {
            (float)(f / aspect), 0, 0, 0,
            0, (float) f, 0, 0,
            0, 0, (float)(-(zFar + zNear) / (zFar - zNear)), (float)(-2.0 * zFar * zNear / (zFar - zNear)),
            0, 0, -1, 0
        };
        float[] cur = GL11UtilFunc.currentMatrix();
        float[] result = new float[16];
        GL11UtilFunc.multiplyMatrices(cur, m, result);
        System.arraycopy(result, 0, cur, 0, 16);
    }

    // --- State ---

    public static void glEnable(int cap) {
        if (cap < GL11State.enabled.length) GL11State.enabled[cap] = true;
    }

    public static void glDisable(int cap) {
        if (cap < GL11State.enabled.length) GL11State.enabled[cap] = false;
    }

    public static boolean glIsEnabled(int cap) {
        return cap < GL11State.enabled.length && GL11State.enabled[cap];
    }

    public static void glBlendFunc(int sfactor, int dfactor) {
        GL11State.blendSrc = sfactor;
        GL11State.blendDst = dfactor;
    }

    public static void glDepthFunc(int func) {
        GL11State.depthFunc = func;
    }

    public static void glDepthMask(boolean mask) {
        GL11State.depthMask = mask;
    }

    public static void glCullFace(int mode) {
        GL11State.cullFaceMode = mode;
    }

    public static void glAlphaFunc(int func, float ref) {
        GL11State.alphaFuncMode = func;
        GL11State.alphaRef = ref;
    }

    public static void glFogf(int pname, float param) {
        switch (pname) {
            case GL_FOG_MODE: GL11State.fogMode = (int) param; break;
            case GL_FOG_START: GL11State.fogStart = param; break;
            case GL_FOG_END: GL11State.fogEnd = param; break;
            case GL_FOG_DENSITY: GL11State.fogDensity = param; break;
        }
    }

    public static void glFogfv(int pname, FloatBuffer params) {
        switch (pname) {
            case GL_FOG_COLOR:
                GL11State.fogColorR = params.get(0);
                GL11State.fogColorG = params.get(1);
                GL11State.fogColorB = params.get(2);
                if (params.capacity() > 3) GL11State.fogColorA = params.get(3);
                break;
        }
    }

    public static void glViewport(int x, int y, int w, int h) {
        GL11State.viewportX = x; GL11State.viewportY = y;
        GL11State.viewportW = w; GL11State.viewportH = h;
    }

    public static void glScissor(int x, int y, int w, int h) {
        GL11State.scissorX = x; GL11State.scissorY = y;
        GL11State.scissorW = w; GL11State.scissorH = h;
    }

    public static void glClearColor(float r, float g, float b, float a) {
        GL11State.clearColorR = r; GL11State.clearColorG = g;
        GL11State.clearColorB = b; GL11State.clearColorA = a;
    }

    public static void glClearDepth(double depth) {
        GL11State.clearDepth = depth;
    }

    public static void glClear(int mask) {
        if (!GL11State.inBegin && GL11State.vertexCount == 0) {
            try { GL11FrameFunc.beginFrame(); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public static void glPolygonOffset(float factor, float units) {
        GL11State.polygonOffsetFactor = factor;
        GL11State.polygonOffsetUnits = units;
    }

    public static String glGetString(int name) {
        switch (name) {
            case GL_VENDOR: return "LWJGL";
            case GL_RENDERER: return "Cathode Vulkan";
            case GL_VERSION: return "1.1.0 Cathode";
            case GL_SHADING_LANGUAGE_VERSION: return "1.10";
            default: return "";
        }
    }

    public static void glGetIntegerv(int pname, IntBuffer params) {
        switch (pname) {
            case GL_MAX_TEXTURE_SIZE: params.put(0, 16384); break;
            case GL_DEPTH_BITS: params.put(0, 24); break;
            case GL_VIEWPORT:
                params.put(0, GL11State.viewportX); params.put(1, GL11State.viewportY);
                params.put(2, GL11State.viewportW); params.put(3, GL11State.viewportH);
                break;
            case GL_ALIASED_POINT_SIZE_RANGE:
                params.put(0, 1); params.put(1, 255);
                break;
            default: params.put(0, 0);
        }
    }

    public static void glGetFloatv(int pname, FloatBuffer params) {
        switch (pname) {
            case GL_CURRENT_COLOR:
                params.put(0, GL11State.curR); params.put(1, GL11State.curG);
                params.put(2, GL11State.curB); params.put(3, GL11State.curA);
                break;
            case GL_CURRENT_TEXTURE_COORDS:
                params.put(0, GL11State.curU); params.put(1, GL11State.curV);
                break;
            case GL_CURRENT_NORMAL:
                params.put(0, GL11State.curNx); params.put(1, GL11State.curNy); params.put(2, GL11State.curNz);
                break;
            default:
                for (int i = 0; i < params.capacity(); i++) params.put(i, 0f);
        }
    }

    // --- Textures ---

    public static void glGenTextures(int n, IntBuffer ids) {
        for (int i = 0; i < n; i++) ids.put(i, GL11State.nextTextureId++);
    }

    public static void glBindTexture(int target, int texId) {
        GL11State.boundTexture = texId;
    }

    public static void glDeleteTextures(int n, IntBuffer ids) {
        for (int i = 0; i < n; i++)
            GL11TextureFunc.deleteTexture(ids.get(i));
    }

    public static void glTexImage2D(int target, int level, int internalFormat, int w, int h,
                                     int border, int format, int type, java.nio.ByteBuffer data) {
        GL11TextureFunc.uploadTexture(GL11State.boundTexture, w, h, format, type, data.array());
    }

    public static void glTexImage2D(int target, int level, int internalFormat, int w, int h,
                                     int border, int format, int type, byte[] data) {
        GL11TextureFunc.uploadTexture(GL11State.boundTexture, w, h, format, type, data);
    }

    public static void glTexParameteri(int target, int pname, int param) {
        if (pname < GL11State.textureParams.length) GL11State.textureParams[pname] = param;
    }

    // --- Immediate Mode ---

    public static void glBegin(int mode) {
        GL11State.inBegin = true;
        GL11State.beginMode = mode;
        GL11State.vertexCount = 0;
        GL11State.vertexData.clear();
    }

    public static void glEnd() {
        GL11State.inBegin = false;
    }

    public static void glVertex3f(float x, float y, float z) {
        writeVertex(x, y, z, 1f);
    }

    public static void glVertex2f(float x, float y) {
        writeVertex(x, y, 0f, 1f);
    }

    public static void glVertex4f(float x, float y, float z, float w) {
        writeVertex(x, y, z, w);
    }

    private static void writeVertex(float x, float y, float z, float w) {
        if (!GL11State.inBegin || GL11State.vertexCount >= GL11State.MAX_VERTICES) return;
        GL11State.vertexData.put(x);
        GL11State.vertexData.put(y);
        GL11State.vertexData.put(z);
        GL11State.vertexData.put(w);
        GL11State.vertexData.put(GL11State.curR);
        GL11State.vertexData.put(GL11State.curG);
        GL11State.vertexData.put(GL11State.curB);
        GL11State.vertexData.put(GL11State.curA);
        GL11State.vertexData.put(GL11State.curU);
        GL11State.vertexData.put(GL11State.curV);
        GL11State.vertexData.put(GL11State.curNx);
        GL11State.vertexData.put(GL11State.curNy);
        GL11State.vertexData.put(GL11State.curNz);
        GL11State.vertexCount++;
    }

    public static void glColor3f(float r, float g, float b) {
        GL11State.curR = r; GL11State.curG = g; GL11State.curB = b;
    }

    public static void glColor4f(float r, float g, float b, float a) {
        GL11State.curR = r; GL11State.curG = g; GL11State.curB = b; GL11State.curA = a;
    }

    public static void glColor3ub(byte r, byte g, byte b) {
        GL11State.curR = (r & 0xFF) / 255f;
        GL11State.curG = (g & 0xFF) / 255f;
        GL11State.curB = (b & 0xFF) / 255f;
    }

    public static void glColor4ub(byte r, byte g, byte b, byte a) {
        GL11State.curR = (r & 0xFF) / 255f;
        GL11State.curG = (g & 0xFF) / 255f;
        GL11State.curB = (b & 0xFF) / 255f;
        GL11State.curA = (a & 0xFF) / 255f;
    }

    public static void glTexCoord2f(float u, float v) {
        GL11State.curU = u; GL11State.curV = v;
    }

    public static void glTexCoord2d(double u, double v) {
        GL11State.curU = (float) u; GL11State.curV = (float) v;
    }

    public static void glNormal3f(float x, float y, float z) {
        GL11State.curNx = x; GL11State.curNy = y; GL11State.curNz = z;
    }

    // --- Client State (no-op) ---

    public static void glClientActiveTexture(int which) { }
    public static void glActiveTexture(int which) { }
    public static void glClientVertexPointer(int size, int type, int stride, FloatBuffer buf) { }
    public static void glClientColorPointer(int size, int type, int stride, FloatBuffer buf) { }
    public static void glClientTexCoordPointer(int size, int type, int stride, FloatBuffer buf) { }
    public static void glClientNormalPointer(int type, int stride, FloatBuffer buf) { }
    public static void glArrayElement(int i) { }
    public static void glEnableClientState(int cap) { }
    public static void glDisableClientState(int cap) { }

    // --- Draw ---

    public static void glDrawArrays(int mode, int first, int count) { }
    public static void glDrawElements(int mode, int count, int type, IntBuffer indices) { }

    // --- Misc ---

    public static void glFinish() {
        if (!GL11Vars.initialized) return;
        try { GL11FrameFunc.endFrame(); } catch (Exception e) { e.printStackTrace(); }
    }

    public static void glFlush() { }
    public static void glHint(int target, int mode) { }
    public static void glPixelStorei(int pname, int param) { }
    public static void glShadeModel(int mode) { }
    public static void glLineWidth(float width) { }
    public static void glPointSize(float size) { }
    public static void glFrontFace(int mode) { }
    public static void glLogicOp(int opcode) { }
}   