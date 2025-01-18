package com.oxtaly.resourcepackmanager.api;

import com.oxtaly.resourcepackmanager.ResourcePackManager;
import com.oxtaly.resourcepackmanager.config.ConfigData;
import com.oxtaly.resourcepackmanager.utils.Utils;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ResourcePackManagerAPI {

    static ConfigData getConfig() {
        return ResourcePackManager.CONFIG_MANAGER.getData();
    }

    /**
     * @param player - ServerPlayerEntity receiving the resource pack request
     * @return - Weather the players were sent the resource pack packet or not
     */
    static  boolean sendConfigResourcePack(@NotNull ServerPlayerEntity player) {
        return sendConfigResourcePack(player, null);
    }
    /**
     * @param bypassDisableModOption - If it should ignore the config.enableMod option or not
     * @param player - ServerPlayerEntity receiving the resource pack request
     * @return - Weather the players were sent the resource pack packet or not
     */
    static boolean sendConfigResourcePack(@NotNull ServerPlayerEntity player, @Nullable Boolean bypassDisableModOption) {
        ConfigData config = ResourcePackManager.CONFIG_MANAGER.getData();
        if((config.enableMod || (bypassDisableModOption != null && bypassDisableModOption)) && config.resourcePackUUID != null && !config.resourcePackURL.isEmpty()) {
            Text resourcePackPrompt = config.resourcePackPrompt.isEmpty() ? null : Text.of(config.resourcePackPrompt);

            ResourcePackManager.LOGGER.info(String.format("Sending resource pack to %s", player.getUuid().toString()));
            ResourcePackSendS2CPacket packet = new ResourcePackSendS2CPacket(
                    config.resourcePackUUID,
                    config.resourcePackURL,
                    Utils.getResourcePackSHA1(),
                    config.forceResourcePack,
                    Optional.ofNullable(resourcePackPrompt)
            );

            player.networkHandler.sendPacket(packet);
            return true;
        }
        return false;
    }

    /**
     * @param players -  A collection of ServerPlayerEntity receiving the resource pack request
     * @return - Weather the players were sent the resource pack packet or not
     */
    static  boolean sendConfigResourcePack(@NotNull Collection<ServerPlayerEntity> players) {
        return sendConfigResourcePack(players, null);
    }
    /**
     * @param bypassDisableModOption - If it should ignore the config.enableMod option or not
     * @param players - A collection of ServerPlayerEntity receiving the resource pack request
     * @return - Weather the players were sent the resource pack packet or not
     */
    static boolean sendConfigResourcePack(@NotNull Collection<ServerPlayerEntity> players, @Nullable Boolean bypassDisableModOption) {
        ConfigData config = ResourcePackManager.CONFIG_MANAGER.getData();
        if((config.enableMod || (bypassDisableModOption != null && bypassDisableModOption)) && config.resourcePackUUID != null && !config.resourcePackURL.isEmpty()) {
            for (ServerPlayerEntity player : players) {
                Text resourcePackPrompt = config.resourcePackPrompt.isEmpty() ? null : Text.of(config.resourcePackPrompt);

                ResourcePackManager.LOGGER.info(String.format("Sending resource pack to %s", player.getUuid().toString()));
                ResourcePackSendS2CPacket packet = new ResourcePackSendS2CPacket(
                        config.resourcePackUUID,
                        config.resourcePackURL,
                        Utils.getResourcePackSHA1(),
                        config.forceResourcePack,
                        Optional.ofNullable(resourcePackPrompt)
                );

                player.networkHandler.sendPacket(packet);
            }
            return true;
        }
        return false;
    }

    /**
     * @param player - ServerPlayerEntity receiving the resource pack request
     * @param url - URL of the resource pack to send to $player
     * @throws URISyntaxException - If the URL given is invalid, this error is thrown
     * @throws IllegalArgumentException - If the URL or player given is null, this error is thrown
     */
    static void sendResourcePack(@NotNull ServerPlayerEntity player, @NotNull String url) throws URISyntaxException, IllegalArgumentException {
        sendResourcePack(player, url, null, null, null, null, null);
    }
    /**
     * @param player - ServerPlayerEntity receiving the resource pack request
     * @param url - URL of the resource pack to send to $player
     * @param uuid - Null value will be computed from URL string
     * @throws URISyntaxException - If the URL given is invalid, this error is thrown
     * @throws IllegalArgumentException - If the URL or player given is null, this error is thrown
     */
    static void sendResourcePack(@NotNull ServerPlayerEntity player, @NotNull String url, @Nullable UUID uuid) throws URISyntaxException, IllegalArgumentException {
        sendResourcePack(player, url, uuid, null, null, null, null);
    }
    /**
     * @param player - ServerPlayerEntity receiving the resource pack request
     * @param url - URL of the resource pack to send to $player
     * @param uuid - Null value will be computed from URL string
     * @throws URISyntaxException - If the URL given is invalid, this error is thrown
     * @throws IllegalArgumentException - If the URL or player given is null, this error is thrown
     */
    static void sendResourcePack(@NotNull ServerPlayerEntity player, @NotNull String url, @Nullable UUID uuid, @Nullable Text prompt) throws URISyntaxException, IllegalArgumentException {
        sendResourcePack(player, url, uuid,null, prompt, null, null);
    }
    /**
     * @param player - ServerPlayerEntity receiving the resource pack request
     * @param url - URL of the resource pack to send to $player
     * @param uuid - Null value will be computed from URL string
     * @param SHA1 - Null value will result in an empty SHA1, unless config.computeResourcePackSHA1FromResourcePack is set to true
     * @throws URISyntaxException - If the URL given is invalid, this error is thrown
     * @throws IllegalArgumentException - If the URL or player given is null, this error is thrown
     */
    static void sendResourcePack(@NotNull ServerPlayerEntity player, @NotNull String url, @Nullable UUID uuid, @Nullable String SHA1) throws URISyntaxException, IllegalArgumentException {
        sendResourcePack(player, url, uuid,SHA1, null, null, null);
    }
    /**
     * @param player - ServerPlayerEntity receiving the resource pack request
     * @param url - URL of the resource pack to send to $player
     * @param uuid - Null value will be computed from URL string
     * @param SHA1 - Null value will result in an empty SHA1, unless config.computeResourcePackSHA1FromResourcePack is set to true
     * @param prompt - Null value will result in an empty prompt
     * @throws URISyntaxException - If the URL given is invalid, this error is thrown
     * @throws IllegalArgumentException - If the URL or player given is null, this error is thrown
     */
    static void sendResourcePack(@NotNull ServerPlayerEntity player, @NotNull String url, @Nullable UUID uuid, @Nullable String SHA1, @Nullable Text prompt) throws URISyntaxException, IllegalArgumentException {
        sendResourcePack(player, url, uuid, SHA1, prompt, null, null);
    }

    /**
     * @param player - ServerPlayerEntity receiving the resource pack request
     * @param url - URL of the resource pack to send to $player
     * @param uuid - Null value will be computed from URL string
     * @param SHA1 - Null value will result in an empty SHA1, unless computeSHA1 is set to true or config.computeResourcePackSHA1FromResourcePack is set to true and computeSHA1 isn't set
     * @param prompt - Null value will result in an empty prompt
     * @param forced - Null value will use the default config.forceResourcePack
     * @param computeSHA1 - Null value will copy config.computeResourcePackSHA1FromResourcePack (defaults to false)
     * @throws URISyntaxException - If the URL given is invalid, this error is thrown
     * @throws IllegalArgumentException - If the URL or player given is null, this error is thrown
     */
    static void sendResourcePack(@NotNull ServerPlayerEntity player, @NotNull String url, @Nullable UUID uuid, @Nullable String SHA1, @Nullable Text prompt, @Nullable Boolean forced, @Nullable Boolean computeSHA1) throws URISyntaxException, IllegalArgumentException {
        ConfigData config = ResourcePackManager.CONFIG_MANAGER.getData();
        if(url.isEmpty() || player == null)
            throw new IllegalArgumentException("Empty URL!");
        if(!Utils.isValidURL(url))
            throw new URISyntaxException(url, "Invalid URL!");
        if(SHA1 == null) {
            if((computeSHA1 != null && computeSHA1) || (computeSHA1 == null && config.computeResourcePackSHA1FromResourcePack)) {
                try {
                    SHA1 = Utils.computeSHA1FromURLFile(url);
                } catch (Exception e) {
                    SHA1 = "";
                }
            }
            else
                SHA1 = "";
        }
        if(SHA1.length() > 40)
            SHA1 = Utils.formatHash(SHA1);
        if(prompt != null && prompt.getString().isEmpty())
            prompt = null;
        if(forced == null)
            forced = config.forceResourcePack;
        if(uuid == null)
            uuid = Utils.UUIDFromStr(url);

        ResourcePackManager.LOGGER.debug(String.format("Sending resource pack {url=\"%s\",uuid=%s,SHA1=%s,forced=%s,prompt=%s} to %s",
                url,
                uuid,
                SHA1,
                forced,
                prompt != null ? "\"" + prompt.getString() + "\"" : null,
                player.getUuid().toString()
                ));
        ResourcePackSendS2CPacket packet = new ResourcePackSendS2CPacket(
                uuid,
                url,
                SHA1,
                forced,
                Optional.ofNullable(prompt)
        );

        player.networkHandler.sendPacket(packet);
    }

    /**
     * @param players -  A collection of ServerPlayerEntity receiving the resource pack request
     * @param url - URL of the resource pack to send to $players
     * @param uuid - Null value will be computed from URL string
     * @param SHA1 - Null value will result in an empty SHA1, unless computeSHA1 is set to true or config.computeResourcePackSHA1FromResourcePack is set to true and computeSHA1 isn't set
     * @param prompt - Null value will result in an empty prompt
     * @param forced - Null value will use the default config.forceResourcePack
     * @param computeSHA1 - Null value will copy config.computeResourcePackSHA1FromResourcePack (defaults to false)
     * @throws URISyntaxException - If the URL given is invalid, this error is thrown
     * @throws IllegalArgumentException - If the URL or player given is null, this error is thrown
     */
    static boolean sendResourcePack(@NotNull Collection<ServerPlayerEntity> players, @NotNull String url, @Nullable UUID uuid, @Nullable String SHA1, @Nullable Text prompt, @Nullable Boolean forced, @Nullable Boolean computeSHA1) throws URISyntaxException, IllegalArgumentException {
        if(players.isEmpty())
            return false;
        ConfigData config = ResourcePackManager.CONFIG_MANAGER.getData();
        if(url.isEmpty())
            throw new IllegalArgumentException("Empty URL!");
        if(!Utils.isValidURL(url))
            throw new URISyntaxException(url, "Invalid URL!");
        if(SHA1 == null) {
            if((computeSHA1 != null && computeSHA1) || (computeSHA1 == null && config.computeResourcePackSHA1FromResourcePack)) {
                try {
                    SHA1 = Utils.computeSHA1FromURLFile(url);
                } catch (Exception e) {
                    SHA1 = "";
                }
            }
            else
                SHA1 = "";
        }
        if(SHA1.length() > 40)
            SHA1 = Utils.formatHash(SHA1);
        if(prompt != null && prompt.getString().isEmpty())
            prompt = null;
        if(forced == null)
            forced = config.forceResourcePack;
        if(uuid == null)
            uuid = Utils.UUIDFromStr(url);

        ResourcePackManager.LOGGER.debug(String.format("Sending resource pack {url=\"%s\",uuid=%s,SHA1=%s,forced=%s,prompt=%s} to %s players",
                url,
                uuid,
                SHA1,
                forced,
                prompt != null ? "\"" + prompt.getString() + "\"" : null,
                players.size()
        ));
        for (ServerPlayerEntity player : players) {
            ResourcePackManager.LOGGER.debug(String.format("Sending resource pack to %s", player.getUuid().toString()));
            ResourcePackSendS2CPacket packet = new ResourcePackSendS2CPacket(
                    uuid,
                    url,
                    SHA1,
                    forced,
                    Optional.ofNullable(prompt)
            );

            player.networkHandler.sendPacket(packet);
        }
        return true;
    }
}
