package com.envyful.crates.type.crate.impl;

import com.envyful.api.math.RandomWeightedSet;
import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.reward.RewardType;
import com.envyful.crates.type.reward.RewardTypeFactory;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.List;

public class SimpleItemCrate extends AbstractCrateType {

    private RandomWeightedSet<RewardType> rewards;

    public SimpleItemCrate() {
        super();
    }

    @Override
    public void read(JsonElement element) throws CommandSyntaxException {
        super.read(element);

        JsonObject jsonObject = element.getAsJsonObject();

        for (JsonElement jsonElement : jsonObject.getAsJsonArray("rewards")) {
            JsonObject rewardObject = jsonElement.getAsJsonObject();
            String type = rewardObject.get("type").getAsString();
            RewardType rewardType = RewardTypeFactory.getInstance(type);

            if (rewardType == null) {
                EnvyCrates.getLogger().error("Invalid reward type `" + type + "` in `" + this.id + "`");
                continue;
            }

            rewardType.read(jsonElement);
            this.rewards.add(rewardType, rewardType.getWeight());
        }
    }

    @Override
    public RewardType generateRandomReward() {
        return this.rewards.getRandom();
    }

    @Override
    public List<RewardType> getAllRewards() {
        return Lists.newArrayList(this.rewards.keySet());
    }
}
