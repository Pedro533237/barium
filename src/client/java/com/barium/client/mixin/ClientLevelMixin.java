package com.barium.client.mixin;

import com.barium.client.optimization.EntityTickThrottlePolicy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
	@Inject(method = "tickNonPassenger", at = @At("HEAD"), cancellable = true)
	private void barium$throttleFarEntityTicks(Entity entity, CallbackInfo ci) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		if (EntityTickThrottlePolicy.shouldSkipClientTick(entity, player)) {
			ci.cancel();
		}
	}
}
