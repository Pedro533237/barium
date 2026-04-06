package com.barium.client.mixin;

import com.barium.client.config.BariumConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {

    protected OptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void barium$addConfigButton(CallbackInfo ci) {
        int buttonWidth = 150;
        int buttonHeight = 20;
        int x = this.width / 2 - 155;
        int y = this.height / 6 + 144;

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("title.barium.config"), button -> {
                    if (this.client != null) {
                        this.client.setScreen(BariumConfigScreen.create(this));
                    }
                }).dimensions(x, y, buttonWidth, buttonHeight)
                .build());
    }
}
