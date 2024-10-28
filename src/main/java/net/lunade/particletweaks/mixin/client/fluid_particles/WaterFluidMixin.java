package net.lunade.particletweaks.mixin.client.fluid_particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.particletweaks.impl.FlowingFluidParticleGenerator;
import net.lunade.particletweaks.registry.ParticleTweaksParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(WaterFluid.class)
public class WaterFluidMixin {

	@Inject(
		method = "animateTick",
		at = @At("TAIL")
	)
	public void particleTweaks$animateTick(Level world, BlockPos pos, FluidState state, RandomSource random, CallbackInfo info) {
		FlowingFluidParticleGenerator.onAnimateTick(world, pos, state, random, 3, 1, true, ParticleTweaksParticleTypes.FLOWING_WATER);
	}
}
