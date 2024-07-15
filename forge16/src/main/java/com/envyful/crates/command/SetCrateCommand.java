package com.envyful.crates.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.text.Placeholder;
import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.CrateFactory;
import com.envyful.crates.type.crate.CrateType;
import net.minecraft.util.math.BlockPos;

@Command(
        value = {
                "setcrate",
                "sc",
                "set"
        }
)
@Permissible("com.envyful.crates.command.set")
public class SetCrateCommand {

    @CommandProcessor
    public void onCommand(@Sender ForgeEnvyPlayer sender,
                          @Completable(CrateTabCompleter.class) @Argument CrateType crate,
                          @Argument(defaultValue = "looking") BlockPos block) {
        if (CrateFactory.isCrate(sender.getParent().getLevel(), block)) {
            sender.message(EnvyCrates.getLocale().getCrateAlreadyThere());
            return;
        }

        CrateFactory.add(sender.getParent().getLevel(), block, crate);
        sender.message(EnvyCrates.getLocale().getCrateAdded(),
                Placeholder.simple("%pos%", block.getX() + "," + block.getY() + "," + block.getZ()),
                Placeholder.simple("%crate%", crate.id()));
    }
}
