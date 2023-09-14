package com.envyful.crates.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.description.Description;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.crate.CrateType;
import net.minecraft.commands.CommandSource;

@Command(
        value = {
                "giveall",
                "givekeyall",
                "gka"
        }
)
@Permissible("com.envyful.crates.command.give.all")
@Description("Gives all online players a crate key")
public class GiveKeyAllCommand {

    @CommandProcessor
    public void onCommand(@Sender CommandSource sender,
                          @Completable(CrateTabCompleter.class) @Argument CrateType crate,
                          @Argument(defaultValue = "1") int amount) {
        sender.sendSystemMessage(UtilChatColour.colour("&e&l(!) &eGiving keys to all online players"));

        for (ForgeEnvyPlayer player : EnvyCrates.getInstance().getPlayerManager().getOnlinePlayers()) {
            crate.giveKey(player, amount);
        }
    }
}
