package com.oxtaly.resourcepackmanager.events;

import com.oxtaly.resourcepackmanager.ResourcePackManager;
import com.oxtaly.resourcepackmanager.api.ResourcePackManagerAPI;
import com.oxtaly.resourcepackmanager.config.ConfigData;
import com.oxtaly.resourcepackmanager.utils.Utils;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerJoinEvent {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register(PlayerJoinEvent::execute);
    }

    // 1 = success & sent pack
    // -1 = success but no pack sent
    // 0 = failure
    public static int execute(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.getPlayer();
        ConfigData config = ResourcePackManager.CONFIG_MANAGER.getData();
        if(config.enableMod && config.sendResourcePackOnJoin) {
            try {
                if(Utils.isValidURL(config.resourcePackURL) && config.resourcePackUUID != null) {
                    ResourcePackManagerAPI.sendConfigResourcePack(player);

                    // .addTask(new SendResourcePackTask(new MinecraftServer.ServerResourcePackProperties(
                    //                 config.resourcePackUUID,
                    //                 config.resourcePackURL,
                    //                 Utils.getResourcePackSHA1(),
                    //                 config.forceResourcePack,
                    //                 Text.of(config.resourcePackPrompt)
                    // )));

                    // handler.send(new ResourcePackSendS2CPacket(
                    //         config.resourcePackUUID,
                    //         config.resourcePackURL,
                    //         Utils.getResourcePackSHA1(),
                    //         config.forceResourcePack,
                    //         Optional.ofNullable(resourcePackPrompt)
                    // ));
                    // player.sendResourcePackURL(resourcePackURL, resourcePackHash, config.forceResourcePack, null);
                    return 1;
                }
                return -1;
            } catch (Exception e) {
                ResourcePackManager.LOGGER.error("An error happened trying to send resource pack!", e);
                return 0;
            }
        }
        return -1;
    }
}
