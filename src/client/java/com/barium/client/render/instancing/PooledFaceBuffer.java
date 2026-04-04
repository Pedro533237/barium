package com.barium.client.render.instancing;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Armazena instâncias compactas de face (16 bytes por face):
 * [x,y,z,face,material,light,flags,reserved] como bytes e shorts compactados.
 */
public final class PooledFaceBuffer {
    public static final int BYTES_PER_FACE = 16;

    private ByteBuffer buffer;
    private int faceCount;

    public PooledFaceBuffer(int initialFaces) {
        this.buffer = ByteBuffer.allocateDirect(Math.max(1, initialFaces) * BYTES_PER_FACE).order(ByteOrder.nativeOrder());
    }

    public void clear() {
        buffer.clear();
        faceCount = 0;
    }

    public void putFace(int x, int y, int z, int faceId, int materialId, int packedLight, int flags) {
        ensureCapacity(faceCount + 1);

        int base = faceCount * BYTES_PER_FACE;
        buffer.putShort(base, (short) x);
        buffer.putShort(base + 2, (short) y);
        buffer.putShort(base + 4, (short) z);
        buffer.put(base + 6, (byte) faceId);
        buffer.put(base + 7, (byte) flags);
        buffer.putInt(base + 8, packedLight);
        buffer.putInt(base + 12, materialId);

        faceCount++;
    }

    public ByteBuffer data() {
        buffer.limit(faceCount * BYTES_PER_FACE);
        return buffer;
    }

    public int faceCount() {
        return faceCount;
    }

    private void ensureCapacity(int wantedFaces) {
        int wantedBytes = wantedFaces * BYTES_PER_FACE;
        if (wantedBytes <= buffer.capacity()) {
            return;
        }

        int newCapacity = Math.max(wantedBytes, buffer.capacity() * 2);
        ByteBuffer newBuffer = ByteBuffer.allocateDirect(newCapacity).order(ByteOrder.nativeOrder());

        int previousLimit = buffer.limit();
        buffer.limit(faceCount * BYTES_PER_FACE);
        newBuffer.put(buffer);
        newBuffer.clear();

        buffer.limit(previousLimit);
        buffer = newBuffer;
    }
}
