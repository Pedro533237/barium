package com.barium.client.mixin;

import com.barium.client.optimization.FrameTimeTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Unique
	private long barium$frameStartNanos;

	@Inject(method = "render", at = @At("HEAD"))
	private void barium$beforeRender(CallbackInfo ci) {
		this.barium$frameStartNanos = FrameTimeTracker.beginFrame();
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void barium$afterRender(CallbackInfo ci) {
		FrameTimeTracker.endFrame(this.barium$frameStartNanos);
	}
}
