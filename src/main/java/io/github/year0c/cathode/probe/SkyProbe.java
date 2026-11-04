package io.github.year0c.cathode.probe;

public class SkyProbe {

    private static final SkyProbe instance = new SkyProbe();

    private float skyLightFactor = 1.0f;
    private float blockLightTint = 1.0f;

    private SkyProbe() {}

    public static SkyProbe getInstance() {
        return instance;
    }

    /** Call once per tick. Currently a no-op — hardcoded day values. */
    public void update(long worldTime) {
        // TODO: replace with timeline keyframe sampling
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
}