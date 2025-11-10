package catgirlemily.peeker.mixin;

import catgirlemily.peeker.PacketPrinter;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonNetworkHandler.class)
public class MixinClientCommonNetworkHandler {

    @Shadow
    protected ClientConnection connection;

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = false)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if (connection == null) return;

        PacketPrinter.printPacket(packet);
    }
}
