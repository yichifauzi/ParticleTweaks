package net.lunade.particletweaks.mixin.client.trailer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.particletweaks.config.ParticleTweaksConfigGetter;
import net.lunade.particletweaks.registry.ParticleTweaksParticleTypes;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

	@WrapMethod(method = "addParticleInternal(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;")
	public Particle particleTweaks$trailerReplacements(
		ParticleOptions parameters,
		boolean alwaysSpawn,
		boolean canSpawnOnMinimal,
		double x,
		double y,
		double z,
		double velocityX,
		double velocityY,
		double velocityZ,
		Operation<Particle> original
	) {
		if (parameters == ParticleTypes.POOF && ParticleTweaksConfigGetter.trailerPoof()) {
			parameters = ParticleTweaksParticleTypes.POOF;
		}
		return original.call(parameters, alwaysSpawn, canSpawnOnMinimal, x, y, z, velocityX, velocityY, velocityZ);
	}

	@ModifyExpressionValue(
		method = "tickRain",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/core/particles/ParticleTypes;RAIN:Lnet/minecraft/core/particles/SimpleParticleType;"
		)
	)
	public SimpleParticleType particleTweaks$useRippleOnWater(
		SimpleParticleType original,
		@Local BlockState blockState,
		@Local FluidState fluidState,
		@Local VoxelShape voxelShape,
		@Local(ordinal = 0) double xOffset,
		@Local(ordinal = 1) double zOffset,
		@Local(ordinal = 1) BlockPos blockPos
	) {
		if (ParticleTweaksConfigGetter.trailerSplashes() && fluidState.is(FluidTags.WATER)) {
			return ParticleTweaksParticleTypes.RIPPLE;
		}
		return original;
	}
}
