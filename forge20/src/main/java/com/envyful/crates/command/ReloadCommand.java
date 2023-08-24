package com.envyful.crates.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.crate.CrateTypeFactory;
import net.minecraft.commands.CommandSource;

@Command(
        value = "reload",
        description = "Reload the config",
        aliases = {
                "reload"
        }
)
@Permissible("com.envyful.crates.command.reload")
@Child
public class ReloadCommand {

    @CommandProcessor
    public void onCommand(@Sender CommandSource sender, String[] args) {
        EnvyCrates.getInstance().reloadConfig();
        CrateTypeFactory.reload();
        sender.sendSystemMessage(UtilChatColour.colour("&e&l(!) &eReloaded configs & crates."));
    }
}
