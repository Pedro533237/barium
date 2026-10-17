package com.barium.client;

import com.barium.BariumMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BariumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BariumMod.LOGGER.info("Barium Client (26.3) inicializado.");
    }
}
