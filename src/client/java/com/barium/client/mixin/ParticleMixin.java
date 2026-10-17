package com.barium.client.mixin;

import com.barium.client.optimization.ParticleOptimizer;
import com.barium.config.BariumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Particle.class)
public abstract class ParticleMixin {

    @Shadow protected double x;
    @Shadow protected double y;
    @Shadow protected double z;
    @Shadow protected int age;
    @Shadow protected int lifetime;
    @Shadow public abstract void remove();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void barium$skipDistantParticleTick(CallbackInfo ci) {
        if (!BariumConfig.C.ENABLE_PARTICLE_OPTIMIZATION) return;

        Entity cam = Minecraft.getInstance().getCameraEntity();
        if (cam == null) return;

        if (ParticleOptimizer.isTooFar(cam.getEyePosition(), this.x, this.y, this.z)) {
            // Mantém o envelhecimento: sem isso a partícula distante nunca morreria.
            if (++this.age >= this.lifetime) {
                this.remove();
            }
            ci.cancel();
        }
    }
}
