package com.envyful.crates.listener;

import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.text.Placeholder;
import com.envyful.crates.EnvyCrates;
import com.envyful.crates.type.CrateFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CrateBreakListener {

    @SubscribeEvent
    public void onPlayerRightClick(BlockEvent.BreakEvent event) {
        var player = event.getPlayer();
        var crateType = CrateFactory.getCrateType(event.getPlayer().level, event.getPos());

        if (crateType == null || !player.isCreative() || !player.isCrouching()) {
            return;
        }

        event.setCanceled(true);
        CrateFactory.remove(event.getPlayer().level, event.getPos());

        PlatformProxy.sendMessage(player, EnvyCrates.getLocale().getCrateRemoved(),
                Placeholder.simple("%pos%", event.getPos().getX() + "," + event.getPos().getY() + "," + event.getPos().getZ()),
                Placeholder.simple("%crate%", crateType.id()));
    }
}
