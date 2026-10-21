package com.nivek.firstperson.mixin;

import com.nivek.firstperson.FirstPersonBody;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin {
	// Mantem calca e botas, mas esconde capacete e armadura dos bracos.
	@Inject(method = "setVisible", at = @At("TAIL"))
	private void nivek$hideHelmetAndArms(BipedEntityModel<?> model, EquipmentSlot slot, CallbackInfo ci) {
		if (!FirstPersonBody.renderingBody) {
			return;
		}
		model.head.visible = false;
		model.hat.visible = false;
		model.leftArm.visible = false;
		model.rightArm.visible = false;
	}
}
