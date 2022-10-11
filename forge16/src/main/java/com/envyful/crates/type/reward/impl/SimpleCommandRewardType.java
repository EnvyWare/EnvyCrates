package com.envyful.crates.type.reward.impl;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.forge.server.UtilForgeServer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleCommandRewardType extends AbstractRewardType {

    private List<String> commands;
    private ItemStack display;
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
        this.display = ItemStack.of(JsonToNBT.parseTag(object.get("display").getAsJsonObject().toString()));

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

        pane.set(this.displayX, this.displayY, GuiFactory.displayable(this.display));
    }

    @Override
    public ItemStack getDisplayItem() {
        return this.display;
    }
}
