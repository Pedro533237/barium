package com.barium.client.mixin;

import com.barium.client.optimization.FrameTimeTracker;
import com.barium.client.optimization.GuiThrottlePolicy;
import com.barium.client.optimization.HudStateTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
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
		LocalPlayer player = minecraft.player;
		if (player == null) {
			return;
		}

		long averageFrameNanos = FrameTimeTracker.getAverageFrameNanos();
		boolean hasOpenScreen = minecraft.screen != null;
		boolean hudStateChanged = HudStateTracker.hasHudStateChanged(player);
		if (!GuiThrottlePolicy.shouldRenderGui(averageFrameNanos, hasOpenScreen, hudStateChanged)) {
			ci.cancel();
		}
	}
}
