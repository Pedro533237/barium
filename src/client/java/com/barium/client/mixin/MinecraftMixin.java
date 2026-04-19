package com.barium.client.mixin;

import com.barium.client.optimization.OutlineOptimizationController;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Unique
	private long barium$tickStartNanos;

	@Inject(method = "runTick", at = @At("HEAD"))
	private void barium$beforeTick(CallbackInfo ci) {
		this.barium$tickStartNanos = System.nanoTime();
	}

	@Inject(method = "runTick", at = @At("TAIL"))
	private void barium$afterTick(CallbackInfo ci) {
		OutlineOptimizationController.recordTickTime(System.nanoTime() - this.barium$tickStartNanos);
	}
}
