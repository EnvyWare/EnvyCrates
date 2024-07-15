package com.envyful.crates.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.crates.EnvyCrates;
import net.minecraft.command.ICommandSource;

@Command(
        value = {
                "envycrates",
                "crates"
        }
)
@Permissible("com.envyful.crates.command")
@SubCommands({ReloadCommand.class, GiveKeyCommand.class, SetCrateCommand.class, GiveKeyAllCommand.class})
public class EnvyCrateCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSource sender, String[] args) {
        PlatformProxy.sendMessage(sender, EnvyCrates.getLocale().getRootCommand());
    }
}
