package com.envyful.crates.type.crate.impl;

import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.json.UtilGson;
import com.envyful.crates.type.crate.CrateType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractCrateType implements CrateType {

    protected String id;
    protected String displayName;
    protected ExtendedConfigItem itemStack;
    protected List<String> givenKeyMessage;
    protected List<String> needAKeyMessage;

    protected AbstractCrateType() {}

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public void giveKey(ForgeEnvyPlayer player, int amount) {
        ItemStack copy = UtilConfigItem.fromConfigItem(this.itemStack);
        copy.setCount(amount);
        copy.getOrCreateTag().put("ENVY_CRATES", StringTag.valueOf(this.id));

        UtilForgeConcurrency.runSync(() -> {
            player.getParent().addItem(copy);
            player.getParent().getInventory().setChanged();
        });

        if (this.givenKeyMessage != null) {
            for (String s : this.givenKeyMessage) {
                player.message(UtilChatColour.colour(s
                        .replace("%display_name%", this.displayName)
                        .replace("%amount%", String.valueOf(amount)))
                );
            }
        }
    }

    @Override
    public void needAKey(ForgeEnvyPlayer player) {
        for (String s : this.needAKeyMessage) {
            player.message(UtilChatColour.colour(s));
        }
    }

    @Override
    public void read(JsonElement element) throws CommandSyntaxException {
        JsonObject jsonObject = element.getAsJsonObject();

        this.id = jsonObject.get("id").getAsString();
        this.displayName = jsonObject.get("display_name").getAsString();
        this.itemStack = UtilGson.GSON.fromJson(jsonObject.get("key"), ExtendedConfigItem.class);
        this.givenKeyMessage = Stream.of(jsonObject.get("given_key_message").getAsJsonArray()).map(JsonElement::getAsString).collect(Collectors.toList());
        this.needAKeyMessage = Stream.of(jsonObject.get("need_a_key").getAsJsonArray()).map(JsonElement::getAsString).collect(Collectors.toList());
    }
}
