package com.barium.client.optimization;

import java.util.concurrent.atomic.AtomicInteger;

public final class OutlineThrottlePolicy {
	private static final AtomicInteger THROTTLE_LEVEL = new AtomicInteger(0);
	private static final AtomicInteger COOL_STREAK = new AtomicInteger(0);
	private static final AtomicInteger OUTLINE_FRAME_COUNTER = new AtomicInteger(0);

	private static final long TARGET_FRAME_NANOS = 16_666_667L;
	private static final long SOFT_PRESSURE_NANOS = 20_000_000L;
	private static final long HARD_PRESSURE_NANOS = 25_000_000L;
	private static final int MAX_LEVEL = 3;

	private OutlineThrottlePolicy() {
	}

	public static boolean shouldRenderOutline(long averageFrameNanos, long p95FrameNanos) {
		updateThrottleLevel(averageFrameNanos, p95FrameNanos);

		int level = THROTTLE_LEVEL.get();
		if (level <= 0) {
			OUTLINE_FRAME_COUNTER.set(0);
			return true;
		}

		int frame = Math.max(1, OUTLINE_FRAME_COUNTER.incrementAndGet());
		int cadence = 1 << level;
		return frame % cadence == 0;
	}

	private static void updateThrottleLevel(long averageFrameNanos, long p95FrameNanos) {
		long blendedPressure = ((averageFrameNanos * 2L) + p95FrameNanos) / 3L;
		int level = THROTTLE_LEVEL.get();

		if (blendedPressure >= HARD_PRESSURE_NANOS) {
			THROTTLE_LEVEL.updateAndGet(previous -> Math.min(MAX_LEVEL, Math.max(previous, level + 1)));
			COOL_STREAK.set(0);
			return;
		}

		if (blendedPressure >= SOFT_PRESSURE_NANOS) {
			THROTTLE_LEVEL.updateAndGet(previous -> Math.min(MAX_LEVEL, previous + 1));
			COOL_STREAK.set(0);
			return;
		}

		if (blendedPressure <= TARGET_FRAME_NANOS) {
			int cool = COOL_STREAK.updateAndGet(previous -> Math.min(previous + 1, 600));
			if (cool >= 24) {
				THROTTLE_LEVEL.updateAndGet(previous -> Math.max(0, previous - 1));
				COOL_STREAK.set(0);
			}
			return;
		}

		COOL_STREAK.set(0);
	}
}
