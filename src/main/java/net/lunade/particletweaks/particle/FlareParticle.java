package net.lunade.particletweaks.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class FlareParticle extends RisingParticle {
	private float rStart = 1F;
	private float rEnd = 1F;
	private float gStart = 1F;
	private float gEnd = 1F;
	private float bStart = 1F;
	private float bEnd = 1F;

	protected FlareParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider) {
		super(world, x, y, z, 0D, 0D, 0D);
		this.xd = (this.random.nextFloat() - 0.5F) * 0.035F;
		this.zd = (this.random.nextFloat() - 0.5F) * 0.035F;
		this.friction = 0.9F;
		this.pickSprite(spriteProvider);
		this.yd = 0.03D;
		this.quadSize = 0.075F;
		this.lifetime = (int)(6D / ((double)this.random.nextFloat() * 0.8D + 0.2D)) + 15;
		this.setSpriteFromAge(spriteProvider);
	}

	@Override
	public int getLightColor(float tickDelta) {
		float percentageLived = ((float)this.age + tickDelta) / (float)this.lifetime;
		return (int) Math.max(240F * (1F - percentageLived), super.getLightColor(tickDelta));
	}

	@Override
	public float getQuadSize(float tickDelta) {
		float percentageLived = ((float)this.age + tickDelta) / (float)this.lifetime;
		this.rCol = Mth.lerp(percentageLived, this.rStart, this.rEnd);
		this.gCol = Mth.lerp(percentageLived, this.gStart, this.gEnd);
		this.bCol = Mth.lerp(percentageLived, this.bStart, this.bEnd);
		return this.quadSize * (1F - percentageLived);
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
			FlareParticle flareParticle = new FlareParticle(clientLevel, x, y, z, g, h, i, spriteProvider);
			flareParticle.rEnd = 0.5F;
			flareParticle.bStart = 0F;
			flareParticle.bEnd = 0F;
			flareParticle.gEnd = 0F;
			return flareParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public record SoulFactory(@NotNull SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
		@Override
		@NotNull
		public Particle createParticle(
			@NotNull SimpleParticleType defaultParticleType, @NotNull ClientLevel clientLevel, double x, double y, double z, double g, double h, double i
		) {
			FlareParticle flareParticle = new FlareParticle(clientLevel, x, y, z, g, h, i, spriteProvider);
			flareParticle.rStart = 0F;
			flareParticle.rEnd = 0.55F;
			flareParticle.bStart = 1F;
			flareParticle.bEnd = 0F;
			flareParticle.gStart = 1F;
			flareParticle.gEnd = 0.25F;
			return flareParticle;
		}
	}
}
