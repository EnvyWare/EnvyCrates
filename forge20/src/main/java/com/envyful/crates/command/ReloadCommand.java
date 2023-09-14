package com.envyful.crates.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.description.Description;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.crate.CrateTypeFactory;
import net.minecraft.commands.CommandSource;

@Command(
        value = "reload"
)
@Permissible("com.envyful.crates.command.reload")
@Description("Reloads the config & crates")
public class ReloadCommand {

    @CommandProcessor
    public void onCommand(@Sender CommandSource sender, String[] args) {
        EnvyCrates.getInstance().reloadConfig();
        CrateTypeFactory.reload();
        sender.sendSystemMessage(UtilChatColour.colour("&e&l(!) &eReloaded configs & crates."));
    }
}
