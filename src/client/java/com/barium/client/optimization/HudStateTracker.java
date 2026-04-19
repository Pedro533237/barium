package com.barium.client.optimization;

import net.minecraft.client.player.LocalPlayer;

public final class HudStateTracker {
	private static int lastFood;
	private static int lastArmor;
	private static int lastAir;
	private static int lastXpLevel;
	private static float lastHealth;
	private static float lastXpProgress;
	private static boolean initialized;

	private HudStateTracker() {
	}

	public static boolean hasHudStateChanged(LocalPlayer player) {
		int food = player.getFoodData().getFoodLevel();
		int armor = player.getArmorValue();
		int air = player.getAirSupply();
		int xpLevel = player.experienceLevel;
		float health = player.getHealth();
		float xpProgress = player.experienceProgress;

		if (!initialized) {
			store(food, armor, air, xpLevel, health, xpProgress);
			initialized = true;
			return true;
		}

		boolean changed = food != lastFood
			|| armor != lastArmor
			|| air != lastAir
			|| xpLevel != lastXpLevel
			|| Float.compare(health, lastHealth) != 0
			|| Float.compare(xpProgress, lastXpProgress) != 0;

		if (changed) {
			store(food, armor, air, xpLevel, health, xpProgress);
		}

		return changed;
	}

	private static void store(int food, int armor, int air, int xpLevel, float health, float xpProgress) {
		lastFood = food;
		lastArmor = armor;
		lastAir = air;
		lastXpLevel = xpLevel;
		lastHealth = health;
		lastXpProgress = xpProgress;
	}
}
