package com.barium.client.mixin;

import com.barium.config.BariumConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BlockRenderDispatcher.class)
public class BlockRenderDispatcherMixin {

    @Inject(method = "renderBatched", at = @At("HEAD"), cancellable = true)
    private void barium$cullDenseFoliage(BlockState state, BlockPos pos, BlockAndTintGetter level, PoseStack poseStack,
                                         VertexConsumer consumer, boolean cull, List<?> parts, CallbackInfo ci) {
        int level0 = BariumConfig.C.DENSE_FOLIAGE_CULLING_LEVEL;
        if (!BariumConfig.C.ENABLE_DENSE_FOLIAGE_CULLING || level0 <= 0) return;
        if (!isTargetFoliage(state)) return;

        long seed = (long) pos.getX() * 3129871 ^ (long) pos.getZ() * 1125899;
        seed = seed * seed * 4231761 + seed * 11;
        int hash = (int) (seed >> 16);

        int cullChance = switch (level0) {
            case 1 -> 25;
            case 2 -> 50;
            case 3 -> 75;
            case 4 -> 90;
            default -> 0;
        };
        if (cullChance > 0 && (Math.abs(hash) % 100) < cullChance) {
            ci.cancel();
        }
    }

    private static boolean isTargetFoliage(BlockState s) {
        return s.is(Blocks.SHORT_GRASS) || s.is(Blocks.FERN) || s.is(Blocks.TALL_GRASS) || s.is(Blocks.LARGE_FERN)
                || s.is(Blocks.DEAD_BUSH) || s.is(Blocks.VINE) || s.is(Blocks.LILY_PAD) || s.is(Blocks.DANDELION)
                || s.is(Blocks.POPPY) || s.is(Blocks.BLUE_ORCHID) || s.is(Blocks.ALLIUM) || s.is(Blocks.AZURE_BLUET)
                || s.is(Blocks.RED_TULIP) || s.is(Blocks.ORANGE_TULIP) || s.is(Blocks.WHITE_TULIP)
                || s.is(Blocks.PINK_TULIP) || s.is(Blocks.OXEYE_DAISY) || s.is(Blocks.CORNFLOWER)
                || s.is(Blocks.LILY_OF_THE_VALLEY) || s.is(Blocks.BROWN_MUSHROOM) || s.is(Blocks.RED_MUSHROOM);
    }
}
