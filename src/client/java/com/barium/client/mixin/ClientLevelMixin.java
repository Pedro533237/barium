package com.barium.client.mixin;

import com.barium.config.BariumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {

    /** Entidades distantes só fazem tick a cada 4 ticks do mundo (ClientLevel.tickEntities = 1,97% no spark). */
    @Inject(method = "tickNonPassenger", at = @At("HEAD"), cancellable = true)
    private void barium$cullDistantEntityTicks(Entity entity, CallbackInfo ci) {
        if (!BariumConfig.C.ENABLE_ENTITY_TICK_CULLING) return;
        if (entity instanceof Player || entity.isVehicle() || entity.isPassenger()) return;

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if (entity.distanceToSqr(player) > BariumConfig.C.ENTITY_TICK_CULLING_DISTANCE_SQ) {
            // Usa o tempo do mundo (e não entity.tickCount, que nunca avançaria se o tick fosse pulado).
            if (((ClientLevel) (Object) this).getGameTime() % 4 != 0) {
                ci.cancel();
            }
        }
    }

    /** Partículas ambiente de blocos (ClientLevel.animateTick = 1,38% no spark): roda a cada 2 ticks. */
    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void barium$reduceAmbientParticles(int x, int y, int z, CallbackInfo ci) {
        if (!BariumConfig.C.REDUCE_AMBIENT_PARTICLES) return;
        if (((ClientLevel) (Object) this).getGameTime() % 2 == 0) {
            ci.cancel();
        }
    }
}
