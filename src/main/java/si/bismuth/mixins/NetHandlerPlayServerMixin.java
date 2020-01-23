package si.bismuth.mixins;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import si.bismuth.MCServer;

@Mixin(NetHandlerPlayServer.class)
public abstract class NetHandlerPlayServerMixin {
	@Shadow
	public EntityPlayerMP player;

	@Inject(method = "processCustomPayload", at = @At(value = "TAIL"))
	private void onProcessCustomPayload(CPacketCustomPayload packet, CallbackInfo ci) {
		MCServer.pcm.processIncoming(this.player, packet);
	}

	@Redirect(method = "processPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayerMP;isInvulnerableDimensionChange()Z"))
	private boolean preventPlayerMovedWronglyOrTooQuickly(EntityPlayerMP player) {
		return true;
	}

	@Redirect(method = "processPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetHandlerPlayServer;disconnect(Lnet/minecraft/util/text/ITextComponent;)V"))
	private void debugPlayerBeingKicked(NetHandlerPlayServer handler, ITextComponent component) {
		this.player.connection.setPlayerLocation(this.player.prevPosX, this.player.prevPosY, this.player.prevPosZ, this.player.prevRotationYaw, this.player.prevRotationPitch);
	}
}
