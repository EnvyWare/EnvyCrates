package com.envyful.crates.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.crate.CrateTypeFactory;
import net.minecraft.command.ICommandSource;

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
    public void onCommand(@Sender ICommandSource sender, String[] args) {
        EnvyCrates.getInstance().reloadConfig();
        CrateTypeFactory.reload();
    }
}
