package com.barium.client.optimization;

import com.barium.config.BariumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Culling de block entities (baús, placas, camas, etc.). */
public class ChunkOptimizer {

    private static final double[][] TEST_POINTS = {
            {0.5, 0.5, 0.5}, {0.1, 0.1, 0.1}, {0.9, 0.1, 0.1},
            {0.1, 0.9, 0.1}, {0.1, 0.1, 0.9}, {0.9, 0.9, 0.9}
    };

    public static boolean shouldRenderBlockEntity(BlockEntity be, Vec3 cam) {
        BlockPos p = be.getBlockPos();
        double dx = p.getX() + 0.5 - cam.x;
        double dy = p.getY() + 0.5 - cam.y;
        double dz = p.getZ() + 0.5 - cam.z;
        return dx * dx + dy * dy + dz * dz <= BariumConfig.C.MAX_BLOCK_ENTITY_RENDER_DISTANCE_SQ;
    }

    public static boolean isBlockEntityOccluded(BlockEntity be, Vec3 cam) {
        Level level = be.getLevel();
        if (level == null) return false;

        BlockPos pos = be.getBlockPos();
        double dx = pos.getX() + 0.5 - cam.x;
        double dy = pos.getY() + 0.5 - cam.y;
        double dz = pos.getZ() + 0.5 - cam.z;
        if (dx * dx + dy * dy + dz * dz < BariumConfig.C.BLOCK_ENTITY_OCCLUSION_MIN_DISTANCE_SQ) {
            return false;
        }

        AABB box = new AABB(pos);
        var player = Minecraft.getInstance().player;

        for (double[] t : TEST_POINTS) {
            Vec3 target = new Vec3(
                    box.minX + (box.maxX - box.minX) * t[0],
                    box.minY + (box.maxY - box.minY) * t[1],
                    box.minZ + (box.maxZ - box.minZ) * t[2]);

            BlockHitResult hit = level.clip(new ClipContext(cam, target,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

            if (hit.getType() == HitResult.Type.MISS || hit.getBlockPos().equals(pos)) {
                return false;
            }
        }
        return true;
    }
}
