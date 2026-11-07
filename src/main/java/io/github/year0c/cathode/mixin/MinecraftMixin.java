package io.github.year0c.cathode.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Session;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    /**
     * @reason Replace AWT Frame/Canvas with SDL window.
     *         Canvas is null — all Display.* calls route through DisplaySdl.
     */
    @Overwrite
    public static void startAndConnect(String username, String sessionId, String server) {
        boolean fullscreen = false;
        String player = username;

        final Minecraft minecraft = new Minecraft(null, null, null, 854, 480, fullscreen) {
            @Override
            public void handleCrash(CrashReport report) {
                System.err.println("Crash: " + report);
            }
        };

        final Thread thread = new Thread(minecraft, "Minecraft main thread");
        thread.setPriority(Thread.MAX_PRIORITY);
        minecraft.appletMode = false;
        minecraft.hostAddress = "www.minecraft.net";
        minecraft.session = (player != null && sessionId != null)
            ? new Session(player, sessionId)
            : new Session("Player" + System.currentTimeMillis() % 1000L, "");

        if (server != null) {
            String[] parts = server.split(":");
            minecraft.setStartupServer(parts[0], Integer.parseInt(parts[1]));
        }

        thread.start();
    }
}   