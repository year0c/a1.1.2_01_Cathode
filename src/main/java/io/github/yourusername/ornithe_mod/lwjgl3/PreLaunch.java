package io.github.yourusername.ornithe_mod.lwjgl3;

public class PreLaunch {
    public static void init() {
        try {
            Class<?> launchClass = Class.forName("net.minecraft.launchwrapper.Launch");
            java.lang.reflect.Field field = launchClass.getField("classLoader");
            Object classLoader = field.get(null);
            classLoader.getClass()
                .getMethod("registerTransformer", String.class)
                .invoke(classLoader, LwjglRedirectTransformer.class.getName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to register LWJGL redirect transformer", e);
        }
    }
}   