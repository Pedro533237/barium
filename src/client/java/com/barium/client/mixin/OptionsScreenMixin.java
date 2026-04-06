package com.barium.client.mixin;

import com.barium.client.config.BariumConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin {

    @Shadow
    protected MinecraftClient client;

    @Shadow
    protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);

    @Inject(method = "init", at = @At("TAIL"))
    private void barium$addConfigButton(CallbackInfo ci) {
        Screen screen = (Screen) (Object) this;

        int buttonWidth = 150;
        int buttonHeight = 20;
        int x = screen.width / 2 - 155;
        int y = screen.height / 6 + 144;

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("title.barium.config"), button -> {
                    if (this.client != null) {
                        this.client.setScreen(BariumConfigScreen.create(screen));
                    }
                }).dimensions(x, y, buttonWidth, buttonHeight)
                .build());
    }
}
