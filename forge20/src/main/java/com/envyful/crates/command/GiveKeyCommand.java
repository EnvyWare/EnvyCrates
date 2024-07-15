package com.envyful.crates.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.description.Description;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.forge.command.completion.player.PlayerTabCompleter;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.crates.type.crate.CrateType;
import net.minecraft.commands.CommandSource;

import java.util.List;

@Command(
        value = {
                "givekey",
                "give",
                "gk"
        }
)
@Permissible("com.envyful.crates.command.give")
@Description("Gives a player a crate key")
public class GiveKeyCommand {

    @CommandProcessor
    public void onCommand(@Sender CommandSource sender,
                          @Completable(PlayerTabCompleter.class) @Argument ForgeEnvyPlayer target,
                          @Completable(CrateTabCompleter.class) @Argument CrateType crate,
                          @Argument(defaultValue = "1") int amount) {
        PlatformProxy.sendMessage(sender, List.of("&e&l(!) &eGiving keys"));
        crate.giveKey(target, amount);
    }
}
