package com.oxtaly.resourcepackmanager.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.Strictness;
import com.oxtaly.resourcepackmanager.ResourcePackManager;
import com.oxtaly.resourcepackmanager.utils.Logger;
import com.oxtaly.resourcepackmanager.utils.Utils;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

public class ConfigManager {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setStrictness(Strictness.LENIENT).create();
    public ConfigData data = ConfigData.DEFAULT;

    public ConfigManager() {}
    public ConfigManager(ConfigData data) {
        this.data = data;
    }

    public int readConfig(File file) throws IOException {
        FileReader reader = new FileReader(file);

        ConfigData configData = GSON.fromJson(reader, ConfigData.class);

        reader.close();

        configData.fillMissing();

        Logger logger = ResourcePackManager.LOGGER;
        String reloadConfigText = " and reload the config with /resourcepackmanager reload.";

        int errorCount = 0;
        if(configData.enableMod && configData.resourcePackURL.isEmpty()) {
            logger.warn("Empty resource_pack_url");
        }
        if(!configData.resourcePackURL.isEmpty() && !Utils.isValidURL(configData.resourcePackURL)) {
            logger.error(String.format("Invalid resource_pack_url value: '%s'! Please provide a valid url" + reloadConfigText, configData.resourcePackUUIDStr));
            configData.resourcePackURL = "";
            errorCount += 1;
        }
        if(!configData.resourcePackUUIDStr.isEmpty()) {
            try {
                configData.resourcePackUUID = UUID.fromString(configData.resourcePackUUIDStr);
                logger.debug(String.format("Loaded pack UUID successfully: %s", configData.resourcePackUUID));
            } catch (IllegalArgumentException e) {
                logger.error(String.format("Invalid resource_pack_uuid value: '%s'! Please provide a valid UUID or leave empty" + reloadConfigText, configData.resourcePackUUIDStr));
                configData.resourcePackUUID = null;
                errorCount += 1;
            }
        } else if(!configData.resourcePackURL.isEmpty()) {
            configData.resourcePackUUID = Utils.UUIDFromStr(configData.resourcePackURL);
            logger.warn(String.format("No UUID provided, using url as base for UUID: %s", configData.resourcePackUUID));
        }
        if(!configData.computeResourcePackSHA1FromResourcePack && configData.resourcePackSHA1.length() > 40) {
            logger.warn(String.format("resource_pack_sha1 value ('%s') exceeded maximum length allowed (40), shortening.", configData.resourcePackSHA1.length()));
            configData.resourcePackSHA1 = Utils.formatHash(configData.resourcePackSHA1);
            errorCount += 1;
        }
        // no need to recheck if the url is valid, if it's invalid it'll already be set to empty
        if(configData.computeResourcePackSHA1FromResourcePack && !configData.resourcePackURL.isEmpty()) {
            try {
                configData.computedResourcePackSHA1 = Utils.computeSHA1FromURLFile(configData.resourcePackURL);
                logger.debug(String.format("Computed SHA1 successfully: %s", configData.computedResourcePackSHA1));
            } catch (Exception e) {
                String fallback = configData.resourcePackSHA1.isEmpty() ? "resource_pack_sha1 value" : "SHA1 of the URL (not the file)";
                logger.error("An error happened trying to compute SHA1 for resource pack! Is your URL a valid file? SHA1 will fallback to " + fallback + ".", e);
                configData.computedResourcePackSHA1 = null;
                errorCount += 1;
            }
        }
        if(!configData.resourcePackURL.isEmpty() && configData.computedResourcePackSHA1 == null && configData.resourcePackSHA1.isEmpty())
            logger.warn("No resource_pack_sha1 value and compute_sha1 is turned off, SHA1 will be left empty and players won't get a new pack with the same url!");
        logger.debug("Loaded config!");
        this.setData(configData);
        return errorCount;
    }

    public void writeConfig(File file) throws JsonIOException, IOException {
        FileWriter writer = new FileWriter(file);
        GSON.toJson(data, writer);
        writer.close();
    }

    public void setData(ConfigData data) {
        this.data = data;
    }

    public ConfigData getData() {
        return this.data;
    }
}
