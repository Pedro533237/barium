package com.barium.client.mixin;

import com.barium.client.optimization.OutlineOptimizationController;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Inject(method = "doEntityOutline", at = @At("HEAD"), cancellable = true)
	private void barium$throttleEntityOutline(CallbackInfo ci) {
		if (!OutlineOptimizationController.shouldRenderEntityOutline()) {
			ci.cancel();
		}
	}
}
