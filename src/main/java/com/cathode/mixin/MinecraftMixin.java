package com.cathode.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import org.lwjglx.LWJGLException;
import org.lwjglx.opengl.Display;
import org.lwjglx.opengl.DisplayMode;
import org.lwjglx.opengl.PixelFormat;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Overwrite
    public static void startAndConnect(String username, String sessionId, String server) {
        Minecraft game = new Minecraft(null, null, null, 854, 480, false) {
            @Override
            public void handleCrash(CrashReport report) {
                System.err.println(report);
                this.running = false;
            }
        };

        try {
            Display.setDisplayMode(new DisplayMode(854, 480));
            Display.setTitle("Minecraft Minecraft Alpha v1.1.2_01");
            Display.create(new PixelFormat().withDepthBits(24));
        } catch (LWJGLException e) {
            throw new RuntimeException(e);
        }

        Thread thread = new Thread(game, "Minecraft main thread");
        thread.setPriority(10);
        game.appletMode = false;
        game.hostAddress = "www.minecraft.net";

        if (username != null && sessionId != null) {
            game.session = new net.minecraft.client.Session(username, sessionId);
        } else {
            game.session = new net.minecraft.client.Session("Player" + System.currentTimeMillis() % 1000L, "");
        }

        if (server != null) {
            String[] strings = server.split(":");
            game.setStartupServer(strings[0], Integer.parseInt(strings[1]));
        }

        thread.start();
    }
}   