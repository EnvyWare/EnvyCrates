package com.envyful.crates.command;

import com.envyful.api.command.injector.TabCompleter;
import com.envyful.crates.type.CrateFactory;
import com.envyful.crates.type.crate.CrateType;
import net.minecraft.commands.CommandSource;

import java.lang.annotation.Annotation;
import java.util.List;

public class CrateTabCompleter implements TabCompleter<CommandSource> {
    @Override
    public List<String> getCompletions(CommandSource sender, String[] currentData, Annotation... completionData) {
        return CrateFactory.getAll().stream().map(CrateType::id).toList();
    }
}
