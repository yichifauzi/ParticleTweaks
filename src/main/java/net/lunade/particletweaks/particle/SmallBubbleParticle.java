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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.particletweaks.impl.FlowingFluidParticleUtil;
import net.lunade.particletweaks.impl.ParticleTweakInterface;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.RisingParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class SmallBubbleParticle extends RisingParticle {
	private final Vec3 direction;
	private final float swaySpeed;

	SmallBubbleParticle(@NotNull ClientLevel level, @NotNull SpriteSet spriteProvider, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		super(level, x, y - 0.125D, z, velocityX, velocityY, velocityZ);
		this.setSize(0.01F, 0.01F);
		this.pickSprite(spriteProvider);
		this.yd = velocityY;
		this.quadSize = this.quadSize * (this.random.nextFloat() * 0.6F + 0.2F);
		this.lifetime = (int)(16D / (Math.random() * 0.8D + 0.2D));
		this.friction = 1F;
		this.hasPhysics = true;

		this.swaySpeed = (0.125F - (float)velocityY) * 80F;
		this.direction = new Vec3(1D, 0D, 0D).yRot((random.nextFloat() * 360F) * Mth.DEG_TO_RAD);

		int waterColor = level.getBiome(BlockPos.containing(x, y, z)).value().getWaterColor();
		this.rCol = Math.clamp(((FastColor.ARGB32.red(waterColor) / 255F) * (float)level.random.triangle(1.3D, 0.3D)), 0F, 1F);
		this.bCol = Math.clamp(((FastColor.ARGB32.blue(waterColor) / 255F) * (float)level.random.triangle(1.3D, 0.3D)), 0F, 1F);
		this.gCol = Math.clamp(((FastColor.ARGB32.green(waterColor) / 255F) * (float)level.random.triangle(1.3D, 0.3D)), 0F, 1F);

		if (this instanceof ParticleTweakInterface particleTweakInterface) {
			particleTweakInterface.particleTweaks$setNewSystem(true);
			particleTweakInterface.particleTweaks$setScalesToZero();
			particleTweakInterface.particleTweaks$setScaler(0.6F);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.y == this.yo || this.onGround || !FlowingFluidParticleUtil.isUnderFluid(this.level, this.x, this.y + 0.3D, this.z)) {
			this.age = this.lifetime;
		}

		double sin = Math.sin((this.age * Math.PI) / (this.swaySpeed));
		this.xd = sin * (this.yd * this.direction.x()) * 0.35D;
		this.zd = sin * (this.yd * this.direction.z()) * 0.35D;
	}

	@Override
	@NotNull
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Environment(EnvType.CLIENT)
	public record Factory(@NotNull SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
		@Override
		@NotNull
		public Particle createParticle(@NotNull SimpleParticleType defaultParticleType, @NotNull ClientLevel clientLevel, double x, double y, double z, double g, double h, double i) {
			return new SmallBubbleParticle(
				clientLevel,
				this.spriteProvider,
				x,
				y,
				z,
				0D,
				clientLevel.random.nextDouble() * clientLevel.random.triangle(0.5D, 0.3D) * 0.05D,
				0D
			);
		}
	}
}
