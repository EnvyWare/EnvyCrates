package com.envyful.crates.type.reward.impl;

import com.envyful.api.forge.config.ConfigSound;
import com.envyful.api.json.UtilGson;
import com.envyful.crates.type.reward.RewardType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public abstract class AbstractRewardType implements RewardType {

    protected String id;
    protected double weight;
    private ConfigSound winSound;

    public AbstractRewardType() {
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public void read(JsonElement element) throws CommandSyntaxException {
        JsonObject object = element.getAsJsonObject();

        this.id = object.get("id").getAsString();
        this.weight = object.get("weight").getAsDouble();

        if (object.has("win_sound")) {
            this.winSound = UtilGson.GSON.fromJson(object.get("win_sound"), ConfigSound.class);
        }
    }

    @Override
    public void playSound(PlayerEntity player) {
        if (this.winSound != null) {
            this.winSound.playSound((ServerPlayerEntity) player);
        }
    }
}
