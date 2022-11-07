package com.envyful.crates.type.reward.impl;

import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.server.UtilForgeServer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.json.UtilGson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleCommandRewardType extends AbstractRewardType {

    private List<String> commands;
    private ExtendedConfigItem display;
    private int displayX;
    private int displayY;
    private int page;

    public SimpleCommandRewardType() {
        super();
    }

    @Override
    public void give(ForgeEnvyPlayer player) {
        for (String command : this.commands) {
            UtilForgeServer.executeCommand(command.replace("%player%", player.getName()));
        }
    }

    @Override
    public void read(JsonElement element) throws CommandSyntaxException {
        super.read(element);

        JsonObject object = element.getAsJsonObject();

        this.commands = Stream.of(object.get("commands").getAsJsonArray()).map(JsonArray::getAsString).collect(Collectors.toList());
        this.display = UtilGson.GSON.fromJson(object.get("display"), ExtendedConfigItem.class);

        JsonObject displayData = object.getAsJsonObject("display_data");

        this.displayX = displayData.get("x").getAsInt();
        this.displayY = displayData.get("y").getAsInt();
        this.page = displayData.get("page").getAsInt();
    }

    @Override
    public void display(Pane pane, int page) {
        if (this.page != page) {
            return;
        }

        pane.set(this.displayX, this.displayY, GuiFactory.displayable(UtilConfigItem.fromConfigItem(this.display)));
    }

    @Override
    public ItemStack getDisplayItem() {
        return UtilConfigItem.fromConfigItem(this.display);
    }
}
