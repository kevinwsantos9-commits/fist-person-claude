package com.nivek.firstperson.mixin;

import com.nivek.firstperson.FirstPersonBody;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
	private static final String RENDER = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V";

	@Inject(method = RENDER, at = @At("HEAD"))
	private void nivek$bodyStart(AbstractClientPlayerEntity player, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (!FirstPersonBody.inWorldRender || !FirstPersonBody.canShowBody(player)) {
			return;
		}
		FirstPersonBody.renderingBody = true;

		float bodyYaw = (float) Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, player.prevBodyYaw, player.bodyYaw));
		float offset = FirstPersonBody.backOffset(player);
		matrices.push();
		matrices.translate(MathHelper.sin(bodyYaw) * offset, 0.0, -MathHelper.cos(bodyYaw) * offset);
	}

	@Inject(method = RENDER, at = @At("RETURN"))
	private void nivek$bodyEnd(AbstractClientPlayerEntity player, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (FirstPersonBody.renderingBody) {
			matrices.pop();
			FirstPersonBody.renderingBody = false;
		}
	}

	// Esconde cabeca e bracos do corpo: a camera fica na cabeca e os bracos em primeira pessoa
	// ja sao desenhados pelo jogo.
	@Inject(method = "setModelPose", at = @At("TAIL"))
	private void nivek$hideParts(AbstractClientPlayerEntity player, CallbackInfo ci) {
		if (!FirstPersonBody.renderingBody) {
			return;
		}
		PlayerEntityModel<AbstractClientPlayerEntity> model = ((PlayerEntityRenderer) (Object) this).getModel();
		model.head.visible = false;
		model.hat.visible = false;
		model.leftArm.visible = false;
		model.rightArm.visible = false;
		model.leftSleeve.visible = false;
		model.rightSleeve.visible = false;
	}
}
