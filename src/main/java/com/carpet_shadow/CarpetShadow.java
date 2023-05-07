package com.carpet_shadow;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.settings.SettingsManager;
import com.carpet_shadow.utility.RandomString;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class CarpetShadow implements CarpetExtension, ModInitializer {
    public static final HashMap<String, WeakReference<ItemStack>> shadowMap = new HashMap<>();
    public static final Logger LOGGER = LogManager.getLogger("carpet-shadow");
    public static RandomString shadow_id_generator = new RandomString(CarpetShadowSettings.shadowItemIdSize);

    public static SettingsManager settingsManager = new SettingsManager("0.0.1","carpet-shadow","Carpet Shadow");

    @Override
    public void onGameStarted() {
        CarpetShadow.LOGGER.info("Carpet Shadow Loaded!");
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER){
            settingsManager.parseSettingsClass(CarpetShadowServerSettings.class);
        }
        settingsManager.parseSettingsClass(CarpetShadowSettings.class);
        shadow_id_generator = new RandomString(CarpetShadowSettings.shadowItemIdSize);
    }

    @Override
    public void onInitialize() {
        CarpetServer.manageExtension(new CarpetShadow());
        CarpetShadow.LOGGER.info("Carpet Shadow Loading!");
        ServerLifecycleEvents.SERVER_STOPPED.register((server -> shadowMap.clear()));
    }


    @Override
    public SettingsManager customSettingsManager() {
        return settingsManager;
    }
}
