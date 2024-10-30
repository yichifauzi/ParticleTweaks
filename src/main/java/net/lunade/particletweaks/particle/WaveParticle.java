/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of Wilder Wild.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.lunade.particletweaks.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class WaveParticle extends TextureSheetParticle {
	private final SpriteSet sprites;
	private final float width;
	private final float strength;

	WaveParticle(@NotNull ClientLevel level, @NotNull SpriteSet spriteProvider, double x, double y, double z, float width, float strength) {
		super(level, x, y + 1D - (0.0625D * 2.5D), z, 0D, 0D, 0D); // Places half a pixel down from Y
		this.setSize(width, 1F);
		this.setSpriteFromAge(spriteProvider);
		this.quadSize = 0.75F; // 12 / 16
		this.sprites = spriteProvider;
		this.width = width;
		this.strength = strength;
		this.lifetime = 13;
		this.quadSize *= 1.25F;
	}

	@Override
	public void tick() {
		if (this.age++ >= this.lifetime) {
			this.remove();
		}

		this.setSpriteFromAge(this.sprites);

		Minecraft minecraft = Minecraft.getInstance();
		Vector3f leftVector = minecraft.gameRenderer.getMainCamera().getLeftVector();
		leftVector = new Vector3f(leftVector.x(), 0F, leftVector.z()).normalize();

		double sin = Math.sin((this.age * Math.PI) / 19D);
		this.xd = sin * (0.015D * leftVector.x());
		this.zd = sin * (0.015D * leftVector.z());
	}

	@Override
	public void render(VertexConsumer buffer, @NotNull Camera camera, float partialTicks) {
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			float yRot = direction.toYRot() * Mth.DEG_TO_RAD;

			float width = this.getWidth(partialTicks);
			float height = this.getHeight(partialTicks);

			this.renderParticle(buffer, camera, partialTicks, direction, width, height, false, transforms -> transforms.rotateY(yRot));
			this.renderParticle(buffer, camera, partialTicks, direction, width, height, true, transforms -> transforms.rotateY((float) -Math.PI + yRot));
		}
	}

	private static final Vector3f NORMALIZED_QUAT_VECTOR = new Vector3f(0.5F, 0.5F, 0.5F).normalize();

	private void renderParticle(
		VertexConsumer buffer,
		@NotNull Camera renderInfo,
		float partialTicks,
		@NotNull Direction direction,
		float width,
		float height,
		boolean flipped,
		@NotNull Consumer<Quaternionf> quaternionConsumer
	) {
		float halfWidth = width * 0.5F;
		Vec3 vec3 = renderInfo.getPosition();
		float f = (float)(Mth.lerp(partialTicks, this.xo, this.x) - vec3.x()) - (direction.getStepX() * halfWidth);
		float g = (float)(Mth.lerp(partialTicks, this.yo, this.y) - vec3.y()) - 1F;
		float h = (float)(Mth.lerp(partialTicks, this.zo, this.z) - vec3.z()) - (direction.getStepZ() * halfWidth);
		Quaternionf quaternionf = new Quaternionf().setAngleAxis(0F, NORMALIZED_QUAT_VECTOR.x(), NORMALIZED_QUAT_VECTOR.y(), NORMALIZED_QUAT_VECTOR.z());
		quaternionConsumer.accept(quaternionf);
		Vector3f[] vector3fs = new Vector3f[]{
			new Vector3f(-halfWidth * 1.07F, 0F, 0F),
			new Vector3f(-halfWidth * 1.07F, 2F * height, 0F),
			new Vector3f(halfWidth * 1.07F, 2F * height, 0F),
			new Vector3f(halfWidth * 1.07F, 0F, 0F)
		};
		float i = this.getQuadSize(partialTicks);

		for (int j = 0; j < 4; ++j) {
			Vector3f vector3f2 = vector3fs[j];
			vector3f2.rotate(quaternionf);
			vector3f2.mul(i);
			vector3f2.add(f, g, h);
		}

		float k = !flipped ? this.getU0() : this.getU1();
		float l = !flipped ? this.getU1() : this.getU0();
		float m = this.getV0();
		float n = this.getV1();
		int light = this.getLightColor(partialTicks);
		buffer.addVertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z())
			.setUv(l, n)
			.setColor(this.rCol, this.gCol, this.bCol, this.alpha)
			.setLight(light);
		buffer.addVertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z())
			.setUv(l, m)
			.setColor(this.rCol, this.gCol, this.bCol, this.alpha)
			.setLight(light);
		buffer.addVertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z())
			.setUv(k, m)
			.setColor(this.rCol, this.gCol, this.bCol, this.alpha)
			.setLight(light);
		buffer.addVertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z())
			.setUv(k, n)
			.setColor(this.rCol, this.gCol, this.bCol, this.alpha)
			.setLight(light);
	}

	public float getWidth(float tickDelta) {
		return this.width + (((this.age + tickDelta) / (float)this.lifetime) * this.strength * 0.75F);
	}

	public float getHeight(float tickDelta) {
		float heightProgress = (float) Math.sin(((this.age + tickDelta) * Math.PI) / this.lifetime);
		return 1F + (heightProgress * this.strength * 0.1F);
	}

	@Override
	@NotNull
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Environment(EnvType.CLIENT)
	public record OutlineFactory(@NotNull SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
		@Override
		@NotNull
		public Particle createParticle(@NotNull SimpleParticleType defaultParticleType, @NotNull ClientLevel clientLevel, double x, double y, double z, double g, double h, double i) {
			WaveParticle waveParticle = new WaveParticle(clientLevel, this.spriteProvider, x, y, z, (float) g, (float) h);
			return waveParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public record Factory(@NotNull SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
		@Override
		@NotNull
		public Particle createParticle(@NotNull SimpleParticleType defaultParticleType, @NotNull ClientLevel clientLevel, double x, double y, double z, double g, double h, double i) {
			WaveParticle waveParticle = new WaveParticle(clientLevel, this.spriteProvider, x, y, z, (float) g, (float) h);

			int waterColor = clientLevel.getBiome(BlockPos.containing(x, y, z)).value().getWaterColor();
			waveParticle.rCol = ARGB.red(waterColor) / 255F;
			waveParticle.bCol = ARGB.blue(waterColor) / 255F;
			waveParticle.gCol = ARGB.green(waterColor) / 255F;

			return waveParticle;
		}
	}
}
