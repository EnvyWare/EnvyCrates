package com.envyful.crates;

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
import com.envyful.crates.config.EnvyCratesLocale;
import com.envyful.crates.listener.CrateBreakListener;
import com.envyful.crates.listener.CrateInteractListener;
import com.envyful.crates.type.CrateFactory;
import com.envyful.crates.type.crate.CrateType;
import com.envyful.crates.type.crate.CrateTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod("envycrates")
public class EnvyCrates {

    public static final String KEY_NBT_TAG = "ENVY_CRATES";

    private static final Logger LOGGER = LogManager.getLogger("envycrates");

    private static EnvyCrates instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory(playerManager);

    private EnvyCratesLocale locale;

    public EnvyCrates() {
        instance = this;
        UtilLogger.setLogger(LOGGER);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        CrateFactory.save();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
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
        this.commandFactory.registerCompleter(new CrateTabCompleter());
        this.commandFactory.registerInjector(ForgeEnvyPlayer.class, (source, args) -> {
            ForgeEnvyPlayer onlinePlayer = this.playerManager.getOnlinePlayer(args[0]);

            if (onlinePlayer == null) {
                for (String s : this.locale.getNotOnline()) {
                    source.sendSystemMessage(UtilChatColour.colour(s));
                }
            }

            return onlinePlayer;
        });
        this.commandFactory.registerInjector(BlockPos.class, (source, args) -> {
            ServerPlayer player = (ServerPlayer) source;

            if (args[0].equals("below_me")) {
                BlockPos pos = player.blockPosition().below();
                BlockState blockState = player.level().getBlockState(pos);

                if (blockState.isAir()) {
                    for (String s : this.locale.getCannotSetAir()) {
                        player.sendSystemMessage(UtilChatColour.colour(s));
                    }
                    return null;
                }

                return pos;
            }

            if (args[0].equalsIgnoreCase("looking")) {
                BlockHitResult clip = player.level().clip(new ClipContext(
                        player.position(),
                        player.position().add(player.getLookAngle().scale(5)),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE, player));

                if (clip.getType() == BlockHitResult.Type.MISS) {
                    for (String s : this.locale.getCannotSetAir()) {
                        player.sendSystemMessage(UtilChatColour.colour(s));
                    }
                    return null;
                }

                return clip.getBlockPos();
            }

            String[] split = args[0].split(",");

            if (split.length != 3) {
                return null;
            }

            BlockPos pos = new BlockPos(
                    UtilParse.parseInt(split[0].replace("~", player.position().x + "")).orElse(-1),
                    UtilParse.parseInt(split[1].replace("~", player.position().y + "")).orElse(-1),
                    UtilParse.parseInt(split[2].replace("~", player.position().z + "")).orElse(-1)
            );

            BlockState blockState = player.level().getBlockState(pos);

            if (blockState.isAir()) {
                for (String s : this.locale.getCannotSetAir()) {
                    player.sendSystemMessage(UtilChatColour.colour(s));
                }
                return null;
            }

            return pos;
        });
        this.commandFactory.registerInjector(CrateType.class, (source, args) -> CrateFactory.get(args[0]));

        this.commandFactory.registerCommand(event.getDispatcher(), this.commandFactory.parseCommand(new EnvyCrateCommand()));
    }

    public static EnvyCrates getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static ForgePlayerManager getPlayerManager() {
        return instance.playerManager;
    }

    public static EnvyCratesLocale getLocale() {
        return instance.locale;
    }
}
