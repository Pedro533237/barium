package com.barium.client.optimization;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Throttles the expensive entity outline pass when frame time is too high.
 */
public final class OutlineOptimizationController {
	private static final AtomicLong AVERAGE_RENDER_NANOS = new AtomicLong(16_000_000L);
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

	public static boolean shouldRenderEntityOutline() {
		long average = AVERAGE_RENDER_NANOS.get();
		int frame = FRAME_COUNTER.get();

		if (average >= 20_000_000L) {
			return frame % 3 == 0;
		}

		if (average >= 14_000_000L) {
			return frame % 2 == 0;
		}

		return true;
	}
}
