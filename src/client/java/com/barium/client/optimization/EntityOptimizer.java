package com.barium.client.optimization;

import com.barium.config.BariumConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class EntityOptimizer {

    /** Retorna true se a entidade deve ser renderizada (culling só por distância). */
    public static boolean shouldRenderByDistance(Entity entity, double camX, double camY, double camZ) {
        if (entity.getType() == EntityType.PLAYER || entity.isVehicle() || entity.isPassenger() || entity.isCurrentlyGlowing()) {
            return true;
        }
        if (isProtectedType(entity)) {
            return true;
        }
        return entity.distanceToSqr(camX, camY, camZ) <= BariumConfig.C.MAX_ENTITY_RENDER_DISTANCE_SQ;
    }

    /** Dragão, barcos e minecarts nunca sofrem culling (evita depender de pacotes que mudam entre versões). */
    private static boolean isProtectedType(Entity entity) {
        String id = entity.getType().getDescriptionId();
        return id.contains("ender_dragon") || id.contains("boat") || id.contains("raft") || id.contains("minecart");
    }
}
