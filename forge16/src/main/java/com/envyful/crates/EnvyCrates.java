package com.envyful.crates;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.type.UtilParse;
import com.envyful.crates.command.CrateTabCompleter;
import com.envyful.crates.config.EnvyCratesLocale;
import com.envyful.crates.type.crate.CrateTypeFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
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

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        this.commandFactory.registerCompleter(new CrateTabCompleter());
        this.commandFactory.registerInjector(ForgeEnvyPlayer.class, (source, args) -> {
            ForgeEnvyPlayer onlinePlayer = this.playerManager.getOnlinePlayer(args[0]);

            if (onlinePlayer == null) {
                for (String s : this.locale.getNotOnline()) {
                    source.sendMessage(UtilChatColour.colour(s), Util.NIL_UUID);
                }
            }

            return onlinePlayer;
        });
        this.commandFactory.registerInjector(BlockPos.class, (source, args) -> {
            String[] split = args[0].split(",");

            if (split.length != 3) {
                return null;
            }

            ServerPlayerEntity player = (ServerPlayerEntity) source;

            BlockPos pos = new BlockPos(
                    UtilParse.parseInteger(split[0].replace("~", player.position().x + "")).orElse(-1),
                    UtilParse.parseInteger(split[1].replace("~", player.position().y + "")).orElse(-1),
                    UtilParse.parseInteger(split[2].replace("~", player.position().z + "")).orElse(-1)
            );

            BlockState blockState = player.getLevel().getBlockState(pos);

            if (blockState.isAir()) {
                return null;
            }

            return pos;
        });
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
