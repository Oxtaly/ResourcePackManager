package com.oxtaly.resourcepackmanager.utils;

import com.oxtaly.resourcepackmanager.ResourcePackManager;
import com.oxtaly.resourcepackmanager.config.ConfigData;
import com.oxtaly.resourcepackmanager.config.ConfigManager;
import com.oxtaly.resourcepackmanager.events.PlayerJoinEvent;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public class Utils {

    public static @NotNull String getConfigPath() {
        return FabricLoader.getInstance().getConfigDir() + "/ResourcePackManager.json";
    }

    public static class minecraftLogBuilder {
        public static @NotNull MutableText log(String string) {
            return minecraftLogBuilder.log(Text.literal(string));
        };
        public static @NotNull MutableText log(MutableText mutableText) {
            MutableText logString = Text.empty()
                    .append(Text.literal("[").formatted(Formatting.DARK_GRAY))
                    .append(Text.literal("RPM").setStyle(Style.EMPTY.withColor(TextColor.parse("#78cce3").getOrThrow())
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("ResourcePackManager")))))
                    .append(Text.literal("] ").formatted(Formatting.DARK_GRAY));
            logString.append(mutableText);
            return logString;
        };
        public static @NotNull Text warn(String string) {
            return minecraftLogBuilder.warn(Text.literal(string));
        };
        public static @NotNull MutableText warn(MutableText mutableText) {
            return minecraftLogBuilder.log(mutableText).setStyle(Style.EMPTY.withColor(TextColor.parse("#ffc107").getOrThrow()));
        };
        public static @NotNull MutableText error(String string) {
            return minecraftLogBuilder.error(Text.literal(string));
        };
        public static @NotNull MutableText error(MutableText mutableText) {
            return minecraftLogBuilder.log(mutableText).formatted(Formatting.RED);
        };
    }

    public static int innitConfig() throws IOException {
        ConfigManager config = ResourcePackManager.CONFIG_MANAGER;
        File configFile = new File(getConfigPath());
        if(!configFile.exists()) {
            config.writeConfig(configFile);
            return -1;
        }
        return config.readConfig(configFile);
    }

    public static void initEvents() {
        PlayerJoinEvent.register();
    }

    public static void saveConfig() throws IOException {
        ConfigManager config = ResourcePackManager.CONFIG_MANAGER;
        File configFile = new File(getConfigPath());
        config.writeConfig(configFile);
    }

    //from: https://www.baeldung.com/java-validate-url#:~:text=Validate%20URL%20Using%20JDK&text=toURI()%3B%20tries%20to%20create,in%20the%20input%20String%20object.
    public static boolean isValidURL(String url) {
        if(url == null) return false;
        if(Objects.equals(url, "")) return false;
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    public static @NotNull String formatHash(@NotNull String hash) {
        if(hash.length() > 40) return hash.substring(0, 40);
        return hash;
    }

    public static @NotNull UUID UUIDFromStr(@NotNull String str) {
        return UUID.nameUUIDFromBytes(str.getBytes(StandardCharsets.UTF_8));
    }

    public static @NotNull String computeSHA1FromURLFile(@NotNull String url) throws IOException, MalformedURLException {
        return DigestUtils.sha1Hex(new URL(url).openStream());
    }

    public static String getResourcePackSHA1() {
        ConfigData config = ResourcePackManager.CONFIG_MANAGER.getData();
        if(config.computeResourcePackSHA1FromResourcePack && !config.computedResourcePackSHA1.isEmpty())
            return config.computedResourcePackSHA1;
        if(!config.resourcePackSHA1.isEmpty())
            return Utils.formatHash(config.resourcePackSHA1);
        else return "";
    }
}
