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
import net.lunade.particletweaks.impl.ParticleTweakInterface;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.RisingParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class CaveDustParticle extends RisingParticle {

	CaveDustParticle(@NotNull ClientLevel level, @NotNull SpriteSet spriteProvider, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		super(level, x, y - 0.125D, z, velocityX, velocityY, velocityZ);
		this.setSize(0.01F, 0.02F);
		this.pickSprite(spriteProvider);
		this.hasPhysics = true;
		this.alpha = 0.2F;

		if (this instanceof ParticleTweakInterface particleTweakInterface) {
			particleTweakInterface.particleTweaks$setNewSystem(true);
			particleTweakInterface.particleTweaks$setScalesToZero();
			particleTweakInterface.particleTweaks$setSlowsInFluid(true);
			particleTweakInterface.particleTweaks$setMovesWithFluid(true);
			particleTweakInterface.particleTweaks$setCanBurn(true);
			particleTweakInterface.particleTweaks$setMaxAlpha(0.2F);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.y == this.yo || this.onGround) {
			this.age = this.lifetime;
		}
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
			CaveDustParticle caveParticle = new CaveDustParticle(clientLevel, this.spriteProvider, x, y, z, 0D, 0.02D, 0D);
			return caveParticle;
		}
	}
}
