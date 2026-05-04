package com.barium.client.optimization;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Arrays;

public final class FrameTimeTracker {
	private static final AtomicLong AVERAGE_FRAME_NANOS = new AtomicLong(16_000_000L);
	private static final int WINDOW_SIZE = 120;
	private static final AtomicLongArray FRAME_WINDOW = new AtomicLongArray(WINDOW_SIZE);
	private static final AtomicInteger WINDOW_INDEX = new AtomicInteger(0);
	private static final AtomicInteger SAMPLES = new AtomicInteger(0);

	private FrameTimeTracker() {
	}

	public static long beginFrame() {
		return System.nanoTime();
	}

	public static void endFrame(long startedAtNanos) {
		long frameTime = Math.max(0L, System.nanoTime() - startedAtNanos);
		AVERAGE_FRAME_NANOS.updateAndGet(previous -> ((previous * 7L) + frameTime) / 8L);
		int index = Math.floorMod(WINDOW_INDEX.getAndIncrement(), WINDOW_SIZE);
		FRAME_WINDOW.set(index, frameTime);
		SAMPLES.updateAndGet(previous -> Math.min(previous + 1, WINDOW_SIZE));
	}

	public static long getAverageFrameNanos() {
		return AVERAGE_FRAME_NANOS.get();
	}

	public static long getP95FrameNanos() {
		int count = SAMPLES.get();
		if (count <= 0) {
			return AVERAGE_FRAME_NANOS.get();
		}

		long[] copy = new long[count];
		for (int i = 0; i < count; i++) {
			copy[i] = FRAME_WINDOW.get(i);
		}
		Arrays.sort(copy);
		int percentileIndex = Math.min(count - 1, (int) Math.ceil(count * 0.95D) - 1);
		return copy[Math.max(0, percentileIndex)];
	}
}
