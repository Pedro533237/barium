package com.barium.client.render.culling;

import org.joml.Matrix4f;

/**
 * Frustum culling baseado em matriz ViewProjection.
 * Mantém 6 planos normalizados para testes rápidos de AABB.
 */
public final class FrustumCullingManager {

    private final float[][] planes = new float[6][4];

    public void updateFromViewProjection(Matrix4f vp) {
        Matrix4f m = new Matrix4f(vp);

        // Left / Right / Bottom / Top / Near / Far
        extractPlane(0, m.m03() + m.m00(), m.m13() + m.m10(), m.m23() + m.m20(), m.m33() + m.m30());
        extractPlane(1, m.m03() - m.m00(), m.m13() - m.m10(), m.m23() - m.m20(), m.m33() - m.m30());
        extractPlane(2, m.m03() + m.m01(), m.m13() + m.m11(), m.m23() + m.m21(), m.m33() + m.m31());
        extractPlane(3, m.m03() - m.m01(), m.m13() - m.m11(), m.m23() - m.m21(), m.m33() - m.m31());
        extractPlane(4, m.m03() + m.m02(), m.m13() + m.m12(), m.m23() + m.m22(), m.m33() + m.m32());
        extractPlane(5, m.m03() - m.m02(), m.m13() - m.m12(), m.m23() - m.m22(), m.m33() - m.m32());
    }

    public boolean isAabbVisible(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        for (float[] p : planes) {
            float px = p[0] >= 0.0f ? maxX : minX;
            float py = p[1] >= 0.0f ? maxY : minY;
            float pz = p[2] >= 0.0f ? maxZ : minZ;

            if ((p[0] * px) + (p[1] * py) + (p[2] * pz) + p[3] < 0.0f) {
                return false;
            }
        }
        return true;
    }

    private void extractPlane(int index, float a, float b, float c, float d) {
        float invLength = (float) (1.0 / Math.sqrt((a * a) + (b * b) + (c * c)));
        planes[index][0] = a * invLength;
        planes[index][1] = b * invLength;
        planes[index][2] = c * invLength;
        planes[index][3] = d * invLength;
    }
}
