package com.envyful.crates.listener;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.CrateFactory;
import com.envyful.crates.type.crate.CrateType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CrateBreakListener {

    @SubscribeEvent
    public void onPlayerRightClick(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();

        CrateType crateType = CrateFactory.getCrateType(event.getPlayer().level, event.getPos());

        if (crateType == null || !player.isCreative() || !player.isCrouching()) {
            return;
        }

        event.setCanceled(true);
        CrateFactory.remove(event.getPlayer().level, event.getPos());

        for (String s : EnvyCrates.getInstance().getLocale().getCrateRemoved()) {
            player.sendMessage(UtilChatColour.colour(s
                    .replace("%pos%", event.getPos().getX() + "," + event.getPos().getY() + "," + event.getPos().getZ())
                    .replace("%crate%", crateType.id())
            ), Util.NIL_UUID);
        }
    }
}
