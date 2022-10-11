package com.envyful.crates.listener;

import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.CrateFactory;
import com.envyful.crates.type.crate.CrateType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CrateInteractListener {

    @SubscribeEvent
    public void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
        PlayerEntity player = event.getPlayer();
        ItemStack itemStack = event.getItemStack();

        CrateType crateType = CrateFactory.getCrateType(event.getHitVec().getBlockPos());

        if (crateType == null) {
            return;
        }

        if (itemStack.getTag() == null || !itemStack.getTag().contains("ENVY_CRATES")) {
            //TODO: message
            return;
        }

        if (!crateType.id().equalsIgnoreCase(itemStack.getTag().get("ENVY_CRATES").getAsString())) {
            //TODO: message
            return;
        }

        event.setCanceled(true);

        if (!player.isCreative()) {
            event.getItemStack().shrink(1);
        }

        crateType.open(EnvyCrates.getInstance().getPlayerManager().getPlayer((ServerPlayerEntity) player));
    }
}
