package com.oxtaly.resourcepackmanager.mixin;

import com.oxtaly.resourcepackmanager.ResourcePackManager;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResourcePackSendS2CPacket.class)
public class ResourcePackSendS2CPacketMixin {

    @Inject(
            at = @At("RETURN"),
            method = "<init>(Ljava/util/UUID;Ljava/lang/String;Ljava/lang/String;ZLjava/util/Optional;)V"
    )
    public void onCreate(CallbackInfo ci) {
        ResourcePackSendS2CPacket packet = (ResourcePackSendS2CPacket) (Object) this;
        ResourcePackManager.LOGGER.info(String.format("%s", packet));
    }
}
