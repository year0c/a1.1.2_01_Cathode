package net.minecraft.client.render.world;

import net.minecraft.client.render.Tesselator;
import io.github.year0c.cathode.probe.SkyProbe;

public class WorldRenderer {

    private final Tesselator tesselator;
    private final SkyProbe probe;

    public WorldRenderer() {
        this.tesselator = new Tesselator();
        this.probe = SkyProbe.getInstance();
    }

    private void renderBlockFace(int x, int y, int z,
        float faceShade,
        float iconR, float iconG, float iconB,
        float u, float v,
        float worldBrightness) {
        tesselator.color(iconR * faceShade, iconG * faceShade, iconB * faceShade, 1.0f);
        tesselator.texture(u, v);

        float skyLevel = probe.getSkyLightLevel();
        float blockLevel = worldBrightness * 15.0f;
        tesselator.setLight(skyLevel, blockLevel);

        tesselator.vertex(x, y, z);
    }
}   