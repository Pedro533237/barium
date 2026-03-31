package com.barium.client.mixin;

import com.barium.config.BariumConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
    private static long barium$lastPollEventsMs = 0L;

    /**
     * Quando o jogo está sem foco, não precisamos processar eventos na taxa máxima
     * de frames. Isso reduz custo de pollEvents sem impactar jogabilidade em foco.
     */
    @Inject(method = "pollEvents", at = @At("HEAD"), cancellable = true)
    private static void barium$throttlePollEventsWhenUnfocused(CallbackInfo ci) {
        if (!BariumConfig.C.ENABLE_BACKGROUND_EVENT_THROTTLING) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.isWindowFocused()) return;

        long now = Util.getMeasuringTimeMs();
        if (now - barium$lastPollEventsMs < 8L) {
            ci.cancel();
            return;
        }

        barium$lastPollEventsMs = now;
    }
}
