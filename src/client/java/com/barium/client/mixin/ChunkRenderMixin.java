package com.barium.client.mixin;

import com.barium.client.render.pipeline.RenderDebugMetrics;
import com.barium.client.util.ChunkRenderManager;
import com.barium.client.util.FloodFillVisibilityManager;
import com.barium.config.BariumConfig;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkBuilder.BuiltChunk.class)
public abstract class ChunkRenderMixin {

    @Unique
    private static final Long2LongOpenHashMap BARIUM_LAST_ALLOWED_BUILD_TICK = new Long2LongOpenHashMap();

    @Shadow public abstract BlockPos getOrigin();

    @Inject(method = "shouldBuild()Z", at = @At("HEAD"), cancellable = true)
    private void barium$onShouldBuild(CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        BlockPos origin = this.getOrigin();
        int chunkX = origin.getX() >> 4;
        int chunkZ = origin.getZ() >> 4;

        ChunkPos playerChunkPos = client.player.getChunkPos();
        int pX = playerChunkPos.x;
        int pZ = playerChunkPos.z;

        int dx = Math.abs(pX - chunkX);
        int dz = Math.abs(pZ - chunkZ);
        int chebyshevDistance = Math.max(dx, dz);

        // Área próxima sempre livre para evitar buracos visuais.
        if (chebyshevDistance <= 2) {
            allowBuildNow(client.world.getTime(), chunkX, chunkZ);
            return;
        }

        if (BariumConfig.C.ENABLE_FLOOD_FILL_CULLING) {
            Profilers.get().push("barium_flood_fill_chunk_culling");
            try {
                if (!FloodFillVisibilityManager.getInstance().isChunkVisible(chunkX, chunkZ)
                        && shouldDeferBuild(client.world.getTime(), chunkX, chunkZ, chebyshevDistance)) {
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
                if (!ChunkRenderManager.getInstance().isChunkInFrustum(chunkX, chunkZ)
                        && shouldDeferBuild(client.world.getTime(), chunkX, chunkZ, chebyshevDistance)) {
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

        allowBuildNow(client.world.getTime(), chunkX, chunkZ);
    }

    @Unique
    private static boolean shouldDeferBuild(long worldTime, int chunkX, int chunkZ, int distance) {
        long key = ChunkPos.toLong(chunkX, chunkZ);
        long interval = computeRebuildInterval(distance);
        long lastAllowed = BARIUM_LAST_ALLOWED_BUILD_TICK.getOrDefault(key, Long.MIN_VALUE / 4);
        return (worldTime - lastAllowed) < interval;
    }

    @Unique
    private static long computeRebuildInterval(int distance) {
        if (distance <= 4) return 2L;
        if (distance <= 8) return 5L;
        if (distance <= 16) return 10L;
        return 20L;
    }

    @Unique
    private static void allowBuildNow(long worldTime, int chunkX, int chunkZ) {
        BARIUM_LAST_ALLOWED_BUILD_TICK.put(ChunkPos.toLong(chunkX, chunkZ), worldTime);
    }
}
