package com.barium.client.mixin;

import com.barium.client.render.pipeline.RenderDebugMetrics;
import com.barium.client.util.ChunkRenderManager;
import com.barium.client.util.FloodFillVisibilityManager;
import com.barium.config.BariumConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkBuilder.BuiltChunk.class)
public abstract class ChunkRenderMixin {

    @Shadow public abstract BlockPos getOrigin();

    @Inject(method = "shouldBuild()Z", at = @At("HEAD"), cancellable = true)
    private void barium$onShouldBuild(CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if (!BariumConfig.C.ENABLE_AGGRESSIVE_CHUNK_BUILD_CULLING) {
            return;
        }

        BlockPos origin = this.getOrigin();
        int chunkX = origin.getX() >> 4;
        int chunkZ = origin.getZ() >> 4;

        ChunkPos playerChunkPos = client.player.getChunkPos();
        int pX = playerChunkPos.x;
        int pZ = playerChunkPos.z;

        if (Math.abs(pX - chunkX) <= 2 && Math.abs(pZ - chunkZ) <= 2) {
            return;
        }

        if (BariumConfig.C.ENABLE_FLOOD_FILL_CULLING) {
            Profilers.get().push("barium_flood_fill_chunk_culling");
            try {
                if (!FloodFillVisibilityManager.getInstance().isChunkVisible(chunkX, chunkZ)) {
                    int dx = Math.abs(pX - chunkX);
                    int dz = Math.abs(pZ - chunkZ);

                    int detailed = BariumConfig.C.DETAILED_RENDER_RADIUS;
                    int skipRate = Math.max(1, BariumConfig.C.CHUNK_UPDATE_SKIP_RATE);
                    int sparse = Math.max(1, BariumConfig.C.SPARSE_CHUNK_FACTOR);

                    if (dx <= detailed && dz <= detailed) {
                        return;
                    }

                    long worldTime = client.world.getTime();
                    boolean onSparseGrid = (Math.floorMod(chunkX - pX, sparse) == 0 && Math.floorMod(chunkZ - pZ, sparse) == 0);
                    if (onSparseGrid && (worldTime % skipRate == 0)) {
                        return;
                    }

                    if (BariumConfig.C.ENABLE_RENDER_DEBUG_METRICS) {
                        RenderDebugMetrics.addCulledChunk();
                    }
                    cir.setReturnValue(false);
                    return;
                }
            } finally {
                Profilers.get().pop();
            }
        }

        if (BariumConfig.C.ENABLE_FRUSTUM_CHUNK_CULLING) {
            Profilers.get().push("barium_frustum_chunk_culling");
            try {
                if (!ChunkRenderManager.getInstance().isChunkInFrustum(chunkX, chunkZ)) {
                    int dx = Math.abs(pX - chunkX);
                    int dz = Math.abs(pZ - chunkZ);

                    int detailed = BariumConfig.C.DETAILED_RENDER_RADIUS;
                    int skipRate = Math.max(1, BariumConfig.C.CHUNK_UPDATE_SKIP_RATE);
                    int sparse = Math.max(1, BariumConfig.C.SPARSE_CHUNK_FACTOR);

                    if (dx <= detailed && dz <= detailed) {
                        return;
                    }

                    long worldTime = client.world.getTime();
                    boolean onSparseGrid = (Math.floorMod(chunkX - pX, sparse) == 0 && Math.floorMod(chunkZ - pZ, sparse) == 0);
                    if (onSparseGrid && (worldTime % skipRate == 0)) {
                        return;
                    }

                    if (BariumConfig.C.ENABLE_RENDER_DEBUG_METRICS) {
                        RenderDebugMetrics.addCulledChunk();
                    }
                    cir.setReturnValue(false);
                }
            } finally {
                Profilers.get().pop();
            }
        }
    }
}
