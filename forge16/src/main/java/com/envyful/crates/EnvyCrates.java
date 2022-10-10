package com.envyful.crates;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.crates.type.crate.CrateTypeFactory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("envycrates")
public class EnvyCrates {

    private static EnvyCrates instance;

    private Logger logger = LogManager.getLogger("envycrates");

    public EnvyCrates() {
        instance = this;
        UtilLogger.setLogger(this.logger);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        CrateTypeFactory.read();
    }

    public static EnvyCrates getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return instance.logger;
    }
}
