package com.envyful.crates.type.reward;

import com.envyful.crates.type.reward.impl.SimpleCommandRewardType;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class RewardTypeFactory {

    private static final Map<String, Supplier<RewardType>> REGISTERED_REWARDS = Maps.newHashMap();

    static {
        register("simple_commands", SimpleCommandRewardType::new);
    }

    private RewardTypeFactory() {
        throw new UnsupportedOperationException("Static Factory");
    }

    public static void register(String id, Supplier<RewardType> supplier) {
        REGISTERED_REWARDS.put(id.toLowerCase(Locale.ROOT), supplier);
    }

    @Nullable
    public static RewardType getInstance(String id) {
        Supplier<RewardType> rewardTypeSupplier = REGISTERED_REWARDS.get(id.toLowerCase(Locale.ROOT));

        if (rewardTypeSupplier == null) {
            return null;
        }

        return rewardTypeSupplier.get();
    }

}
