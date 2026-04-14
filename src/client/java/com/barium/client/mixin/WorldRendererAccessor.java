package com.barium.client.mixin;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {
    // CORREÇÃO: O campo 'frustum' foi renomeado para 'capturedFrustum' no Minecraft moderno.
    @Accessor("capturedFrustum")
    Frustum getFrustum();
}