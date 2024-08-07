package com.envyful.crates.listener;

import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.CrateFactory;
import com.envyful.crates.type.crate.CrateType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CrateInteractListener {

    @SubscribeEvent
    public void onPlayerRightClickAir(PlayerInteractEvent.RightClickItem event) {
        this.handleKeyInteract(event);
    }

    @SubscribeEvent
    public void onPlayerRightClickAir(PlayerInteractEvent.EntityInteractSpecific event) {
       this.handleKeyInteract(event);
    }

    @SubscribeEvent
    public void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
        var player = event.getPlayer();
        var itemStack = event.getItemStack();
        var crateType = CrateFactory.getCrateType(event.getPlayer().level, event.getHitVec().getBlockPos());

        if (crateType == null) {
            this.handleKeyInteract(event);
            return;
        }

        event.setCanceled(true);
        event.setUseBlock(Event.Result.DENY);

        if (event.getHand() != Hand.MAIN_HAND) {
            return;
        }

        if (itemStack.getCount() < 1 || itemStack.getTag() == null || !itemStack.getTag().contains(EnvyCrates.KEY_NBT_TAG) ||
                !crateType.id().equalsIgnoreCase(itemStack.getTag().get(EnvyCrates.KEY_NBT_TAG).getAsString())) {
            crateType.needAKey(EnvyCrates.getPlayerManager().getPlayer((ServerPlayerEntity) player));
            return;
        }

        if (!player.isCreative()) {
            if (event.getItemStack().getCount() <= 1) {
                event.getPlayer().inventory.removeItem(event.getItemStack());
            } else {
                event.getItemStack().shrink(1);
            }
        }

        crateType.open(EnvyCrates.getPlayerManager().getPlayer((ServerPlayerEntity) player));
    }

    private void handleKeyInteract(PlayerInteractEvent event) {
        if (event.getItemStack().getTag() == null || !event.getItemStack().getTag().contains(EnvyCrates.KEY_NBT_TAG)) {
            return;
        }

        CrateType crateType = CrateFactory.get(event.getItemStack().getTag().get(EnvyCrates.KEY_NBT_TAG).getAsString());
        event.setCanceled(true);
        event.setCancellationResult(ActionResultType.FAIL);
        crateType.preview(EnvyCrates.getPlayerManager().getPlayer((ServerPlayerEntity) event.getPlayer()), 1);
    }


    @SubscribeEvent
    public void onPlayerLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        var player = event.getPlayer();
        var crateType = CrateFactory.getCrateType(event.getPlayer().level, event.getPos());

        if (crateType == null || (player.isCreative() && player.isCrouching())) {
            return;
        }

        event.setCanceled(true);
        crateType.preview(EnvyCrates.getPlayerManager().getPlayer((ServerPlayerEntity) player), 1);
    }
}
