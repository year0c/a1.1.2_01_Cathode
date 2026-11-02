package org.lwjgl.opengl;

public class GLProfile {
    public static final GLProfile ES10 = new GLProfile("ES10");
    public static final GLProfile ES20 = new GLProfile("ES20");
    public static final GLProfile ES30 = new GLProfile("ES30");
    public static final GLProfile ES31 = new GLProfile("ES31");
    public static final GLProfile ES32 = new GLProfile("ES32");
    public static final GLProfile ES33 = new GLProfile("ES33");
    public static final GLProfile GL11 = new GLProfile("GL11");
    public static final GLProfile GL20 = new GLProfile("GL20");
    public static final GLProfile GL21 = new GLProfile("GL21");
    public static final GLProfile GL30 = new GLProfile("GL30");
    public static final GLProfile GL31 = new GLProfile("GL31");
    public static final GLProfile GL32 = new GLProfile("GL32");
    public static final GLProfile GL32COMPAT = new GLProfile("GL32COMPAT");
    public static final GLProfile GL33 = new GLProfile("GL33");
    public static final GLProfile GL40 = new GLProfile("GL40");
    public static final GLProfile GL41 = new GLProfile("GL41");
    public static final GLProfile GL42 = new GLProfile("GL42");
    public static final GLProfile GL43 = new GLProfile("GL43");
    public static final GLProfile GL45 = new GLProfile("GL45");

    private final String name;

    private GLProfile(String name) { this.name = name; }

    public static GLProfile create(int[] version) {
        if (version[0] == 1 && version[1] <= 1) return GL11;
        if (version[0] == 2 && version[1] <= 0) return GL20;
        if (version[0] == 2 && version[1] <= 1) return GL21;
        if (version[0] == 3 && version[1] <= 0) return GL30;
        if (version[0] == 3 && version[1] <= 1) return GL31;
        if (version[0] == 3 && version[1] <= 2) return GL32;
        if (version[0] == 3 && version[1] <= 3) return GL33;
        if (version[0] == 4 && version[1] <= 0) return GL40;
        if (version[0] == 4 && version[1] <= 1) return GL41;
        if (version[0] == 4 && version[1] <= 2) return GL42;
        if (version[0] == 4 && version[1] <= 3) return GL43;
        return GL45;
    }

    public String getName() { return name; }

    @Override
    public String toString() { return name; }
}   