package com.envyful.crates.command;

import com.envyful.api.command.injector.TabCompleter;
import com.envyful.crates.type.CrateFactory;
import com.envyful.crates.type.crate.CrateType;
import net.minecraft.command.ICommandSource;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public class CrateTabCompleter implements TabCompleter<ICommandSource> {

    @Override
    public List<String> getCompletions(ICommandSource sender, String[] currentData, Annotation... completionData) {
        return CrateFactory.getAll().stream().map(CrateType::id).collect(Collectors.toList());
    }
}
