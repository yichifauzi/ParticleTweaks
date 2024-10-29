package net.lunade.particletweaks.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.particletweaks.impl.ParticleTweakInterface;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class PoofParticle extends TextureSheetParticle {

	protected PoofParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider) {
		super(world, x, y, z);
		this.gravity = -0.05F;
		this.friction = 0.9F;
		this.pickSprite(spriteProvider);
		this.xd = velocityX + (Math.random() * 2D - 1D) * 0.05D;
		this.yd = velocityY + (Math.random() * 2D - 1D) * 0.05D;
		this.zd = velocityZ + (Math.random() * 2D - 1D) * 0.05D;
		this.quadSize = (0.1F * (this.random.nextFloat() * this.random.nextFloat() * 6F + 1F)) * 1.4F;
		this.lifetime = (int)((6D / ((double)this.random.nextFloat() * 0.8D + 0.2D)) * 0.75D);
		this.setSpriteFromAge(spriteProvider);

		if (this instanceof ParticleTweakInterface particleTweakInterface) {
			particleTweakInterface.particleTweaks$setNewSystem(true);
			particleTweakInterface.particleTweaks$setScaler(0.25F);
			//particleTweakInterface.particleTweaks$setScalesToZero();
		}
	}

	@Override
	public @NotNull ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Environment(EnvType.CLIENT)
	public record Factory(@NotNull SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
		@Override
		@NotNull
		public Particle createParticle(
			@NotNull SimpleParticleType defaultParticleType, @NotNull ClientLevel clientLevel, double x, double y, double z, double g, double h, double i
		) {
			return new PoofParticle(clientLevel, x, y, z, g, h, i, spriteProvider);
		}
	}
}
