package com.barium.client.render.culling;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

/**
 * Culling de faces em nível de bloco para reduzir faces internas no mesh de chunk.
 */
public final class FaceCullingManager {

    private FaceCullingManager() {
    }

    public static boolean shouldRenderFace(BlockRenderView world, BlockPos origin, BlockState state, Direction side) {
        BlockPos neighborPos = origin.offset(side);
        BlockState neighbor = world.getBlockState(neighborPos);

        if (neighbor.isAir()) {
            return true;
        }

        // Respeita lógica vanilla/customizada de modelos (inclui blocos custom Blockbench quando fornecem culling shape correto).
        if (state.isSideInvisible(neighbor, side)) {
            return false;
        }

        // Faces entre blocos transparentes devem continuar visíveis na maioria dos casos.
        if (isTransparentLike(state) || isTransparentLike(neighbor)) {
            return true;
        }

        if (!neighbor.isOpaqueFullCube()) {
            return true;
        }

        return !neighbor.isSideSolidFullSquare(world, neighborPos, side.getOpposite());
    }

    public static boolean isFullyOccluded(BlockRenderView world, BlockPos origin, BlockState state) {
        if (isTransparentLike(state)) {
            return false;
        }

        for (Direction direction : Direction.values()) {
            if (shouldRenderFace(world, origin, state, direction)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isTransparentLike(BlockState state) {
        return !state.isOpaqueFullCube()
                || state.getRenderType() == BlockRenderType.INVISIBLE
                || state.isIn(BlockTags.LEAVES);
    }
}
