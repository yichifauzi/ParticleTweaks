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
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class FluidFlowParticle extends TextureSheetParticle {
	private static final int LAVA_COLOR = 16743195;
	private final SpriteSet sprites;
	private boolean isLava;
	private boolean floatOnFluid;
	private boolean endWhenUnderFluid;

	FluidFlowParticle(
		ClientLevel world, @NotNull SpriteSet spriteProvider, double d, double e, double f, double velX, double velY, double velZ
	) {
		super(world, d, e, f, velX, velY, velZ);
		this.xd = velX;
		this.yd = velY;
		this.zd = velZ;
		this.setSpriteFromAge(spriteProvider);
		this.sprites = spriteProvider;
		this.gravity = 0.9F;
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteFromAge(this.sprites);
		if (this.onGround) {
			this.age = this.lifetime;
		} else {
			BlockPos blockPos = BlockPos.containing(this.x, this.y, this.z);
			BlockState blockState = this.level.getBlockState(blockPos);
			FluidState fluidState = blockState.getFluidState();
			float fluidHeight = fluidState.getHeight(this.level, blockPos);
			boolean isFluidHighEnough = !fluidState.isEmpty() && (fluidHeight + (float) blockPos.getY()) >= this.y;
			if (isFluidHighEnough) {
				if (fluidState.getFlow(this.level, blockPos).horizontalDistance() == 0D) this.age = Math.clamp(this.age + 3, 0, this.lifetime);
				if (this.floatOnFluid) {
					if (!fluidState.hasProperty(FlowingFluid.FALLING) || !fluidState.getValue(FlowingFluid.FALLING)) {
						if (this.yd < 0.01D) {
							this.yd += 0.05D;
						}
						this.yd += (0F - this.yd) * 0.4D;
						this.y += ((blockPos.getY() + fluidHeight) - this.y) * 0.5D;
					}
				}
				if (this.endWhenUnderFluid) {
					this.age = this.lifetime;
				}
			}
		}
	}

	@Override
	protected int getLightColor(float tint) {
		return this.isLava ? 240 : super.getLightColor(tint);
	}

	@Override
	public @NotNull ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Environment(EnvType.CLIENT)
	public record LavaFactory(SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
		@Override
		@NotNull
		public Particle createParticle(
			@NotNull SimpleParticleType particleOptions, @NotNull ClientLevel clientLevel, double x, double y, double z, double g, double h, double i
		) {
			FluidFlowParticle lavaParticle = new FluidFlowParticle(clientLevel, this.spriteProvider, x, y, z, g, h, i);

			lavaParticle.rCol = FastColor.ARGB32.red(LAVA_COLOR) / 255F;
			lavaParticle.bCol = FastColor.ARGB32.blue(LAVA_COLOR) / 255F;
			lavaParticle.gCol = FastColor.ARGB32.green(LAVA_COLOR) / 255F;

			lavaParticle.isLava = true;
			lavaParticle.quadSize *= 0.75F;
			lavaParticle.floatOnFluid = false;
			lavaParticle.endWhenUnderFluid = false;
			lavaParticle.setSize(0.078125F, 0.078125F);

			if (lavaParticle instanceof ParticleTweakInterface particleTweakInterface) {
				particleTweakInterface.particleTweaks$setNewSystem(true);
				particleTweakInterface.particleTweaks$setMovesWithFluid(true);
				particleTweakInterface.particleTweaks$setCanBurn(true);
				particleTweakInterface.particleTweaks$setScalesToZero();
				particleTweakInterface.particleTweaks$setSwitchesExit(true);
				particleTweakInterface.particleTweaks$setFluidMovementScale(0.05D);
				particleTweakInterface.particleTweaks$setScaler(0.5F);
			}

			return lavaParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public record WaterFactory(SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
		@Override
		@NotNull
		public Particle createParticle(
			@NotNull SimpleParticleType particleOptions, @NotNull ClientLevel clientLevel, double x, double y, double z, double g, double h, double i
		) {
			FluidFlowParticle waterParticle = new FluidFlowParticle(clientLevel, this.spriteProvider, x, y, z, g, h, i);

			int waterColor = clientLevel.getBiome(BlockPos.containing(x, y, z)).value().getWaterColor();
			waterParticle.rCol = Math.clamp(((FastColor.ARGB32.red(waterColor) / 255F) * (float)clientLevel.random.triangle(1.3D, 0.3D)), 0F, 1F);
			waterParticle.bCol = Math.clamp(((FastColor.ARGB32.blue(waterColor) / 255F) * (float)clientLevel.random.triangle(1.3D, 0.3D)), 0F, 1F);
			waterParticle.gCol = Math.clamp(((FastColor.ARGB32.green(waterColor) / 255F) * (float)clientLevel.random.triangle(1.3D, 0.3D)), 0F, 1F);

			waterParticle.alpha = 0.6F;
			waterParticle.quadSize *= 0.5F;
			waterParticle.floatOnFluid = true;
			waterParticle.endWhenUnderFluid = false;
			waterParticle.setSize(0.0325F, 0.0325F);

			if (waterParticle instanceof ParticleTweakInterface particleTweakInterface) {
				particleTweakInterface.particleTweaks$setNewSystem(true);
				particleTweakInterface.particleTweaks$setMovesWithFluid(true);
				particleTweakInterface.particleTweaks$setCanBurn(true);
				particleTweakInterface.particleTweaks$setScalesToZero();
				particleTweakInterface.particleTweaks$setSwitchesExit(true);
				particleTweakInterface.particleTweaks$setFluidMovementScale(0.05D);
				particleTweakInterface.particleTweaks$setScaler(0.5F);
				particleTweakInterface.particleTweaks$setMaxAlpha(0.6F);
			}

			return waterParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public record SplashFactory(SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
		@Override
		@NotNull
		public Particle createParticle(
			@NotNull SimpleParticleType particleOptions, @NotNull ClientLevel clientLevel, double x, double y, double z, double g, double h, double i
		) {
			FluidFlowParticle splashParticle = new FluidFlowParticle(clientLevel, this.spriteProvider, x, y, z, g, h, i);

			splashParticle.alpha = 0.6F;
			splashParticle.endWhenUnderFluid = true;
			splashParticle.quadSize *= 1.5F;
			splashParticle.lifetime *= 2;

			if (splashParticle instanceof ParticleTweakInterface particleTweakInterface) {
				particleTweakInterface.particleTweaks$setNewSystem(true);
				particleTweakInterface.particleTweaks$setMovesWithFluid(true);
				particleTweakInterface.particleTweaks$setCanBurn(true);
				particleTweakInterface.particleTweaks$setScalesToZero();
				particleTweakInterface.particleTweaks$setSwitchesExit(true);
				particleTweakInterface.particleTweaks$setFluidMovementScale(0.05D);
				particleTweakInterface.particleTweaks$setScaler(0.5F);
				particleTweakInterface.particleTweaks$setMaxAlpha(0.6F);
			}

			return splashParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public record SmallCascadeFactory(SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
		@Override
		@NotNull
		public Particle createParticle(
			@NotNull SimpleParticleType particleOptions, @NotNull ClientLevel clientLevel, double x, double y, double z, double g, double h, double i
		) {
			FluidFlowParticle smallCascadeParticle = new FluidFlowParticle(clientLevel, this.spriteProvider, x, y, z, g, h, i);

			int waterColor = clientLevel.getBiome(BlockPos.containing(x, y, z)).value().getWaterColor();
			smallCascadeParticle.rCol = Math.clamp(((FastColor.ARGB32.red(waterColor) / 255F) * (float)clientLevel.random.triangle(1.3D, 0.3D)), 0F, 1F);
			smallCascadeParticle.bCol = Math.clamp(((FastColor.ARGB32.blue(waterColor) / 255F) * (float)clientLevel.random.triangle(1.3D, 0.3D)), 0F, 1F);
			smallCascadeParticle.gCol = Math.clamp(((FastColor.ARGB32.green(waterColor) / 255F) * (float)clientLevel.random.triangle(1.3D, 0.3D)), 0F, 1F);

			smallCascadeParticle.alpha = 0.25F;
			smallCascadeParticle.endWhenUnderFluid = true;
			smallCascadeParticle.quadSize *= 1.5F;

			if (smallCascadeParticle instanceof ParticleTweakInterface particleTweakInterface) {
				particleTweakInterface.particleTweaks$setNewSystem(true);
				particleTweakInterface.particleTweaks$setMovesWithFluid(true);
				particleTweakInterface.particleTweaks$setCanBurn(true);
				particleTweakInterface.particleTweaks$setScalesToZero();
				particleTweakInterface.particleTweaks$setSwitchesExit(true);
				particleTweakInterface.particleTweaks$setFluidMovementScale(0.125D);
				particleTweakInterface.particleTweaks$setScaler(0.5F);
				particleTweakInterface.particleTweaks$setMaxAlpha(0.25F);
			}

			return smallCascadeParticle;
		}
	}

	@Environment(EnvType.CLIENT)
	public record CascadeFactory(SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
		@Override
		@NotNull
		public Particle createParticle(
			@NotNull SimpleParticleType particleOptions, @NotNull ClientLevel clientLevel, double x, double y, double z, double g, double h, double i
		) {
			FluidFlowParticle cascadeParticle = new FluidFlowParticle(clientLevel, this.spriteProvider, x, y, z, g, h, i);

			cascadeParticle.alpha = 0.75F;
			cascadeParticle.endWhenUnderFluid = false;
			cascadeParticle.quadSize *= 2.5F;

			if (cascadeParticle instanceof ParticleTweakInterface particleTweakInterface) {
				particleTweakInterface.particleTweaks$setNewSystem(true);
				particleTweakInterface.particleTweaks$setMovesWithFluid(true);
				particleTweakInterface.particleTweaks$setCanBurn(true);
				particleTweakInterface.particleTweaks$setScalesToZero();
				particleTweakInterface.particleTweaks$setSwitchesExit(true);
				particleTweakInterface.particleTweaks$setFluidMovementScale(0.125D);
				particleTweakInterface.particleTweaks$setScaler(0.5F);
				particleTweakInterface.particleTweaks$setMaxAlpha(0.75F);
			}

			return cascadeParticle;
		}
	}
}
