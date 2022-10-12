package com.envyful.crates;

import com.envyful.api.command.sender.SenderTypeFactory;
import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.gui.factory.ForgeGuiFactory;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.type.UtilParse;
import com.envyful.crates.command.CrateTabCompleter;
import com.envyful.crates.command.EnvyCrateCommand;
import com.envyful.crates.command.ForgeEnvyPlayerSender;
import com.envyful.crates.config.EnvyCratesLocale;
import com.envyful.crates.listener.CrateBreakListener;
import com.envyful.crates.listener.CrateInteractListener;
import com.envyful.crates.type.CrateFactory;
import com.envyful.crates.type.crate.CrateType;
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
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
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
    public void onServerStopping(FMLServerStoppingEvent event) {
        CrateFactory.save();
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        CrateTypeFactory.read();
        reloadConfig();
        GuiFactory.setPlatformFactory(new ForgeGuiFactory());

        CrateFactory.load();

        MinecraftForge.EVENT_BUS.register(new CrateInteractListener());
        MinecraftForge.EVENT_BUS.register(new CrateBreakListener());
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
        SenderTypeFactory.register(new ForgeEnvyPlayerSender());
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
            ServerPlayerEntity player = (ServerPlayerEntity) source;

            if (args[0].equals("below_me")) {
                BlockPos pos = player.blockPosition().below();
                BlockState blockState = player.getLevel().getBlockState(pos);

                if (blockState.isAir()) {
                    for (String s : this.locale.getCannotSetAir()) {
                        player.sendMessage(UtilChatColour.colour(s), Util.NIL_UUID);
                    }
                    return null;
                }

                return pos;
            }

            String[] split = args[0].split(",");

            if (split.length != 3) {
                return null;
            }

            BlockPos pos = new BlockPos(
                    UtilParse.parseInteger(split[0].replace("~", player.position().x + "")).orElse(-1),
                    UtilParse.parseInteger(split[1].replace("~", player.position().y + "")).orElse(-1),
                    UtilParse.parseInteger(split[2].replace("~", player.position().z + "")).orElse(-1)
            );

            BlockState blockState = player.getLevel().getBlockState(pos);

            if (blockState.isAir()) {
                for (String s : this.locale.getCannotSetAir()) {
                    player.sendMessage(UtilChatColour.colour(s), Util.NIL_UUID);
                }
                return null;
            }

            return pos;
        });
        this.commandFactory.registerInjector(CrateType.class, (source, args) -> {
            return CrateFactory.get(args[0]); //TODO
        });
        this.commandFactory.registerCommand(event.getDispatcher(), new EnvyCrateCommand());
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
