package com.oxtaly.resourcepackmanager.mixin;

import com.oxtaly.resourcepackmanager.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ServerPropertiesHandler.class)
public class ServerPropertiesHandlerMixin {
    @Inject(
            at = @At("HEAD"),
            method = "getServerResourcePackProperties(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;)Ljava/util/Optional;",
            cancellable = true
    )
    private static void ResourcePackManager$IgnoreServerPropertiesResourcePack(CallbackInfoReturnable<Optional<MinecraftServer.ServerResourcePackProperties>> ci) {
        ResourcePackManager.LOGGER.debug("Removed resource pack server properties");
        ci.setReturnValue(Optional.empty());
    }
}
