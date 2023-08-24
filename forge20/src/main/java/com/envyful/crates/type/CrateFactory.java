package com.envyful.crates.type;

import com.envyful.api.forge.world.UtilWorld;
import com.envyful.api.type.Pair;
import com.envyful.api.type.UtilParse;
import com.envyful.crates.type.crate.CrateType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class CrateFactory {

    private static final Map<String, CrateType> LOADED_CRATES = Maps.newHashMap();
    private static final Map<Pair<String, BlockPos>, String> CRATES = Maps.newConcurrentMap();

    private CrateFactory() {
        throw new UnsupportedOperationException("Static factory");
    }

    public static CrateType get(String id) {
        return LOADED_CRATES.get(id.toLowerCase(Locale.ROOT));
    }

    public static void clear() {
        LOADED_CRATES.clear();
    }

    public static void register(CrateType crateType) {
        LOADED_CRATES.put(crateType.id().toLowerCase(Locale.ROOT), crateType);
    }

    public static List<CrateType> getAll() {
        return Lists.newArrayList(LOADED_CRATES.values());
    }

    public static boolean isCrate(Level level, BlockPos pos) {
        return CRATES.containsKey(Pair.of(UtilWorld.getName(level), pos));
    }

    @Nullable
    public static CrateType getCrateType(Level level, BlockPos pos) {
        return Optional.ofNullable(CRATES.get(Pair.of(UtilWorld.getName(level), pos))).map(CrateFactory::get).orElse(null);
    }

    public static void remove(Level level, BlockPos pos) {
        CRATES.remove(Pair.of(UtilWorld.getName(level), pos));
    }

    public static void add(Level level, BlockPos pos, CrateType crateType) {
        CRATES.put(Pair.of(UtilWorld.getName(level), pos), crateType.id());
    }

    public static void save() {
        File cratePositions = Paths.get("config/EnvyCrates/crates.yml").toFile();

        if (cratePositions.exists()) {
            try {
                Files.delete(cratePositions.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!cratePositions.exists()) {
            try {
                cratePositions.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileWriter fileWriter = new FileWriter(cratePositions);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            for (Map.Entry<Pair<String, BlockPos>, String> entry : CRATES.entrySet()) {
                bufferedWriter.write(entry.getKey().getX() + " " + entry.getKey().getY().getX() + "," +
                        entry.getKey().getY().getY() + "," + entry.getKey().getY().getZ() + " " + entry.getValue());
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        File cratePositions = Paths.get("config/EnvyCrates/crates.yml").toFile();

        if (!cratePositions.exists()) {
            try {
                cratePositions.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileReader fileReader = new FileReader(cratePositions);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] args = line.split(" ");
                String world = args[0];
                String[] split = args[1].split(",");
                BlockPos pos = new BlockPos(
                        UtilParse.parseInteger(split[0]).orElse(0),
                        UtilParse.parseInteger(split[1]).orElse(0),
                        UtilParse.parseInteger(split[2]).orElse(0)
                );
                CRATES.put(Pair.of(world, pos), args[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
