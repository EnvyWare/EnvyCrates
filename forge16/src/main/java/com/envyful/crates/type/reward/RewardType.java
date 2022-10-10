package com.envyful.crates.type.reward;

import com.envyful.api.forge.player.ForgeEnvyPlayer;
import com.envyful.api.gui.pane.Pane;

public interface RewardType {

    String id();

    void give(ForgeEnvyPlayer player);

    void display(Pane pane);

}
