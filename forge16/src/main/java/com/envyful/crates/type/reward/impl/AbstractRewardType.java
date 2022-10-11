package com.envyful.crates.type.reward.impl;

import com.envyful.crates.type.reward.RewardType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public abstract class AbstractRewardType implements RewardType {

    protected String id;
    protected double weight;

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
    }
}
