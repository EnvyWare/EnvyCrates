package com.envyful.crates.type.crate.impl;

import com.envyful.api.config.type.ConfigInterface;
import com.envyful.api.config.type.ConfigItem;
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
import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.reward.RewardType;
import com.envyful.crates.type.reward.RewardTypeFactory;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleItemCrate extends AbstractCrateType {

    private RandomWeightedSet<RewardType> rewards;
    private ConfigInterface previewGuiSettings;
    private ConfigInterface displayGuiSettings;
    private List<Integer> displaySlots;
    private int spinDuration;
    private int finalRewardPosition;
    private int previewPages;
    private ExtendedConfigItem nextPageItem;
    private ExtendedConfigItem previousPageItem;
    private String sound;

    public SimpleItemCrate() {
        super();
    }

    @Override
    public void read(JsonElement element) throws CommandSyntaxException {
        super.read(element);

        JsonObject jsonObject = element.getAsJsonObject();

        this.rewards = new RandomWeightedSet<>();

        for (JsonElement jsonElement : jsonObject.getAsJsonArray("rewards")) {
            JsonObject rewardObject = jsonElement.getAsJsonObject();
            String type = rewardObject.get("type").getAsString();
            RewardType rewardType = RewardTypeFactory.getInstance(type);

            if (rewardType == null) {
                EnvyCrates.getLogger().error("Invalid reward type `{}` in `{}`", type, this.id);
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

        if (jsonObject.has("sound")) {
            this.sound = jsonObject.get("sound").getAsString();
        }
    }

    private List<Integer> parsePositions(JsonArray object) {
        List<Integer> positions = Lists.newArrayList();

        for (JsonElement slots : object) {
            positions.add(slots.getAsInt());
        }

        return positions;
    }

    @Override
    public RewardType generateRandomReward() {
        return this.rewards.getRandom();
    }

    @Override
    public List<RewardType> getAllRewards() {
        return Lists.newArrayList(this.rewards.keySet());
    }

    @Override
    public void preview(ForgeEnvyPlayer player, int page) {
        Pane pane = GuiFactory.paneBuilder()
                .height(this.previewGuiSettings.getHeight())
                .width(9)
                .topLeftX(0)
                .topLeftY(0)
                .build();

        UtilConfigInterface.fillBackground(pane, this.previewGuiSettings);

        for (RewardType allReward : this.getAllRewards()) {
            allReward.display(pane, page);
        }

        if (page != this.previewPages) {
            UtilConfigItem.builder()
                    .clickHandler((envyPlayer, clickType) -> {
                        if (page == this.previewPages) {
                            preview(player, 1);
                        } else {
                            preview(player, page + 1);
                        }
                    })
                    .extendedConfigItem(player, pane, this.nextPageItem);
        }

        if (page != 1) {
            UtilConfigItem.builder()
                    .clickHandler((envyPlayer, clickType) -> {
                        if (page == 1) {
                            preview(player, this.previewPages);
                        } else {
                            preview(player, page - 1);
                        }
                    })
                    .extendedConfigItem(player, pane, this.previousPageItem);
        }

        GuiFactory.guiBuilder()
                .addPane(pane)
                .title(UtilChatColour.colour(this.previewGuiSettings.getTitle()))
                .height(this.previewGuiSettings.getHeight())
                .setPlayerManager(EnvyCrates.getPlayerManager())
                .build()
                .open(EnvyCrates.getPlayerManager().getPlayer(player.getParent()));
    }

    @Override
    public void open(ForgeEnvyPlayer player) {
        AtomicInteger timer = new AtomicInteger(0);
        RewardType finalReward = this.generateRandomReward();
        AtomicBoolean cleared = new AtomicBoolean(false);

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
                                finalReward.playSound(player.getParent());

                                if (!cleared.get()) {
                                    cleared.set(true);
                                    int counter = 0;
                                    for (ConfigItem fillerItem : this.displayGuiSettings.getFillerItems()) {
                                        if (!fillerItem.isEnabled() || counter == this.finalRewardPosition) {
                                            ++counter;
                                            continue;
                                        }

                                        pane1.set(counter % 9, counter / 9, GuiFactory.displayable(UtilConfigItem.fromConfigItem(fillerItem)));
                                        ++counter;
                                    }
                                }

                                pane1.set(this.finalRewardPosition % 9, this.finalRewardPosition / 9, GuiFactory.displayable(new ItemBuilder(finalReward.getDisplayItem())
                                        .enchant(Enchantments.UNBREAKING, 1)
                                        .itemFlag(ItemFlag.HIDE_ENCHANTS)
                                        .build()));
                                return;
                            }

                            if (this.sound != null) {
                                player.getParent().connection.send(new ClientboundSoundPacket(ForgeRegistries.SOUND_EVENTS.getDelegateOrThrow(ResourceLocation.tryParse(this.sound)), SoundSource.MUSIC,
                                        player.getParent().getX(), player.getParent().getY(), player.getParent().getZ(), 1.0f, 1.0f, 0L));
                            }

                            List<Integer> spinSlots = this.displaySlots;

                            for (int i = spinSlots.size() - 1; i > 0; i--) {
                                int slot = spinSlots.get(i);
                                int lastSlot = spinSlots.get(i - 1);

                                pane1.set(slot % 9, slot / 9, pane1.get(lastSlot % 9, lastSlot / 9));
                            }

                            int slot = spinSlots.get(0);

                            int subtraction = this.displaySlots.indexOf(this.finalRewardPosition);

                            if (subtraction == -1) {
                                subtraction = 5;
                            } else {
                                subtraction = this.displaySlots.size() - subtraction;
                            }

                            if (timer.get() == ((2 * this.spinDuration) - subtraction)) {
                                pane1.set(slot % 9, slot / 9, GuiFactory.displayable(new ItemBuilder(finalReward.getDisplayItem())
                                        .enchant(Enchantments.UNBREAKING, 1)
                                        .itemFlag(ItemFlag.HIDE_ENCHANTS)
                                        .build()));
                            } else {
                                pane1.set(slot % 9, slot / 9, GuiFactory.displayable(this.generateRandomReward().getDisplayItem()));
                            }
                        })
                        .build())
                .build();

        UtilConfigInterface.fillBackground(pane, this.displayGuiSettings);

        for (Integer spinSlot : this.displaySlots) {
            pane.set(spinSlot % 9, spinSlot / 9, GuiFactory.displayable(this.generateRandomReward().getDisplayItem()));
        }

        GuiFactory.guiBuilder()
                .addPane(pane)
                .title(UtilChatColour.colour(this.displayGuiSettings.getTitle()))
                .height(this.displayGuiSettings.getHeight())
                .setPlayerManager(EnvyCrates.getPlayerManager())
                .closeConsumer(GuiFactory.closeConsumerBuilder()
                        .async()
                        .handler(envyPlayer -> finalReward.give(player))
                        .build())
                .build()
                .open(EnvyCrates.getPlayerManager().getPlayer(player.getParent()));
    }
}
