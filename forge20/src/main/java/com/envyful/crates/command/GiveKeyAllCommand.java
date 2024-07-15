package com.envyful.crates.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.description.Description;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.crate.CrateType;
import net.minecraft.commands.CommandSource;

import java.util.List;

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
        PlatformProxy.sendMessage(sender, List.of("&e&l(!) &eGiving keys to all online players"));

        for (var player : EnvyCrates.getPlayerManager().getOnlinePlayers()) {
            crate.giveKey(player, amount);
        }
    }
}
