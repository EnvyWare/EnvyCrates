package com.envyful.crates.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.crate.CrateType;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;

@Command(
        value = "giveall",
        description = "Gives a key to the command",
        aliases = {
                "givekeyall",
                "gka"
        }
)
@Permissible("com.envyful.crates.command.give.all")
@Child
public class GiveKeyAllCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSource sender,
                          @Completable(CrateTabCompleter.class) @Argument CrateType crate,
                          @Argument(defaultValue = "1") int amount) {
        sender.sendMessage(UtilChatColour.colour("&e&l(!) &eGiving keys to all online players"), Util.NIL_UUID);

        for (ForgeEnvyPlayer player : EnvyCrates.getInstance().getPlayerManager().getOnlinePlayers()) {
            crate.giveKey(player, amount);
        }
    }
}
