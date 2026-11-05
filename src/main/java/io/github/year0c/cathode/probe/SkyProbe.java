package io.github.year0c.cathode.probe;

public class SkyProbe {

    private static final SkyProbe instance = new SkyProbe();

    private float skyLightFactor = 1.0f;
    private float blockLightTint = 1.0f;
    private float timeOfDay = 0.0f; // 0=noon, 0.5=midnight, 1.0=noon

    private SkyProbe() {}

    public static SkyProbe getInstance() {
        return instance;
    }

    /** Call once per tick. */
    public void update(long worldTime) {
        this.timeOfDay = (worldTime % 24000L) / 24000.0f;
        float sunElevation = (float) Math.cos(timeOfDay * 2.0 * Math.PI);
        // Modern MC: minimum ambient light ~7/15 at night
        this.skyLightFactor = Math.max(7.0f / 15.0f, Math.max(0.0f, sunElevation));
        this.blockLightTint = 1.0f;
    }   

    public float getSkyLightFactor() {
        return skyLightFactor;
    }

    public float getBlockLightTint() {
        return blockLightTint;
    }

    public float getSkyLightLevel() {
        return skyLightFactor * 15.0f;
    }

    /** Normalized 0-1, 0=noon, 0.5=midnight. For sky shader. */
    public float getTimeOfDay() {
        return timeOfDay;
    }
}   