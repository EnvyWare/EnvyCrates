package com.envyful.crates.type.crate;

import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.CrateFactory;
import com.envyful.crates.type.crate.impl.SimpleItemCrate;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class CrateTypeFactory {

    private static final Gson GSON = new GsonBuilder().create();
    private static final Map<String, Supplier<CrateType>> TYPES = Maps.newHashMap();

    static {
        register("simple", SimpleItemCrate::new);
    }

    public static void register(String id, Supplier<CrateType> crateType) {
        TYPES.put(id.toLowerCase(Locale.ROOT), crateType);
    }

    @Nullable
    public static CrateType getInstance(String id) {
        Supplier<CrateType> crateTypeSupplier = TYPES.get(id.toLowerCase(Locale.ROOT));

        if (crateTypeSupplier == null) {
            return null;
        }

        return crateTypeSupplier.get();
    }

    public static void read() {
        File cratesFolder = Paths.get("config/EnvyCrates/crates").toFile();

        if (!cratesFolder.exists()) {
            cratesFolder.mkdirs();
        }

        loadCrateFromDirectory(cratesFolder);
    }

    private static void loadCrateFromDirectory(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                loadCrateFromDirectory(directory);
                continue;
            }

            JsonElement json = getJson(file);

            if (json == null) {
                continue;
            }

            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.getAsJsonPrimitive("type").getAsString();
            CrateType instance = getInstance(type);

            if (instance == null) {
                EnvyCrates.getLogger().error("Invalid crate type `" + type + "` in file `" + file.getAbsolutePath() + "`.");
                continue;
            }

            try {
                instance.read(json);
                CrateFactory.register(instance);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private static JsonElement getJson(File file) {
        try (FileReader fileReader = new FileReader(file)) {
            return GSON.fromJson(fileReader, JsonElement.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
