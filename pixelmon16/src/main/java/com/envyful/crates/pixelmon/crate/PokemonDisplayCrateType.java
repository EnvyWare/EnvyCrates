package com.envyful.crates.pixelmon.crate;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ExtendedConfigItem;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.config.UtilConfigInterface;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.forge.items.ItemFlag;
import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.json.UtilGson;
import com.envyful.api.math.RandomWeightedSet;
import com.envyful.api.reforged.pixelmon.config.SpriteConfig;
import com.envyful.api.reforged.pixelmon.sprite.UtilSprite;
import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.crate.impl.AbstractCrateType;
import com.envyful.crates.type.reward.RewardType;
import com.envyful.crates.type.reward.RewardTypeFactory;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.enchantment.Enchantments;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PokemonDisplayCrateType extends AbstractCrateType {

    private RandomWeightedSet<PokemonSpecRewardType> rewards;
    private ConfigInterface previewGuiSettings;
    private ConfigInterface displayGuiSettings;
    private List<Integer> displaySlots;
    private int spinDuration;
    private int finalRewardPosition;
    private int previewPages;
    private ExtendedConfigItem nextPageItem;
    private ExtendedConfigItem previousPageItem;
    private SpriteConfig spriteConfig;

    public PokemonDisplayCrateType() {
        super();
    }

    @Override
    public void read(JsonElement element) throws CommandSyntaxException {
        super.read(element);

        JsonObject jsonObject = element.getAsJsonObject();

        this.rewards = new RandomWeightedSet<>();

        for (JsonElement jsonElement : jsonObject.getAsJsonArray("rewards")) {
            PokemonSpecRewardType rewardType = (PokemonSpecRewardType) RewardTypeFactory.getInstance("spec");

            if (rewardType == null) {
                continue;
            }

            rewardType.read(jsonElement);
            this.rewards.add(rewardType, rewardType.getWeight());
        }

        this.previewGuiSettings = UtilGson.GSON.fromJson(jsonObject.get("preview_gui_settings"), ConfigInterface.class);
        this.displayGuiSettings = UtilGson.GSON.fromJson(jsonObject.get("display_gui_settings"), ConfigInterface.class);
        this.displaySlots = this.parsePositions(jsonObject.get("display_slots").getAsJsonArray());
        this.spinDuration = jsonObject.get("spin_duration").getAsInt();
        this.finalRewardPosition = jsonObject.get("final_reward_position").getAsInt();
        this.previewPages = jsonObject.get("preview_pages").getAsInt();
        this.nextPageItem = UtilGson.GSON.fromJson(jsonObject.get("preview_next_page"), ExtendedConfigItem.class);
        this.previousPageItem = UtilGson.GSON.fromJson(jsonObject.get("preview_previous_page"), ExtendedConfigItem.class);
        this.spriteConfig = UtilGson.GSON.fromJson(jsonObject.get("sprite_config"), SpriteConfig.class);
    }

    private List<Integer> parsePositions(JsonArray object) {
        List<Integer> positions = Lists.newArrayList();

        for (JsonElement slots : object) {
            positions.add(slots.getAsInt());
        }

        return positions;
    }

    @Override
    public PokemonSpecRewardType generateRandomReward() {
        return this.rewards.getRandom();
    }

    @Override
    public List<RewardType> getAllRewards() {
        return Lists.newArrayList(this.rewards.keySet());
    }

    @Override
    public void preview(ForgeEnvyPlayer player, int page) {
        Pane pane = GuiFactory.paneBuilder()
                .height(this.displayGuiSettings.getHeight())
                .width(9)
                .topLeftX(0)
                .topLeftY(0)
                .build();

        UtilConfigInterface.fillBackground(pane, this.displayGuiSettings);

        for (RewardType allReward : this.getAllRewards()) {
            allReward.display(pane, page);
        }

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> {
                    if (page == this.previewPages) {
                        preview(player, 1);
                    } else {
                        preview(player, page + 1);
                    }
                })
                .extendedConfigItem(player, pane, this.nextPageItem);

        UtilConfigItem.builder()
                .clickHandler((envyPlayer, clickType) -> {
                    if (page == 1) {
                        preview(player, this.previewPages);
                    } else {
                        preview(player, page - 1);
                    }
                })
                .extendedConfigItem(player, pane, this.previousPageItem);

        GuiFactory.guiBuilder()
                .addPane(pane)
                .title(UtilChatColour.colour(this.displayGuiSettings.getTitle()))
                .height(this.displayGuiSettings.getHeight())
                .setPlayerManager(EnvyCrates.getInstance().getPlayerManager())
                .build()
                .open(EnvyCrates.getInstance().getPlayerManager().getPlayer(player.getParent()));
    }

    @Override
    public void open(ForgeEnvyPlayer player) {
        AtomicInteger timer = new AtomicInteger(0);
        PokemonSpecRewardType finalReward = this.generateRandomReward();
        Pokemon finalPoke = PokemonSpecificationProxy.create(finalReward.getSpec()).create();

        Pane pane = GuiFactory.paneBuilder()
                .height(this.displayGuiSettings.getHeight())
                .width(9)
                .topLeftX(0)
                .topLeftY(0)
                .tickHandler(GuiFactory.tickBuilder()
                        .async()
                        .initialDelay(10)
                        .repeatDelay(10)
                        .handler(pane1 -> {
                            timer.incrementAndGet();

                            if (timer.get() >= (2 * this.spinDuration)) {
                                for (Integer spinSlot : this.displaySlots) {
                                    pane1.set(spinSlot % 9, spinSlot / 9, GuiFactory.displayable(UtilConfigItem.fromConfigItem(this.previewGuiSettings.getFillerItems().get(0))));
                                }

                                pane1.set(this.finalRewardPosition % 9, this.finalRewardPosition / 9, GuiFactory.displayable(finalReward.getDisplayItem()));
                                return;
                            }

                            List<Integer> spinSlots = this.displaySlots;

                            for (int i = spinSlots.size() - 1; i > 0; i--) {
                                int slot = spinSlots.get(i);
                                int lastSlot = spinSlots.get(i - 1);

                                pane1.set(slot % 9, slot / 9, pane1.get(lastSlot % 9, lastSlot / 9));
                            }

                            int slot = spinSlots.get(0);

                            if (timer.get() == ((2 * this.spinDuration) - 5)) {
                                pane1.set(slot % 9, slot / 9, GuiFactory.displayable(new ItemBuilder(UtilSprite.getPokemonElement(finalPoke, this.spriteConfig))
                                        .enchant(Enchantments.UNBREAKING, 1)
                                        .itemFlag(ItemFlag.HIDE_ENCHANTS)
                                        .build()));
                            } else {
                                PokemonSpecRewardType pokemonSpecRewardType = this.generateRandomReward();
                                Pokemon poke = PokemonSpecificationProxy.create(pokemonSpecRewardType.getSpec()).create();

                                pane1.set(slot % 9, slot / 9, GuiFactory.displayable(UtilSprite.getPokemonElement(poke, this.spriteConfig)));
                            }
                        })
                        .build())
                .build();

        UtilConfigInterface.fillBackground(pane, this.displayGuiSettings);

        for (Integer spinSlot : this.displaySlots) {
            PokemonSpecRewardType pokemonSpecRewardType = this.generateRandomReward();
            Pokemon poke = PokemonSpecificationProxy.create(pokemonSpecRewardType.getSpec()).create();

            pane.set(spinSlot % 9, spinSlot / 9, GuiFactory.displayable(UtilSprite.getPokemonElement(poke, this.spriteConfig)));
        }

        GuiFactory.guiBuilder()
                .addPane(pane)
                .title(UtilChatColour.colour(this.displayGuiSettings.getTitle()))
                .height(this.displayGuiSettings.getHeight())
                .setPlayerManager(EnvyCrates.getInstance().getPlayerManager())
                .closeConsumer(GuiFactory.closeConsumerBuilder()
                        .async()
                        .handler(envyPlayer -> finalReward.give(player))
                        .build())
                .build()
                .open(EnvyCrates.getInstance().getPlayerManager().getPlayer(player.getParent()));
    }
}
