package com.envyful.crates.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.crate.CrateTypeFactory;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;

@Command(
        value = {
                "reload"
        }
)
@Permissible("com.envyful.crates.command.reload")
public class ReloadCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSource sender, String[] args) {
        EnvyCrates.getInstance().reloadConfig();
        CrateTypeFactory.reload();
        sender.sendMessage(UtilChatColour.colour("&e&l(!) &eReloaded configs & crates."), Util.NIL_UUID);
    }
}
