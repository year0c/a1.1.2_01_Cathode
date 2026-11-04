package org.lwjglx;

public class Sys {
    public static String platform = "linux";
    public static String system = "Linux";
    public static String version = "unknown";
    public static String os = "linux";
    public static final int win = 1;
    public static final int osx = 2;
    public static final int linux = 3;
    public static final int freebsd = 4;
    public static final int unsupported = 5;

    static {
        int p = 0;
        String osName = System.getProperty("os.name", "").toLowerCase();
        if (osName.contains("win")) p = 1;
        else if (osName.contains("mac") || osName.contains("darwin")) p = 2;
        else if (osName.contains("linux")) p = 3;
        else if (osName.contains("bsd")) p = 4;
        else p = 5;
        platform = p == 1 ? "windows" : p == 2 ? "osx" : p == 3 ? "linux" : p == 4 ? "freebsd" : "unsupported";
        os = platform;
        system = System.getProperty("os.name", "unknown");
        version = System.getProperty("os.version", "unknown");
    }

    public static void loadLibrary(String name) {}
    public static void doLoadLibrary(String name) {}
    public static String getPlatform() { return platform; }
    public static String getVersion() { return version; }
    public static void initialize() {}
}   