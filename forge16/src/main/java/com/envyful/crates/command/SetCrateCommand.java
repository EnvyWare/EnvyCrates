package com.envyful.crates.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.crates.type.CrateFactory;
import com.envyful.crates.type.crate.CrateType;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.math.BlockPos;

@Command(
        value = "setcrate",
        description = "Sets a crate position",
        aliases = {
                "sc",
                "set"
        }
)
@Permissible("com.envyful.crates.command.set")
@Child
public class SetCrateCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSource sender,
                          @Completable(CrateTabCompleter.class) @Argument CrateType crate,
                          @Argument BlockPos block) {
        if (CrateFactory.isCrate(block)) {
            //TODO:
            return;
        }

        CrateFactory.add(block, crate);
        //TODO:
    }
}
