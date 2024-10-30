package net.lunade.particletweaks.mixin.client.trailer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.particletweaks.particle.WaveSeedParticle;
import net.lunade.particletweaks.registry.ParticleTweaksParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
public abstract class EntityMixin {

	@Shadow
	private EntityDimensions dimensions;

	@Shadow
	public abstract double getX();

	@Shadow
	public abstract double getZ();

	@Shadow
	private Level level;

	@Shadow
	@Final
	protected RandomSource random;

	@Shadow
	public abstract double getY();

	@Inject(
		method = "doWaterSplashEffect",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;gameEvent(Lnet/minecraft/core/Holder;)V",
			shift = At.Shift.BEFORE
		)
	)
	public void particleTweaks$doWaterSplashEffect(
		CallbackInfo info
	) {
		Entity entity = Entity.class.cast(this);
		Vec3 vec3 = entity.getDeltaMovement();
		EntityDimensions entityDimensions = this.dimensions;
		float width = entityDimensions.width();

		double ySpeed = Math.abs(vec3.y);
		double strength = ySpeed * ((entity instanceof Player) ? 0.45D : width * 1.1D);
		boolean useGenericSplash = false;
		if (strength >= 0.4D) {
			BlockPos pos = entity.blockPosition();
			BlockState blockState = this.level.getBlockState(pos);
			FluidState fluidState = blockState.getFluidState();

			if (fluidState.isSourceOfType(Fluids.WATER)) {
				if (blockState.getCollisionShape(this.level, pos).isEmpty()) {
					int waterSurface = pos.getY() + 1;
					for (int i = 1; true; i++) {
						if (i == 4) return;
						BlockState aboveState = this.level.getBlockState(pos.above(i));
						FluidState aboveFluidState = aboveState.getFluidState();
						if (aboveFluidState.isSourceOfType(Fluids.WATER)) {
							waterSurface += 1;
						} else if (!aboveFluidState.isEmpty()) {
							return;
						} else {
							break;
						}
					}
					entity.level().addAlwaysVisibleParticle(
						ParticleTweaksParticleTypes.WAVE_SEED,
						this.getX(),
						waterSurface,
						this.getZ(),
						width,
						strength - 0.35D,
						0F
					);
				} else {
					useGenericSplash = fluidState.is(FluidTags.WATER);
				}
			} else {
				useGenericSplash = fluidState.is(FluidTags.WATER);
			}
		} else {
			useGenericSplash = true;
		}

		if (useGenericSplash) {
			BlockPos pos = entity.blockPosition();
			int waterSurface = pos.getY() + 1;
			for (int i = 1; true; i++) {
				if (i == 4) return;
				BlockState aboveState = this.level.getBlockState(pos.above(i));
				FluidState aboveFluidState = aboveState.getFluidState();
				if (aboveFluidState.isSourceOfType(Fluids.WATER)) {
					waterSurface += 1;
				} else if (!aboveFluidState.isEmpty()) {
					return;
				} else {
					break;
				}
			}
			WaveSeedParticle.spawnSplashParticles(
				this.level,
				this.getX(),
				waterSurface,
				this.getZ(),
				this.random,
				this.random.nextInt(2, 5),
				width,
				0.1F,
				0.2F
			);
		}
	}
}
