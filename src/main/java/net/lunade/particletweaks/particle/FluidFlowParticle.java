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
import net.minecraft.tags.FluidTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class FluidFlowParticle extends TextureSheetParticle {
	private static final int LAVA_COLOR = 16743195;
	private final boolean isLava;
	private final boolean floatOnFluid;
	private final boolean endWhenUnderFluid;

	FluidFlowParticle(ClientLevel world, @NotNull SpriteSet spriteProvider, double d, double e, double f, double velX, double velY, double velZ, @NotNull FluidState fluid) {
		super(world, d, e, f, velX, velY, velZ);
		this.xd = velX;
		this.yd = velY;
		this.zd = velZ;
		this.pickSprite(spriteProvider);
		this.isLava = fluid.is(FluidTags.LAVA);
		this.floatOnFluid = !this.isLava;
		this.gravity = 0.9F;

		if (this.isLava) {
			this.rCol = FastColor.ARGB32.red(LAVA_COLOR) / 255F;
			this.bCol = FastColor.ARGB32.blue(LAVA_COLOR) / 255F;
			this.gCol = FastColor.ARGB32.green(LAVA_COLOR) / 255F;
			this.quadSize *= 0.75F;
			this.endWhenUnderFluid = false;
			this.setSize(0.078125F, 0.078125F);
		} else if (fluid.is(FluidTags.WATER)) {
			int waterColor = world.getBiome(BlockPos.containing(d, e, f)).value().getWaterColor();
			this.rCol = Math.clamp(((FastColor.ARGB32.red(waterColor) / 255F) * (float)world.random.triangle(1.25D, 0.25D)), 0F, 1F);
			this.bCol = Math.clamp(((FastColor.ARGB32.blue(waterColor) / 255F) * (float)world.random.triangle(1.25D, 0.25D)), 0F, 1F);
			this.gCol = Math.clamp(((FastColor.ARGB32.green(waterColor) / 255F) * (float)world.random.triangle(1.25D, 0.25D)), 0F, 1F);
			this.alpha = 0.6F;
			this.quadSize *= 0.5F;
			this.endWhenUnderFluid = false;
			this.setSize(0.0625F, 0.0625F);
		} else {
			int waterColor = world.getBiome(BlockPos.containing(d, e, f)).value().getWaterColor();
			this.rCol = Math.clamp(((FastColor.ARGB32.red(waterColor) / 255F) * (float)world.random.triangle(1.35D, 0.4D)), 0F, 1F);
			this.bCol = Math.clamp(((FastColor.ARGB32.blue(waterColor) / 255F) * (float)world.random.triangle(1.35D, 0.4D)), 0F, 1F);
			this.gCol = Math.clamp(((FastColor.ARGB32.green(waterColor) / 255F) * (float)world.random.triangle(1.35D, 0.4D)), 0F, 1F);
			this.alpha = 0.175F;
			this.quadSize *= 2F;
			this.endWhenUnderFluid = true;
		}

		if (this instanceof ParticleTweakInterface particleTweakInterface) {
			particleTweakInterface.particleTweaks$setNewSystem(true);
			particleTweakInterface.particleTweaks$setMovesWithFluid(true);
			particleTweakInterface.particleTweaks$setCanBurn(!this.isLava);
			particleTweakInterface.particleTweaks$setScalesToZero();
			particleTweakInterface.particleTweaks$setSwitchesExit(true);
			particleTweakInterface.particleTweaks$setFluidMovementScale(0.05D);
			particleTweakInterface.particleTweaks$setScaler(0.5F);
			if (!this.isLava) {
				if (fluid.is(FluidTags.WATER)) {
					particleTweakInterface.particleTweaks$setMaxAlpha(0.6F);
				} else {
					particleTweakInterface.particleTweaks$setMaxAlpha(0.175F);
				}
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.onGround) {
			this.age = this.lifetime;
		} else {
			BlockPos blockPos = BlockPos.containing(this.x, this.y, this.z);
			BlockState blockState = this.level.getBlockState(blockPos);
			FluidState fluidState = blockState.getFluidState();
			float fluidHeight = fluidState.getHeight(this.level, blockPos);
			boolean isFluidHighEnough = !fluidState.isEmpty() && (fluidHeight + (float) blockPos.getY()) >= this.y;
			if (isFluidHighEnough) {
				if (fluidState.getFlow(this.level, blockPos).horizontalDistance() == 0D) this.age = Math.clamp(this.age + 1, 0, this.lifetime);
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
			return new FluidFlowParticle(clientLevel, this.spriteProvider, x, y, z, g, h, i, Fluids.LAVA.defaultFluidState());
		}
	}

	@Environment(EnvType.CLIENT)
	public record WaterFactory(SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
		@Override
		@NotNull
		public Particle createParticle(
			@NotNull SimpleParticleType particleOptions, @NotNull ClientLevel clientLevel, double x, double y, double z, double g, double h, double i
		) {
			return new FluidFlowParticle(clientLevel, this.spriteProvider, x, y, z, g, h, i, Fluids.WATER.defaultFluidState());
		}
	}

	@Environment(EnvType.CLIENT)
	public record SplashFactory(SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
		@Override
		@NotNull
		public Particle createParticle(
			@NotNull SimpleParticleType particleOptions, @NotNull ClientLevel clientLevel, double x, double y, double z, double g, double h, double i
		) {
			return new FluidFlowParticle(clientLevel, this.spriteProvider, x, y, z, g, h, i, Fluids.EMPTY.defaultFluidState());
		}
	}
}
