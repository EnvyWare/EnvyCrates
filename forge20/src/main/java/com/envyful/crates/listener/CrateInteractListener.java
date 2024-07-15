package com.envyful.crates.listener;

import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.CrateFactory;
import com.envyful.crates.type.crate.CrateType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();

        CrateType crateType = CrateFactory.getCrateType(event.getEntity().level(), event.getHitVec().getBlockPos());

        if (crateType == null) {
            this.handleKeyInteract(event);
            return;
        }

        event.setCanceled(true);
        event.setUseBlock(Event.Result.DENY);

        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        if (itemStack.getCount() < 1 || itemStack.getTag() == null || !itemStack.getTag().contains(EnvyCrates.KEY_NBT_TAG) ||
                !crateType.id().equalsIgnoreCase(itemStack.getTag().get(EnvyCrates.KEY_NBT_TAG).getAsString())) {
            crateType.needAKey(EnvyCrates.getPlayerManager().getPlayer((ServerPlayer) player));
            return;
        }

        if (!player.isCreative()) {
            if (event.getItemStack().getCount() <= 1) {
                event.getEntity().getInventory().removeItem(event.getItemStack());
            } else {
                event.getItemStack().shrink(1);
            }
        }

        crateType.open(EnvyCrates.getPlayerManager().getPlayer((ServerPlayer) player));
    }

    private void handleKeyInteract(PlayerInteractEvent event) {
        if (event.getItemStack().getTag() == null || !event.getItemStack().getTag().contains(EnvyCrates.KEY_NBT_TAG)) {
            return;
        }

        CrateType crateType = CrateFactory.get(event.getItemStack().getTag().get(EnvyCrates.KEY_NBT_TAG).getAsString());
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.FAIL);
        crateType.preview(EnvyCrates.getPlayerManager().getPlayer((ServerPlayer) event.getEntity()), 1);
    }


    @SubscribeEvent
    public void onPlayerLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();

        CrateType crateType = CrateFactory.getCrateType(event.getEntity().level(), event.getPos());

        if (crateType == null || (player.isCreative() && player.isCrouching())) {
            return;
        }

        event.setCanceled(true);
        crateType.preview(EnvyCrates.getPlayerManager().getPlayer((ServerPlayer) player), 1);
    }
}
