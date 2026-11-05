package org.lwjgl.opengl.util.GL11;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.util.GL11.GL11Constants.*;

public class GL11State {
    // Matrix stacks
    public static final float[][] modelviewStack = new float[32][16];
    public static final float[][] projectionStack = new float[32][16];
    public static final float[][] textureStack = new float[32][16];
    public static int modelviewSp = 0, projectionSp = 0, textureSp = 0;
    public static int currentMatrixMode = GL_MODELVIEW;

    // State
    public static final boolean[] enabled = new boolean[4096];
    public static int blendSrc = GL_SRC_ALPHA;
    public static int blendDst = GL_ONE_MINUS_SRC_ALPHA;
    public static int depthFunc = GL_LEQUAL;
    public static boolean depthMask = true;
    public static int cullFaceMode = GL_BACK;
    public static float alphaRef = 0.0f;
    public static int alphaFuncMode = GL_GREATER;

    // Fog
    public static float fogStart = 0f, fogEnd = 1f, fogDensity = 1f;
    public static float fogColorR = 1f, fogColorG = 1f, fogColorB = 1f, fogColorA = 1f;
    public static int fogMode = GL_LINEAR;

    // Viewport / Scissor
    public static int viewportX = 0, viewportY = 0, viewportW = 854, viewportH = 480;
    public static int scissorX = 0, scissorY = 0, scissorW = 854, scissorH = 480;

    // Clear
    public static float clearColorR = 0, clearColorG = 0, clearColorB = 0, clearColorA = 1;
    public static double clearDepth = 1.0;

    // Polygon offset
    public static float polygonOffsetFactor = 0f, polygonOffsetUnits = 0f;

    // Texture
    public static int boundTexture = 0;
    public static int[] textureParams = new int[256];
    public static int nextTextureId = 1;

    // Immediate mode
    public static boolean inBegin = false;
    public static int beginMode = 0;
    public static int vertexCount = 0;
    public static final int MAX_VERTICES = 65536;
    public static final int FLOATS_PER_VERTEX = 12;
    public static final FloatBuffer vertexData = BufferUtils.createFloatBuffer(MAX_VERTICES * FLOATS_PER_VERTEX);

    // Current vertex attributes
    public static float curR = 1f, curG = 1f, curB = 1f, curA = 1f;
    public static float curU = 0f, curV = 0f;
    public static float curNx = 0f, curNy = 1f, curNz = 0f;

    static {
        for (int i = 0; i < 16; i++) {
            modelviewStack[0][i] = (i % 5 == 0) ? 1f : 0f;
            projectionStack[0][i] = (i % 5 == 0) ? 1f : 0f;
            textureStack[0][i] = (i % 5 == 0) ? 1f : 0f;
        }
    }
}   