package com.oxtaly.resourcepackmanager.config;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static java.util.Objects.requireNonNullElse;

public class ConfigData implements Cloneable {
    public static ConfigData DEFAULT = createDefault();

    @SerializedName("config_version_DO_NOT_TOUCH")
    public String configVersion;

    @SerializedName("enable_mod")
    public Boolean enableMod;
    @SerializedName("enable_model_command")
    public Boolean enableModelCommand;

    @SerializedName("send_resource_pack_on_join")
    public Boolean sendResourcePackOnJoin;
    @SerializedName("force_resource_pack")
    public Boolean forceResourcePack;

    @SerializedName("resource_pack_url")
    public String resourcePackURL;
    @SerializedName("resource_pack_prompt")
    public String resourcePackPrompt;

    @SerializedName("resource_pack_uuid")
    public String resourcePackUUIDStr;
    @SerializedName("resource_pack_sha1")
    public String resourcePackSHA1;
    @SerializedName("compute_sha1")
    public Boolean computeResourcePackSHA1FromResourcePack;

    public String computedResourcePackSHA1;
    public UUID resourcePackUUID;

    public static @NotNull ConfigData createDefault() {
        ConfigData configData = new ConfigData();

        configData.configVersion = "1.0.1";
        configData.enableMod = true;
        configData.enableModelCommand = true;
        configData.sendResourcePackOnJoin = true;
        configData.forceResourcePack = false;
        configData.resourcePackURL = "";
        configData.resourcePackPrompt = "<gold>This server makes uses of a resource pack./gold>";
        configData.resourcePackUUIDStr = "";
        configData.resourcePackSHA1 = "";
        configData.computeResourcePackSHA1FromResourcePack = false;

        configData.computedResourcePackSHA1 = null;
        configData.resourcePackUUID = null;

        return configData;
    }

    public void fillMissing() {
        this.configVersion = requireNonNullElse(this.configVersion, DEFAULT.configVersion);
        this.enableMod = requireNonNullElse(this.enableMod, DEFAULT.enableMod);
        this.enableModelCommand = requireNonNullElse(this.enableModelCommand, DEFAULT.enableModelCommand);
        this.sendResourcePackOnJoin = requireNonNullElse(this.sendResourcePackOnJoin, DEFAULT.sendResourcePackOnJoin);
        this.forceResourcePack = requireNonNullElse(this.forceResourcePack, DEFAULT.forceResourcePack);
        this.resourcePackURL = requireNonNullElse(this.resourcePackURL, DEFAULT.resourcePackURL);
        this.resourcePackPrompt = requireNonNullElse(this.resourcePackPrompt, DEFAULT.resourcePackPrompt);
        this.resourcePackUUIDStr = requireNonNullElse(this.resourcePackUUIDStr, DEFAULT.resourcePackUUIDStr);
        this.resourcePackSHA1 = requireNonNullElse(this.resourcePackSHA1, DEFAULT.resourcePackSHA1);
        this.computeResourcePackSHA1FromResourcePack = requireNonNullElse(this.computeResourcePackSHA1FromResourcePack, DEFAULT.computeResourcePackSHA1FromResourcePack);
    }

    public ConfigData clone() {
        try {
            return (ConfigData) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
