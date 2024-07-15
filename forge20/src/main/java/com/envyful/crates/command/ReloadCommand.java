package com.envyful.crates.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.description.Description;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.crate.CrateTypeFactory;
import net.minecraft.commands.CommandSource;

import java.util.List;

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
        PlatformProxy.sendMessage(sender, List.of("&e&l(!) &eReloaded configs & crates"));
    }
}
