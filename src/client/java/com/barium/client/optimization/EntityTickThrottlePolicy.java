package com.barium.client.optimization;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;

public final class EntityTickThrottlePolicy {
	private static final double FAR_DISTANCE_SQR = 48.0D * 48.0D;
	private static final double VERY_FAR_DISTANCE_SQR = 96.0D * 96.0D;

	private EntityTickThrottlePolicy() {
	}

	public static boolean shouldSkipClientTick(Entity entity, LocalPlayer player) {
		if (entity == player) {
			return false;
		}

		if (!entity.isAlive() || entity.isRemoved()) {
			return false;
		}

		if (entity.isPassenger() || entity.isVehicle()) {
			return false;
		}

		double distanceSqr = entity.distanceToSqr(player);
		if (distanceSqr < FAR_DISTANCE_SQR) {
			return false;
		}

		int tick = entity.tickCount;
		if (distanceSqr >= VERY_FAR_DISTANCE_SQR) {
			return (tick & 3) != 0;
		}

		return (tick & 1) != 0;
	}
}
