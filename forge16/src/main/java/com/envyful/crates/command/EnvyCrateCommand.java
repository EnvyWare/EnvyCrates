package com.envyful.crates.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.crates.EnvyCrates;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;

@Command(
        value = "envycrates",
        description = "Root crates command",
        aliases = {
                "crates"
        }
)
@Permissible("com.envyful.crates.command")
@SubCommands({ReloadCommand.class, GiveKeyCommand.class, SetCrateCommand.class, GiveKeyAllCommand.class})
public class EnvyCrateCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSource sender, String[] args) {
        for (String s : EnvyCrates.getInstance().getLocale().getRootCommand()) {
            sender.sendMessage(UtilChatColour.colour(s), Util.NIL_UUID);
        }
    }
}
