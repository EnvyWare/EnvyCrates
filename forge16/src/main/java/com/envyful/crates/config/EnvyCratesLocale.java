package com.envyful.crates.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.google.common.collect.Lists;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
@ConfigPath("config/EnvyCrates/locale.yml")
public class EnvyCratesLocale extends AbstractYamlConfig {

    private List<String> rootCommand = Lists.newArrayList(
            "&e&l(!) &eYou can give crates with /crates givekey <name> <type> <amount>"
    );

    public EnvyCratesLocale() {
        super();
    }

    public List<String> getRootCommand() {
        return this.rootCommand;
    }
}