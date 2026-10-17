package com.barium.client.mixin;

import com.barium.config.BariumConfig;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureAtlas.class)
public class TextureAtlasMixin {

    // "tick" no 1.21.4 e anteriores; "cycleAnimationFrames" nas versões mais novas.
    @Inject(method = {"tick", "cycleAnimationFrames"}, at = @At("HEAD"), cancellable = true)
    private void barium$freezeAnimatedTextures(CallbackInfo ci) {
        if (BariumConfig.C.DISABLE_TEXTURE_ANIMATIONS) {
            ci.cancel();
        }
    }
}
