package com.barium.client.optimization;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Throttles expensive outline/post passes using render + tick pressure.
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
		long averageRender = AVERAGE_RENDER_NANOS.get();
		long averageTick = AVERAGE_TICK_NANOS.get();
		int frame = FRAME_COUNTER.get();
		int step = calculateStep(averageRender, averageTick);
		return frame % step == 0;
	}

	private static int calculateStep(long averageRender, long averageTick) {
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
}
