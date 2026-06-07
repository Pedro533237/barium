package com.barium.client.optimization;

import com.barium.config.BariumConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;

public class ChunkOptimizer {

    public static boolean shouldRenderBlockEntity(BlockEntity blockEntity, Camera camera) {
        if (!BariumConfig.C.ENABLE_BLOCK_ENTITY_CULLING) return true;
        
        double distSq = blockEntity.getBlockPos().distToCenterSqr(camera.getPosition());
        return distSq <= BariumConfig.C.MAX_BLOCK_ENTITY_RENDER_DISTANCE_SQ;
    }

    public static boolean isBlockEntityOccluded(BlockEntity blockEntity, Camera camera) {
        if (!BariumConfig.C.ENABLE_BLOCK_ENTITY_OCCLUSION_CULLING) return false;

        // Não aplicar oclusão em baús para evitar bugs de invisibilidade
        if (blockEntity instanceof ChestBlockEntity) return false;

        Level world = blockEntity.getLevel();
        if (world == null) return false;

        BlockPos pos = blockEntity.getBlockPos();
        Vec3 cameraPos = camera.getPosition();
        
        // Se estiver muito perto (menos de 4 blocos), não ocluir para evitar bugs visuais
        if (cameraPos.distanceToSqr(Vec3.atCenterOf(pos)) < 16.0) return false;

        // O segredo: Raycast tipo VISUAL e ignorar o próprio bloco atingido
        ClipContext context = new ClipContext(
                cameraPos, 
                Vec3.atCenterOf(pos),
                ClipContext.Block.VISUAL, 
                ClipContext.Fluid.NONE, 
                Minecraft.getInstance().player
        );

        BlockHitResult hitResult = world.clip(context);

        // Se o hit for do tipo MISS ou se o bloco atingido for o próprio baú, ele DEVE renderizar
        if (hitResult.getType() == HitResult.Type.MISS) return false;
        if (hitResult.getBlockPos().equals(pos)) return false;

        // Se o raio bateu em outro bloco antes, então o baú está escondido
        return true;
    }
}
