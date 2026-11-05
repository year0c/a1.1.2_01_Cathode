package io.github.year0c.cathode.vulkan;

import net.minecraft.world.World;
import org.lwjgl.vulkan.*;

import java.util.*;

/**
 * Tracks per-section meshes and dirty state.
 * Only rebuilds sections that changed or newly entered view.
 */
public class SectionManager {

    private static SectionManager instance;

    public static void setInstance(SectionManager sm) { instance = sm; }
    public static SectionManager get() { return instance; }

    private final VkDevice device;
    private final VkPhysicalDevice physicalDevice;

    private final HashMap<Long, SectionMesh> sections = new HashMap<>();
    private final HashSet<Long> dirty = new HashSet<>();

    public SectionManager(VkDevice device, VkPhysicalDevice physicalDevice) {
        this.device = device;
        this.physicalDevice = physicalDevice;
    }

    public static long key(int sx, int sy, int sz) {
        return ((long)(sx & 0xFFFF) << 32) | ((long)(sy & 0xFFFF) << 16) | (long)(sz & 0xFFFF);
    }

    public static int keyX(long k) { return (int)(k >> 32) & 0xFFFF; }
    public static int keyY(long k) { return (int)(k >> 16) & 0xFFFF; }
    public static int keyZ(long k) { return (int)k & 0xFFFF; }

    public void markDirty(int sx, int sy, int sz) {
        dirty.add(key(sx, sy, sz));
    }

    /**
     * Mark a block's section (and neighbors if on boundary) as dirty.
     */
    public void markBlockDirty(int bx, int by, int bz) {
        int sx = bx >> 4, sy = by >> 4, sz = bz >> 4;
        markDirty(sx, sy, sz);
        // If on section boundary, neighbor section is also affected
        if ((bx & 15) == 0 && sx > 0) markDirty(sx - 1, sy, sz);
        if ((bx & 15) == 15) markDirty(sx + 1, sy, sz);
        if ((by & 15) == 0 && sy > 0) markDirty(sx, sy - 1, sz);
        if ((by & 15) == 15) markDirty(sx, sy + 1, sz);
        if ((bz & 15) == 0 && sz > 0) markDirty(sx, sy, sz - 1);
        if ((bz & 15) == 15) markDirty(sx, sy, sz + 1);
    }

    /**
     * Rebuild all dirty sections that are within the given view bounds.
     * Also marks newly-visible sections as dirty on first encounter.
     */
    public void rebuildDirty(World world, int centerSX, int centerSY, int centerSZ, int viewDist) {
        for (int dx = -viewDist; dx <= viewDist; dx++) {
            for (int dz = -viewDist; dz <= viewDist; dz++) {
                for (int dy = 0; dy < 8; dy++) {
                    int sx = centerSX + dx;
                    int sy = centerSY + dy;
                    int sz = centerSZ + dz;
                    long k = key(sx, sy, sz);

                    SectionMesh mesh = sections.get(k);
                    if (mesh == null) {
                        // First time seeing this section — it's dirty
                        dirty.add(k);
                    }

                    if (dirty.contains(k)) {
                        if (mesh == null) {
                            mesh = new SectionMesh(device, physicalDevice);
                            sections.put(k, mesh);
                        }
                        mesh.clear();
                        ChunkMesher.compileSection(world, sx, sy, sz, mesh);
                        dirty.remove(k);
                    }
                }
            }
        }

        // Free sections that are far outside view (simple GC)
        // (optional — skip for now, memory is fine)
    }

    /**
     * Get all visible section meshes for drawing.
     * Returns solid and liquid buffers in draw order.
     */
    public List<SectionMesh> getVisible(int centerSX, int centerSY, int centerSZ, int viewDist) {
        List<SectionMesh> result = new ArrayList<>();
        for (int dx = -viewDist; dx <= viewDist; dx++) {
            for (int dz = -viewDist; dz <= viewDist; dz++) {
                for (int dy = 0; dy < 8; dy++) {
                    long k = key(centerSX + dx, centerSY + dy, centerSZ + dz);
                    SectionMesh mesh = sections.get(k);
                    if (mesh != null && (mesh.getSolidCount() > 0 || mesh.getLiquidCount() > 0)) {
                        result.add(mesh);
                    }
                }
            }
        }
        return result;
    }

    public void close() {
        for (SectionMesh m : sections.values()) {
            m.close();
        }
        sections.clear();
        dirty.clear();
    }
}   