package com.barium.client.optimization;

import java.util.concurrent.atomic.AtomicInteger;

public final class GuiThrottlePolicy {
	private static final AtomicInteger GUI_FRAME_COUNTER = new AtomicInteger(0);
	private static final long GUI_THROTTLE_THRESHOLD_NANOS = 33_000_000L;

	private GuiThrottlePolicy() {
	}

	public static boolean shouldRenderGui(long averageFrameNanos, boolean hasOpenScreen) {
		if (hasOpenScreen || averageFrameNanos < GUI_THROTTLE_THRESHOLD_NANOS) {
			return true;
		}

		int frame = GUI_FRAME_COUNTER.incrementAndGet();
		return (frame & 1) == 0;
	}
}
