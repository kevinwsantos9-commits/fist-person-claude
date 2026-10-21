package com.nivek.firstperson;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;

/** Estado e regras para desenhar o corpo do proprio jogador em primeira pessoa. */
public final class FirstPersonBody {
	/** Verdadeiro apenas enquanto o WorldRenderer desenha o mundo (nao vale para a tela de inventario). */
	public static boolean inWorldRender;

	/** Verdadeiro apenas enquanto o corpo do jogador esta sendo desenhado em primeira pessoa. */
	public static boolean renderingBody;

	private static final float BACK_OFFSET = 0.25f;
	private static final float BACK_OFFSET_CROUCH = 0.33f;

	private FirstPersonBody() {
	}

	/** O corpo so aparece em pe ou agachado, sem veiculo, nao invisivel e fora do modo espectador. */
	public static boolean canShowBody(Entity entity) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (!(entity instanceof ClientPlayerEntity player) || entity != client.player) {
			return false;
		}
		Camera camera = client.gameRenderer.getCamera();
		if (camera.isThirdPerson() || camera.getFocusedEntity() != entity) {
			return false;
		}
		if (player.isSpectator() || player.isInvisible() || player.hasVehicle()) {
			return false;
		}
		EntityPose pose = player.getPose();
		return pose == EntityPose.STANDING || pose == EntityPose.CROUCHING;
	}

	/** Quanto o corpo e empurrado para tras da camera, para a camera nao ficar dentro do tronco. */
	public static float backOffset(Entity entity) {
		return entity.isInPose(EntityPose.CROUCHING) ? BACK_OFFSET_CROUCH : BACK_OFFSET;
	}
}
