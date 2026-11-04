package org.lwjglx.opengl;

public class ContextAttribs {
    public ContextAttribs() {}

    public ContextAttribs(int majorVersion, int minorVersion) {}

    public ContextAttribs withProfileCore(boolean core) { return this; }
    public ContextAttribs withForwardCompatible(boolean forward) { return this; }
}   