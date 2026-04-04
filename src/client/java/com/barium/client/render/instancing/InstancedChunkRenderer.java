package com.barium.client.render.instancing;

import com.barium.config.BariumConfig;

/**
 * Estrutura para draw instanciado. A ligação com VAO/VBO e shader é feita pela pipeline do mod.
 */
public final class InstancedChunkRenderer {

    private final PooledFaceBuffer pooledFaces = new PooledFaceBuffer(8_192);

    public PooledFaceBuffer pooledFaces() {
        return pooledFaces;
    }

    public boolean shouldUseInstancing() {
        return BariumConfig.C.ENABLE_VERTEX_POOLING && BariumConfig.C.ENABLE_INSTANCED_RENDERING;
    }

    public int flushFaceCount() {
        int count = pooledFaces.faceCount();
        pooledFaces.clear();
        return count;
    }
}
