package com.barium.client.optimization;

import com.barium.config.BariumConfig;
import net.minecraft.world.phys.Vec3;

public class ParticleOptimizer {

    public static boolean isTooFar(Vec3 cam, double x, double y, double z) {
        if (!BariumConfig.C.ENABLE_PARTICLE_OPTIMIZATION) return false;
        double dx = x - cam.x, dy = y - cam.y, dz = z - cam.z;
        return dx * dx + dy * dy + dz * dz > BariumConfig.C.PARTICLE_CULL_DISTANCE_SQ;
    }
}
