package com.nivek.firstperson.mixin;

import com.nivek.firstperson.FirstPersonBody;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
	private static final String RENDER = "render(Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V";

	@Inject(method = RENDER, at = @At("HEAD"))
	private void nivek$worldRenderStart(CallbackInfo ci) {
		FirstPersonBody.inWorldRender = true;
	}

	@Inject(method = RENDER, at = @At("RETURN"))
	private void nivek$worldRenderEnd(CallbackInfo ci) {
		FirstPersonBody.inWorldRender = false;
	}

	// O jogo pula o proprio jogador em primeira pessoa. Aqui dizemos "terceira pessoa"
	// apenas quando o corpo deve aparecer, assim o jogador e desenhado normalmente.
	@Redirect(method = RENDER, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;isThirdPerson()Z"))
	private boolean nivek$drawOwnBody(Camera camera) {
		return camera.isThirdPerson() || FirstPersonBody.canShowBody(camera.getFocusedEntity());
	}
}
