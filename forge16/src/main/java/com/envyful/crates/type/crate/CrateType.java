package com.envyful.crates.type.crate;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.crates.type.reward.RewardType;
import com.google.gson.JsonElement;

import java.util.List;

public interface CrateType {

    String id();

    void giveKey(ForgeEnvyPlayer player, int amount);

    RewardType generateRandomReward();

    List<RewardType> getAllRewards();

    void read(JsonElement element);

}
