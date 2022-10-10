package com.envyful.crates;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@Mod("envycrates")
public class EnvyCrates {

    private static EnvyCrates instance;

    public EnvyCrates() {
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {}

    public static EnvyCrates getInstance() {
        return instance;
    }
}
