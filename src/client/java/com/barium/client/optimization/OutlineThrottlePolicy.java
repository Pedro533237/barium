package com.barium.client.optimization;

import java.util.concurrent.atomic.AtomicInteger;

public final class OutlineThrottlePolicy {
	private static final AtomicInteger OUTLINE_FRAME_COUNTER = new AtomicInteger(0);

	private OutlineThrottlePolicy() {
	}

	public static boolean shouldRenderOutline(long averageFrameNanos) {
		int step = calculateStep(averageFrameNanos);
		int frame = OUTLINE_FRAME_COUNTER.incrementAndGet();
		return frame % step == 0;
	}

	private static int calculateStep(long averageFrameNanos) {
		if (averageFrameNanos >= 28_000_000L) {
			return 4;
		}

		if (averageFrameNanos >= 20_000_000L) {
			return 2;
		}

		return 1;
	}
}
