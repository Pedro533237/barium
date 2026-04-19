package com.barium.client.optimization;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Dynamic frame-pressure controller for expensive client-side render passes.
 */
public final class OutlineOptimizationController {
	private static final AtomicLong AVERAGE_RENDER_NANOS = new AtomicLong(16_000_000L);
	private static final AtomicLong AVERAGE_TICK_NANOS = new AtomicLong(15_000_000L);
	private static final AtomicInteger FRAME_COUNTER = new AtomicInteger(0);

	private OutlineOptimizationController() {
	}

	public static long beginFrame() {
		return System.nanoTime();
	}

	public static void endFrame(long startedAtNanos) {
		long frameTime = Math.max(0L, System.nanoTime() - startedAtNanos);
		AVERAGE_RENDER_NANOS.updateAndGet(previous -> ((previous * 7L) + frameTime) / 8L);
		FRAME_COUNTER.incrementAndGet();
	}

	public static void recordTickTime(long tickTimeNanos) {
		long safeTickTime = Math.max(0L, tickTimeNanos);
		AVERAGE_TICK_NANOS.updateAndGet(previous -> ((previous * 7L) + safeTickTime) / 8L);
	}

	public static boolean shouldRenderEntityOutline() {
		int step = calculateOutlineStep(AVERAGE_RENDER_NANOS.get(), AVERAGE_TICK_NANOS.get());
		return FRAME_COUNTER.get() % step == 0;
	}

	public static boolean shouldRenderLevel() {
		int step = calculateLevelStep(AVERAGE_RENDER_NANOS.get(), AVERAGE_TICK_NANOS.get());
		return FRAME_COUNTER.get() % step == 0;
	}

	private static int calculateOutlineStep(long averageRender, long averageTick) {
		if (averageRender >= 30_000_000L || averageTick >= 50_000_000L) {
			return 6;
		}

		if (averageRender >= 22_000_000L || averageTick >= 35_000_000L) {
			return 4;
		}

		if (averageRender >= 16_000_000L || averageTick >= 25_000_000L) {
			return 2;
		}

		return 1;
	}

	private static int calculateLevelStep(long averageRender, long averageTick) {
		if (averageRender >= 45_000_000L || averageTick >= 60_000_000L) {
			return 3;
		}

		if (averageRender >= 30_000_000L || averageTick >= 45_000_000L) {
			return 2;
		}

		return 1;
	}
}
