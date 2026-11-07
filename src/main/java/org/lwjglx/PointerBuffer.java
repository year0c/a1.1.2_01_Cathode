package org.lwjglx;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class PointerBuffer {

    private final long address;
    private final int capacity;
    private int position;
    private int limit;

    public PointerBuffer(int capacity) {
        this.capacity = capacity;
        this.limit = capacity;
        this.address = MemoryUtil.nmemAlloc(capacity * 8);
    }

    public PointerBuffer put(long value) {
        MemoryUtil.memPutLong(address + (long) position * 8, value);
        position++;
        return this;
    }

    public PointerBuffer put(int index, long value) {
        MemoryUtil.memPutLong(address + (long) index * 8, value);
        return this;
    }

    public long get() {
        return MemoryUtil.memGetLong(address + (long) position * 8);
    }

    public long get(int index) {
        return MemoryUtil.memGetLong(address + (long) index * 8);
    }

    public long address() {
        return address;
    }

    public long address(int index) {
        return address + (long) index * 8;
    }

    public int remaining() {
        return limit - position;
    }

    public int position() {
        return position;
    }

    public int position(int newPosition) {
        position = newPosition;
        return position;
    }

    public int limit() {
        return limit;
    }

    public int limit(int newLimit) {
        limit = newLimit;
        return limit;
    }

    public int capacity() {
        return capacity;
    }

    public void free() {
        MemoryUtil.nmemFree(address);
    }
}   