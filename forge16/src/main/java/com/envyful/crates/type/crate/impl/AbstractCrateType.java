package com.envyful.crates.type.crate.impl;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.crates.type.crate.CrateType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.StringNBT;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractCrateType implements CrateType {

    protected String id;
    protected String displayName;
    protected ItemStack itemStack;
    protected List<String> givenKeyMessage;

    protected AbstractCrateType() {}

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public void giveKey(ForgeEnvyPlayer player, int amount) {
        ItemStack copy = this.itemStack.copy();
        copy.setCount(amount);
        copy.getOrCreateTag().put("ENVY_CRATES", StringNBT.valueOf(this.id));
        player.getParent().addItem(copy);

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
    public void read(JsonElement element) throws CommandSyntaxException {
        JsonObject jsonObject = element.getAsJsonObject();

        this.id = jsonObject.get("id").getAsString();
        this.displayName = jsonObject.get("display_name").getAsString();
        this.itemStack = ItemStack.of(JsonToNBT.parseTag(jsonObject.get("key").getAsJsonObject().toString()));
        this.givenKeyMessage = Stream.of(jsonObject.get("given_key_message").getAsJsonArray()).map(JsonElement::getAsString).collect(Collectors.toList());
    }
}
