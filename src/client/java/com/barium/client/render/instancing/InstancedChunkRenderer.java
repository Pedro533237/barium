package com.barium.client.render.instancing;

import com.barium.client.render.culling.FaceCullingManager;
import com.barium.config.BariumConfig;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

/**
 * Estrutura para draw instanciado. A ligação com VAO/VBO e shader é feita pela pipeline do mod.
 */
public final class InstancedChunkRenderer {

    private static final InstancedChunkRenderer INSTANCE = new InstancedChunkRenderer();

    private final PooledFaceBuffer pooledFaces = new PooledFaceBuffer(8_192);

    public static InstancedChunkRenderer getInstance() {
        return INSTANCE;
    }

    public PooledFaceBuffer pooledFaces() {
        return pooledFaces;
    }

    public boolean shouldUseInstancing() {
        return BariumConfig.C.ENABLE_VERTEX_POOLING && BariumConfig.C.ENABLE_INSTANCED_RENDERING;
    }

    public void recordVisibleFaces(BlockRenderView world, BlockPos pos, BlockState state, int materialId, int packedLight) {
        if (!shouldUseInstancing()) {
            return;
        }

        for (Direction direction : Direction.values()) {
            if (FaceCullingManager.shouldRenderFace(world, pos, state, direction)) {
                pooledFaces.putFace(pos.getX(), pos.getY(), pos.getZ(), faceId(direction), materialId, packedLight, 0);
            }
        }
    }

    private int faceId(Direction direction) {
        return switch (direction) {
            case DOWN -> 0;
            case UP -> 1;
            case NORTH -> 2;
            case SOUTH -> 3;
            case WEST -> 4;
            case EAST -> 5;
        };
    }

    public int flushFaceCount() {
        int count = pooledFaces.faceCount();
        pooledFaces.clear();
        return count;
    }
}
