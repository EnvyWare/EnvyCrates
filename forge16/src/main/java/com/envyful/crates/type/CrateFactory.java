package com.envyful.crates.type;

import com.envyful.crates.type.crate.CrateType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CrateFactory {

    private static final Map<String, CrateType> LOADED_CRATES = Maps.newHashMap();

    public static CrateType get(String id) {
        return LOADED_CRATES.get(id.toLowerCase(Locale.ROOT));
    }

    public static void register(CrateType crateType) {
        LOADED_CRATES.put(crateType.id().toLowerCase(Locale.ROOT), crateType);
    }

    public static List<CrateType> getAll() {
        return Lists.newArrayList(LOADED_CRATES.values());
    }
}
