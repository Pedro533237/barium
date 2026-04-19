package com.barium.client.optimization;

import java.util.concurrent.atomic.AtomicInteger;

public final class OutlineThrottlePolicy {
	private static final AtomicInteger HOT_FRAME_STREAK = new AtomicInteger(0);
	private static final AtomicInteger OUTLINE_FRAME_COUNTER = new AtomicInteger(0);

	private OutlineThrottlePolicy() {
	}

	public static boolean shouldRenderOutline(long averageFrameNanos) {
		if (averageFrameNanos < 20_000_000L) {
			HOT_FRAME_STREAK.set(0);
			return true;
		}

		int streak = HOT_FRAME_STREAK.updateAndGet(previous -> Math.min(previous + 1, 600));
		if (streak < 120) {
			return true;
		}

		int frame = OUTLINE_FRAME_COUNTER.incrementAndGet();
		return frame % 2 == 0;
	}
}
