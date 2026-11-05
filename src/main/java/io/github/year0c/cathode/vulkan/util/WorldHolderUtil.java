package io.github.year0c.cathode.vulkan.util;

import net.minecraft.world.World;

public class WorldHolderUtil {
    private static World instance;

    public static void set(World w) { instance = w; }
    public static World get() { return instance; }
}   