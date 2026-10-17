package com.barium.client.mixin;

import com.barium.client.optimization.ChunkOptimizer;
import com.barium.config.BariumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin {

    @Inject(method = "tryExtractRenderState", at = @At("HEAD"), cancellable = true)
    private void barium$blockEntityCulling(BlockEntity blockEntity, float partialTick,
                                           ModelFeatureRenderer.CrumblingOverlay crumbling,
                                           CallbackInfoReturnable<BlockEntityRenderState> cir) {
        Entity camEntity = Minecraft.getInstance().getCameraEntity();
        if (camEntity == null) return;
        Vec3 cam = camEntity.getEyePosition();

        if (BariumConfig.C.ENABLE_BLOCK_ENTITY_CULLING && !ChunkOptimizer.shouldRenderBlockEntity(blockEntity, cam)) {
            cir.setReturnValue(null);
            return;
        }
        if (BariumConfig.C.ENABLE_BLOCK_ENTITY_OCCLUSION_CULLING && ChunkOptimizer.isBlockEntityOccluded(blockEntity, cam)) {
            cir.setReturnValue(null);
        }
    }
}
