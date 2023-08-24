package com.envyful.crates.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.command.completion.player.PlayerTabCompleter;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.crates.type.crate.CrateType;
import net.minecraft.commands.CommandSource;

@Command(
        value = "givekey",
        description = "Gives a key to the command",
        aliases = {
                "give",
                "gk"
        }
)
@Permissible("com.envyful.crates.command.give")
@Child
public class GiveKeyCommand {

    @CommandProcessor
    public void onCommand(@Sender CommandSource sender,
                          @Completable(PlayerTabCompleter.class) @Argument ForgeEnvyPlayer target,
                          @Completable(CrateTabCompleter.class) @Argument CrateType crate,
                          @Argument(defaultValue = "1") int amount) {
        sender.sendSystemMessage(UtilChatColour.colour("&e&l(!) &eGiving keys"));
        crate.giveKey(target, amount);
    }
}
