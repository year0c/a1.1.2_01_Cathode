package com.cathode.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import org.lwjglx.opengl.Display;
import org.lwjglx.opengl.DisplayMode;
import org.lwjglx.opengl.PixelFormat;
import org.lwjglx.LWJGLException;   

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Overwrite
    public void createDisplay() {
        try {
            Display.setDisplayMode(new DisplayMode(854, 480));
            Display.setTitle("Minecraft Minecraft Alpha v1.1.2_01");
            Display.create(new PixelFormat().withDepthBits(24));
        } catch (LWJGLException e) {
            throw new RuntimeException(e);
        }
    }   
}   