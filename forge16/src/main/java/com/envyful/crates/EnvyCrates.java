package com.envyful.crates;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.crates.config.EnvyCratesLocale;
import com.envyful.crates.type.crate.CrateTypeFactory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod("envycrates")
public class EnvyCrates {

    private static EnvyCrates instance;

    private Logger logger = LogManager.getLogger("envycrates");

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private EnvyCratesLocale locale;

    public EnvyCrates() {
        instance = this;
        UtilLogger.setLogger(this.logger);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        CrateTypeFactory.read();
    }

    public void reloadConfig() {
        try {
            this.locale = YamlConfigFactory.getInstance(EnvyCratesLocale.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static EnvyCrates getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return instance.logger;
    }

    public ForgePlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public EnvyCratesLocale getLocale() {
        return this.locale;
    }
}
