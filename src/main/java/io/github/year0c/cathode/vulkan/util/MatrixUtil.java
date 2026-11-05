package io.github.year0c.cathode.vulkan.util;

public class MatrixUtil {

    /**
     * Build a column-major 4x4 perspective * view matrix.
     *
     * @param fovY     vertical field of view in radians (half-angle, i.e. fullFOV / 2)
     * @param aspect   width / height
     * @param near     near plane
     * @param far      far plane
     * @param eyeX     camera position
     * @param eyeY
     * @param eyeZ
     * @param yaw      radians, 0 = south (+Z), positive = west (-X)
     * @param pitch    radians, 0 = horizon, positive = up
     */
    public static float[] buildMVP(float fovY, float aspect, float near, float far,
                                   float eyeX, float eyeY, float eyeZ,
                                   float yaw, float pitch) {
        // --- Perspective (column-major) ---
        float f = 1.0f / (float) Math.tan(fovY * 0.5);
        float range = far - near;

        float[] proj = new float[16];
        proj[0]  = f / aspect;
        proj[5]  = f;
        proj[10] = (far + near) / range;
        proj[11] = -1.0f;
        proj[14] = 2.0f * far * near / range;

        // --- View (inverse of camera world transform) ---
        // Forward vector from yaw/pitch (MC convention: yaw 0 = south = +Z, pitch up = +Y)
        float cp = (float) Math.cos(pitch);
        float sp = (float) Math.sin(pitch);
        float cy = (float) Math.cos(yaw);
        float sy = (float) Math.sin(yaw);

        // Forward (where camera looks)
        float fx = -sy * cp;
        float fy = sp;
        float fz = -cy * cp;

        // Right = forward × up(0,1,0)
        float rx = fz;
        float ry = 0;
        float rz = -fx;

        // Up = right × forward
        float ux = ry * fz - rz * fy;
        float uy = rz * fx - rx * fz;
        float uz = rx * fy - ry * fx;

        float[] view = new float[16];
        view[0]  = rx;
        view[4]  = ry;
        view[8]  = rz;
        view[1]  = ux;
        view[5]  = uy;
        view[9]  = uz;
        view[2]  = -fx;
        view[6]  = -fy;
        view[10] = -fz;
        view[12] = -(rx * eyeX + ry * eyeY + rz * eyeZ);
        view[13] = -(ux * eyeX + uy * eyeY + uz * eyeZ);
        view[14] = -(-fx * eyeX - fy * eyeY - fz * eyeZ);
        view[15] = 1.0f;

        // --- MVP = proj * view (column-major multiply) ---
        float[] mvp = new float[16];
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                float sum = 0;
                for (int k = 0; k < 4; k++) {
                    sum += proj[k * 4 + row] * view[col * 4 + k];
                }
                mvp[col * 4 + row] = sum;
            }
        }

        return mvp;
    }
}   