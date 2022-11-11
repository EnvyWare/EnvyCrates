package com.envyful.crates.type.reward;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.pane.Pane;
import com.google.gson.JsonElement;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface RewardType {

    String id();

    double getWeight();

    void give(ForgeEnvyPlayer player);

    void display(Pane pane, int page);

    ItemStack getDisplayItem();

    void read(JsonElement element) throws CommandSyntaxException;

    void playSound(PlayerEntity player);

}
