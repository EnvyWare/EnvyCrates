package com.envyful.crates.listener;

import com.envyful.crates.type.CrateFactory;
import com.envyful.crates.type.crate.CrateType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CrateBreakListener {

    @SubscribeEvent
    public void onPlayerRightClick(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();

        CrateType crateType = CrateFactory.getCrateType(event.getPos());

        if (crateType == null || !player.isCreative()) {
            return;
        }

        event.setCanceled(true);
        CrateFactory.remove(event.getPos());
        //TODO:
    }
}
