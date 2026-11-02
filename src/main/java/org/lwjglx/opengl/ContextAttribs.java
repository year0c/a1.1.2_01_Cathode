package org.lwjglx.opengl;

import org.lwjglx.opengl.ContextAttribs;

public class ContextAttribs {
    private int majorVersion = 1;
    private int minorVersion = 0;
    private boolean profileCore = false;
    private boolean forwardCompatible = false;

    public ContextAttribs() {}

    public ContextAttribs(int majorVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public ContextAttribs withProfileCore(boolean core) { profileCore = core; return this; }
    public ContextAttribs withForwardCompatible(boolean forward) { forwardCompatible = forward; return this; }

    public int getMajorVersion() { return majorVersion; }
    public int getMinorVersion() { return minorVersion; }
    public boolean isProfileCore() { return profileCore; }
    public boolean isForwardCompatible() { return forwardCompatible; }
}   