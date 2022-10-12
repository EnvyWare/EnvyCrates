package com.envyful.crates.type;

import com.envyful.crates.type.crate.CrateType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class CrateFactory {

    private static final Map<String, CrateType> LOADED_CRATES = Maps.newHashMap();
    private static final Map<BlockPos, String> CRATES = Maps.newConcurrentMap();

    public static CrateType get(String id) {
        return LOADED_CRATES.get(id.toLowerCase(Locale.ROOT));
    }

    public static void register(CrateType crateType) {
        LOADED_CRATES.put(crateType.id().toLowerCase(Locale.ROOT), crateType);
    }

    public static List<CrateType> getAll() {
        return Lists.newArrayList(LOADED_CRATES.values());
    }

    public static boolean isCrate(BlockPos pos) {
        return CRATES.containsKey(pos);
    }

    @Nullable
    public static CrateType getCrateType(BlockPos pos) {
        return Optional.ofNullable(CRATES.get(pos)).map(CrateFactory::get).orElse(null);
    }

    public static void remove(BlockPos pos) {
        CRATES.remove(pos);
    }

    public static void add(BlockPos pos, CrateType crateType) {
        CRATES.put(pos, crateType.id());
    }

    public static void save() {

    }

    public static void load() {

    }
}
