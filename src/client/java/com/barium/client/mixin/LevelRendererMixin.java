package com.barium.client.mixin;

import com.barium.client.optimization.FrameTimeTracker;
import com.barium.client.optimization.OutlineThrottlePolicy;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Inject(method = "doEntityOutline", at = @At("HEAD"), cancellable = true)
	private void barium$throttleEntityOutline(CallbackInfo ci) {
		long averageFrameNanos = FrameTimeTracker.getAverageFrameNanos();
		long p95FrameNanos = FrameTimeTracker.getP95FrameNanos();
		if (!OutlineThrottlePolicy.shouldRenderOutline(averageFrameNanos, p95FrameNanos)) {
			ci.cancel();
		}
	}
}
