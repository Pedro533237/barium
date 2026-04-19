package com.barium.client.optimization;

import java.util.concurrent.atomic.AtomicInteger;

public final class GuiThrottlePolicy {
	private static final AtomicInteger GUI_FRAME_COUNTER = new AtomicInteger(0);
	private static final long GUI_THROTTLE_THRESHOLD_NANOS = 25_000_000L;

	private GuiThrottlePolicy() {
	}

	public static boolean shouldRenderGui(long averageFrameNanos, boolean hasOpenScreen, boolean hudStateChanged) {
		if (hasOpenScreen || hudStateChanged || averageFrameNanos < GUI_THROTTLE_THRESHOLD_NANOS) {
			return true;
		}

		int frame = GUI_FRAME_COUNTER.incrementAndGet();
		return (frame & 1) == 0;
	}
}
