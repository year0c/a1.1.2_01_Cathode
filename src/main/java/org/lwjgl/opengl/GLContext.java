package org.lwjgl.opengl;

public class GLContext {
    public static GLProfile getShareableGLProfile() { return null; }
    public static GLProfile getSystemGLProfile() { return null; }
    public static GLProfile getSystemGLProfile(String s) { return null; }
    public static GLProfile getSystemGLProfiles() { return null; }
    public static boolean isProfileSupported(GLProfile profile) { return true; }
    public static GLProfile[] getAvailableGLProfiles() { return new GLProfile[0]; }
    public static GLProfile[] getAvailableGLProfiles(String s) { return new GLProfile[0]; }
    public static ContextCapabilities getCapabilities() { return new ContextCapabilities(); }
}   