package com.envyful.crates.listener;

import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.CrateFactory;
import com.envyful.crates.type.crate.CrateType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CrateInteractListener {

    @SubscribeEvent
    public void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
        PlayerEntity player = event.getPlayer();
        ItemStack itemStack = event.getItemStack();

        CrateType crateType = CrateFactory.getCrateType(event.getPlayer().level, event.getHitVec().getBlockPos());

        if (crateType == null) {
            return;
        }

        event.setCanceled(true);
        event.setUseBlock(Event.Result.DENY);

        if (event.getHand() != Hand.MAIN_HAND) {
            return;
        }

        if (itemStack.getTag() == null || !itemStack.getTag().contains("ENVY_CRATES") ||
                !crateType.id().equalsIgnoreCase(itemStack.getTag().get("ENVY_CRATES").getAsString())) {
            crateType.needAKey(EnvyCrates.getInstance().getPlayerManager().getPlayer((ServerPlayerEntity) player));
            return;
        }

        if (!player.isCreative()) {
            event.getItemStack().shrink(1);
        }

        crateType.open(EnvyCrates.getInstance().getPlayerManager().getPlayer((ServerPlayerEntity) player));
    }

    @SubscribeEvent
    public void onPlayerLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        PlayerEntity player = event.getPlayer();

        CrateType crateType = CrateFactory.getCrateType(event.getPlayer().level, event.getPos());

        if (crateType == null || (player.isCreative() && player.isCrouching())) {
            return;
        }

        event.setCanceled(true);
        crateType.preview(EnvyCrates.getInstance().getPlayerManager().getPlayer((ServerPlayerEntity) player), 1);
    }
}
