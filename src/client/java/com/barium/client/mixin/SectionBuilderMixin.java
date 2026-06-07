package com.barium.client.mixin;

import com.barium.config.BariumConfig;
import com.mojang.blaze3d.systems.VertexSorter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.SectionBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SectionBuilder.class)
public class SectionBuilderMixin {

    @Unique
    private boolean barium_isDistant = false;

    @Inject(method = "build", at = @At("HEAD"))
    private void barium$checkDistance(ChunkSectionPos sectionPos,
                                      ChunkRendererRegion region,
                                      VertexSorter sorter,
                                      net.minecraft.client.render.chunk.BlockBufferAllocatorStorage allocator,
                                      CallbackInfoReturnable<?> cir) {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }

        BlockPos playerPos = MinecraftClient.getInstance().player.getBlockPos();
        int dx = Math.abs(sectionPos.getMinX() - playerPos.getX());
        int dz = Math.abs(sectionPos.getMinZ() - playerPos.getZ());

        int threshold = BariumConfig.C.DISTANT_GEOMETRY_CULL_DISTANCE;
        this.barium_isDistant = (dx > threshold || dz > threshold);
    }

    @Inject(method = "build", at = @At("HEAD"), cancellable = true)
    private void barium$cullEmptySections(ChunkSectionPos sectionPos,
                                          ChunkRendererRegion renderRegion,
                                          VertexSorter vertexSorter,
                                          net.minecraft.client.render.chunk.BlockBufferAllocatorStorage allocatorStorage,
                                          CallbackInfoReturnable<SectionBuilder.RenderData> cir) {
        if (!BariumConfig.C.ENABLE_EMPTY_CHUNK_SECTION_CULLING) {
            return;
        }

        // Yarn 1.21.9: nunca cancelar build por estado de visibilidade transitório.
        if (renderRegion != null && isSectionEmpty(renderRegion, sectionPos)) {
            cir.setReturnValue(new SectionBuilder.RenderData());
        }
    }

    @Unique
    private boolean isSectionEmpty(ChunkRendererRegion region, ChunkSectionPos sectionPos) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        int startX = sectionPos.getMinX();
        int startY = sectionPos.getMinY();
        int startZ = sectionPos.getMinZ();

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    mutablePos.set(startX + x, startY + y, startZ + z);
                    BlockState state = region.getBlockState(mutablePos);

                    if (!state.isAir()) {
                        if (BariumConfig.C.ENABLE_DISTANT_GEOMETRY_CULLING && this.barium_isDistant && isDetailBlock(state.getBlock())) {
                            continue;
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Unique
    private boolean isDetailBlock(Block block) {
        return block == Blocks.SHORT_GRASS
                || block == Blocks.TALL_GRASS
                || block == Blocks.FERN
                || block == Blocks.LARGE_FERN
                || block instanceof net.minecraft.block.FlowerBlock;
    }
}
