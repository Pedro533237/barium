package com.barium.client.mixin;

import com.barium.client.optimization.FrameTimeTracker;
import com.barium.client.optimization.GuiThrottlePolicy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void barium$throttleGuiRender(CallbackInfo ci) {
		Minecraft minecraft = Minecraft.getInstance();
		long averageFrameNanos = FrameTimeTracker.getAverageFrameNanos();
		boolean hasOpenScreen = minecraft.screen != null;
		if (!GuiThrottlePolicy.shouldRenderGui(averageFrameNanos, hasOpenScreen)) {
			ci.cancel();
		}
	}
}
