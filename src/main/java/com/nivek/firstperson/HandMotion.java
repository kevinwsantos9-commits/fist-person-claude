package com.nivek.firstperson;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

/** Movimento extra e suave das maos ao andar, correr e pular. */
public final class HandMotion {
	private static float walk;
	private static float sprint;
	private static float air;
	private static long lastNanos;

	private HandMotion() {
	}

	public static void apply(MatrixStack matrices, ClientPlayerEntity player, float tickDelta) {
		MinecraftClient client = MinecraftClient.getInstance();

		long now = System.nanoTime();
		float dt = lastNanos == 0L ? 0f : Math.min((now - lastNanos) / 1.0e9f, 0.1f);
		lastNanos = now;

		boolean onGround = player.isOnGround();
		boolean bobbing = client.options.getBobView().getValue();

		float speed = Math.min(player.limbAnimator.getSpeed(tickDelta), 1f);
		float walkTarget = onGround && bobbing ? speed : 0f;
		float sprintTarget = player.isSprinting() ? 1f : 0f;
		// subindo: a mao desce um pouco; caindo: a mao sobe um pouco
		float airTarget = onGround ? 0f : MathHelper.clamp((float) -player.getVelocity().y * 0.15f, -0.07f, 0.07f);

		walk += (walkTarget - walk) * smoothing(dt, 10f);
		sprint += (sprintTarget - sprint) * smoothing(dt, 8f);
		air += (airTarget - air) * smoothing(dt, 10f);

		float phase = player.limbAnimator.getPos(tickDelta) * 0.6662f;
		float strength = walk * (1f + 0.6f * sprint);

		float x = MathHelper.sin(phase) * 0.012f * strength;
		float y = -Math.abs(MathHelper.cos(phase)) * 0.018f * strength + air;
		float roll = MathHelper.sin(phase) * 1.2f * strength;
		float tilt = 3f * sprint;

		matrices.translate(x, y, 0f);
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(roll));
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(tilt));
	}

	private static float smoothing(float dt, float rate) {
		return 1f - (float) Math.exp(-rate * dt);
	}
}
