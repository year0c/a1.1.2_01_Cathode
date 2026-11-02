package org.lwjglx;

import org.lwjglx.PointerBuffer;
import org.lwjgl.system.CustomBuffer;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import org.jspecify.annotations.Nullable;

public class PointerBuffer extends CustomBuffer<PointerBuffer> {

    public PointerBuffer(int capacity) {
        super(MemoryUtil.nmemAlloc(capacity * 8), null, -1, 0, capacity, capacity);
    }

    @Override
    protected PointerBuffer self() { return this; }

    @Override
    public int sizeof() { return 8; }

    @Override
    protected PointerBuffer create(long address, @Nullable ByteBuffer container, int mark, int position, int limit, int capacity) {
        PointerBuffer buf = new PointerBuffer(capacity);
        buf.position(position);
        buf.limit(limit);
        return buf;
    }

    public static PointerBuffer allocateDirect(int capacity) {
        return new PointerBuffer(capacity);
    }

    public long address() {
        return address(0);
    }

    public long address(int index) {
        return super.address(index);
    }

    public long get() {
        int idx = nextGetIndex();
        return MemoryUtil.memGetLong(address(idx));
    }

    public long get(int index) {
        return MemoryUtil.memGetLong(address(index));
    }

    public PointerBuffer put(long value) {
        int idx = nextPutIndex();
        MemoryUtil.memPutLong(address(idx), value);
        return this;
    }

    public PointerBuffer put(int index, long value) {
        MemoryUtil.memPutLong(address(index), value);
        return this;
    }

    public PointerBuffer put(PointerBuffer src) {
        for (int i = 0; i < src.remaining(); i++) {
            put(src.get());
        }
        return this;
    }

    public void free() {
        MemoryUtil.nmemFree(address());
    }
}   