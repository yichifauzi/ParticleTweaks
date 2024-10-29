package net.lunade.particletweaks.mixin.client.fluid_particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.particletweaks.impl.FlowingFluidParticleUtil;
import net.lunade.particletweaks.registry.ParticleTweaksParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LavaFluid.class)
public class LavaFluidMixin {

	@Inject(
		method = "animateTick",
		at = @At("TAIL")
	)
	public void particleTweaks$animateTick(Level world, BlockPos pos, FluidState state, RandomSource random, CallbackInfo info) {
		FlowingFluidParticleUtil.onAnimateTick(
			world,
			pos,
			state,
			random,
			1,
			0,
			10,
			false,
			false,
			ParticleTweaksParticleTypes.FLOWING_LAVA
		);
	}
}
