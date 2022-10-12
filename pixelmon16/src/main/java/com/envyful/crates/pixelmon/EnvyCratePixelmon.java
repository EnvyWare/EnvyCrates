package com.envyful.crates.pixelmon;

import com.envyful.crates.pixelmon.crate.PokemonDisplayCrateType;
import com.envyful.crates.pixelmon.crate.PokemonSpecRewardType;
import com.envyful.crates.type.crate.CrateTypeFactory;
import com.envyful.crates.type.reward.RewardTypeFactory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("envycratespixelmon")
public class EnvyCratePixelmon {

    public EnvyCratePixelmon() {
        MinecraftForge.EVENT_BUS.register(this);

        RewardTypeFactory.register("spec", PokemonSpecRewardType::new);
        CrateTypeFactory.register("spec", PokemonDisplayCrateType::new);
    }
}
