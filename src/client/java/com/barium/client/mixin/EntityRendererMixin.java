package com.barium.client.mixin;

import com.barium.client.optimization.EntityOptimizer;
import com.barium.config.BariumConfig;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void barium$cullByDistance(Entity entity, Frustum frustum, double camX, double camY, double camZ,
                                       CallbackInfoReturnable<Boolean> cir) {
        if (!BariumConfig.C.ENABLE_ENTITY_CULLING) return;
        if (!EntityOptimizer.shouldRenderByDistance(entity, camX, camY, camZ)) {
            cir.setReturnValue(false);
        }
    }
}
