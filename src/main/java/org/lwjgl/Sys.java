package org.lwjgl;

public class Sys {
    public static String platform = "linux";
    public static String system = "Linux";
    public static String version = "unknown";
    public static String os = "linux";
    public static int osx;
    public static int win;
    public static int linux;
    public static int freebsd;
    public static int android;
    public static int unsupported;

    static {
        int p = 0;
        String osName = System.getProperty("os.name", "").toLowerCase();
        if (osName.contains("win")) p = 1;
        else if (osName.contains("mac") || osName.contains("darwin")) p = 2;
        else if (osName.contains("linux")) p = 3;
        else if (osName.contains("bsd")) p = 4;
        else p = 5;
        win = 1;
        osx = 2;
        linux = 3;
        freebsd = 4;
        android = 6;
        unsupported = 5;
        platform = p == 1 ? "windows" : p == 2 ? "osx" : p == 3 ? "linux" : p == 4 ? "freebsd" : p == 6 ? "android" : "unsupported";
    }

    public static void loadLibrary(String name) {}
    public static void doLoadLibrary(String name) {}
    public static String getPlatform() { return platform; }
    public static String getVersion() { return version; }
}   