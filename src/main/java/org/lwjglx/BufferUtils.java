package org.lwjglx;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

public class BufferUtils {

    public static long getBufferAddress(Buffer buffer) {
        if (buffer instanceof ByteBuffer) return MemoryUtil.memAddress((ByteBuffer) buffer, 0);
        if (buffer instanceof ShortBuffer) return MemoryUtil.memAddress((ShortBuffer) buffer, 0);
        if (buffer instanceof IntBuffer) return MemoryUtil.memAddress((IntBuffer) buffer, 0);
        if (buffer instanceof LongBuffer) return MemoryUtil.memAddress((LongBuffer) buffer, 0);
        if (buffer instanceof FloatBuffer) return MemoryUtil.memAddress((FloatBuffer) buffer, 0);
        if (buffer instanceof DoubleBuffer) return MemoryUtil.memAddress((DoubleBuffer) buffer, 0);
        return 0;
    }

    public static ByteBuffer allocateBytes(int size) {
        return ByteBuffer.allocateDirect(size);
    }

    public static ShortBuffer allocateShorts(int size) {
        return ByteBuffer.allocateDirect(size * 2).asShortBuffer();
    }

    public static IntBuffer allocateInts(int size) {
        return ByteBuffer.allocateDirect(size * 4).asIntBuffer();
    }

    public static LongBuffer allocateLongs(int size) {
        return ByteBuffer.allocateDirect(size * 8).asLongBuffer();
    }

    public static FloatBuffer allocateFloats(int size) {
        return ByteBuffer.allocateDirect(size * 4).asFloatBuffer();
    }

    public static DoubleBuffer allocateDoubles(int size) {
        return ByteBuffer.allocateDirect(size * 8).asDoubleBuffer();
    }

    public static ByteBuffer createByteBuffer(int size) {
        return ByteBuffer.allocateDirect(size);
    }

    public static ByteBuffer createByteBuffer(long address, int size) {
        return MemoryUtil.memAlloc(size);
    }

    public static ShortBuffer createShortBuffer(long address, int size) {
        return MemoryUtil.memAllocShort(size);
    }

    public static IntBuffer createIntBuffer(long address, int size) {
        return MemoryUtil.memAllocInt(size);
    }

    public static LongBuffer createLongBuffer(long address, int size) {
        return MemoryUtil.memAllocLong(size);
    }

    public static FloatBuffer createFloatBuffer(long address, int size) {
        return MemoryUtil.memAllocFloat(size);
    }

    public static DoubleBuffer createDoubleBuffer(long address, int size) {
        return MemoryUtil.memAllocDouble(size);
    }

    public static PointerBuffer createPointerBuffer(int size) {
        return PointerBuffer.allocateDirect(size);
    }   
}   