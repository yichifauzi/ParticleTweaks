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
import net.lunade.particletweaks.impl.ParticleTweakInterface;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class RippleParticle extends TextureSheetParticle {
	private final SpriteSet sprites;

	RippleParticle(@NotNull ClientLevel level, @NotNull SpriteSet spriteProvider, double x, double y, double z) {
		super(level, x, y + 1D, z, 0D, 0D, 0D);
		this.xd = 0D;
		this.yd = 0D;
		this.zd = 0D;
		this.setSize(0.0875F, 0.01F);
		this.setSpriteFromAge(spriteProvider);
		this.sprites = spriteProvider;
		this.lifetime = 5;
		this.quadSize = 0.35F;
		this.gravity = 0F;

		if (this instanceof ParticleTweakInterface particleTweakInterface) {
			particleTweakInterface.particleTweaks$setNewSystem(true);
			particleTweakInterface.particleTweaks$setScalesToZero();
			particleTweakInterface.particleTweaks$setScaler(0.45F);
			particleTweakInterface.particleTweaks$setSwitchesExit(true);
		}
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteFromAge(this.sprites);
	}

	@Override
	public void render(VertexConsumer buffer, @NotNull Camera camera, float tickDelta) {
		float yRot = 90F * Mth.DEG_TO_RAD;
		this.renderParticle(buffer, camera, tickDelta, transforms -> transforms.rotateX(yRot));
	}

	private static final Vector3f NORMALIZED_QUAT_VECTOR = new Vector3f(0.5F, 0.5F, 0.5F).normalize();

	private void renderParticle(
		VertexConsumer buffer,
		@NotNull Camera renderInfo,
		float tickDelta,
		@NotNull Consumer<Quaternionf> quaternionConsumer
	) {
		Vec3 vec3 = renderInfo.getPosition();
		float f = (float)(Mth.lerp(tickDelta, this.xo, this.x) - vec3.x());
		float g = (float)(Mth.lerp(tickDelta, this.yo, this.y) - vec3.y()) - 1F;
		float h = (float)(Mth.lerp(tickDelta, this.zo, this.z) - vec3.z());
		Quaternionf quaternionf = new Quaternionf().setAngleAxis(0F, NORMALIZED_QUAT_VECTOR.x(), NORMALIZED_QUAT_VECTOR.y(), NORMALIZED_QUAT_VECTOR.z());
		quaternionConsumer.accept(quaternionf);
		Vector3f[] vector3fs = new Vector3f[]{
			new Vector3f(-1F, -1F, 0F),
			new Vector3f(-1F, 1F, 0F),
			new Vector3f(1F, 1F, 0F),
			new Vector3f(1F, -1F, 0F)
		};
		float i = this.getQuadSize(tickDelta);

		for (int j = 0; j < 4; ++j) {
			Vector3f vector3f2 = vector3fs[j];
			vector3f2.rotate(quaternionf);
			vector3f2.mul(i);
			vector3f2.add(f, g, h);
		}

		float k = this.getU0();
		float l = this.getU1();
		float m = this.getV0();
		float n = this.getV1();
		int light = this.getLightColor(tickDelta);
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

	@Override
	@NotNull
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Environment(EnvType.CLIENT)
	public record Factory(@NotNull SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
		@Override
		@NotNull
		public Particle createParticle(@NotNull SimpleParticleType defaultParticleType, @NotNull ClientLevel clientLevel, double x, double y, double z, double g, double h, double i) {
			return new RippleParticle(clientLevel, this.spriteProvider, x, y, z);
		}
	}
}
