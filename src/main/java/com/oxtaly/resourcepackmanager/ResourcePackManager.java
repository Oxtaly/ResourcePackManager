package com.oxtaly.resourcepackmanager;

import com.oxtaly.resourcepackmanager.commands.ModCommands;
import com.oxtaly.resourcepackmanager.config.ConfigManager;
import com.oxtaly.resourcepackmanager.utils.Logger;
import com.oxtaly.resourcepackmanager.utils.Utils;
import net.fabricmc.api.ModInitializer;

import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ResourcePackManager implements ModInitializer {

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "resourcepackmanager";
    public static final Logger LOGGER = new Logger(LoggerFactory.getLogger(MOD_ID), "[ResourcePackManager] ");
	public static final ConfigManager CONFIG_MANAGER = new ConfigManager();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		try {
			Utils.innitConfig();
			ModCommands.init();
			Utils.initEvents();
			LOGGER.info("Loaded!");
		} catch (IOException e) {
			LOGGER.error("An error happened trying to load config!", e);
		} catch (Exception e) {
			LOGGER.error("An error happened trying to load the mod!", e);
		}
		// Utils.getResourcePackSHA1();
	}
}