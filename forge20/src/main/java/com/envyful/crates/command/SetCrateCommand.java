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
import com.envyful.crates.type.CrateFactory;
import com.envyful.crates.type.crate.CrateType;
import net.minecraft.core.BlockPos;

@Command(
        value = {
                "setcrate",
                "sc",
                "set"
        }
)
@Permissible("com.envyful.crates.command.set")
@Description("Sets a crate at the specified location")
public class SetCrateCommand {

    @CommandProcessor
    public void onCommand(@Sender ForgeEnvyPlayer sender,
                          @Completable(CrateTabCompleter.class) @Argument CrateType crate,
                          @Argument(defaultValue = "looking") BlockPos block) {
        if (CrateFactory.isCrate(sender.getParent().level(), block)) {
            for (String s : EnvyCrates.getInstance().getLocale().getCrateAlreadyThere()) {
                sender.message(UtilChatColour.colour(s));
            }
            return;
        }

        CrateFactory.add(sender.getParent().level(), block, crate);
        for (String s : EnvyCrates.getInstance().getLocale().getCrateAdded()) {
            sender.message(UtilChatColour.colour(s
                    .replace("%pos%", block.getX() + "," + block.getY() + "," + block.getZ())
                    .replace("%crate%", crate.id())));
        }
    }
}
