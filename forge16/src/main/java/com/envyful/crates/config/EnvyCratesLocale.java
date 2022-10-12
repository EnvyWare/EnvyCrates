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

    private List<String> notOnline = Lists.newArrayList(
            "&c&l(!) &cThat player is not online"
    );

    private List<String> crateAlreadyThere = Lists.newArrayList(
            "&c&l(!) &cThat block is already a crate block!"
    );

    private List<String> crateAdded = Lists.newArrayList(
            "&e&l(!) &eSet %pos% to %crate%"
    );

    private List<String> crateRemoved = Lists.newArrayList(
            "&e&l(!) &eRemoved crate at %pos% for %crate%"
    );

    private List<String> cannotSetAir = Lists.newArrayList(
            "&c&l(!) &cCannot set air"
    );

    public EnvyCratesLocale() {
        super();
    }

    public List<String> getRootCommand() {
        return this.rootCommand;
    }

    public List<String> getNotOnline() {
        return this.notOnline;
    }

    public List<String> getCrateAlreadyThere() {
        return this.crateAlreadyThere;
    }

    public List<String> getCrateAdded() {
        return this.crateAdded;
    }

    public List<String> getCrateRemoved() {
        return this.crateRemoved;
    }

    public List<String> getCannotSetAir() {
        return this.cannotSetAir;
    }
}
