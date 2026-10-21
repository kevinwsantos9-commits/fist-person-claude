package com.nivek.firstperson.mixin;

import com.nivek.firstperson.FirstPersonBody;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HeldItemFeatureRenderer.class, HeadFeatureRenderer.class})
public abstract class HideBodyFeaturesMixin {
	// Nao desenha o item na mao do corpo (a mao de primeira pessoa ja mostra o item)
	// nem itens na cabeca (abobora, cabecas de mob), que ficariam na frente da camera.
	@Inject(
		method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
		at = @At("HEAD"),
		cancellable = true
	)
	private void nivek$skip(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity,
			float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch,
			CallbackInfo ci) {
		if (FirstPersonBody.renderingBody) {
			ci.cancel();
		}
	}
}
