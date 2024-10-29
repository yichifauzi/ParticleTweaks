package net.lunade.particletweaks.mixin.client.trailer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.particletweaks.registry.ParticleTweaksParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TorchBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(TorchBlock.class)
public class TorchBlockMixin {

	@WrapOperation(
		method = "animateTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
			ordinal = 0
		)
	)
	public void particleTweaks$trailerSmoke(
		Level instance, ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Void> original
	) {
		parameters = instance.random.nextBoolean() ? ParticleTweaksParticleTypes.COMFY_SMOKE_A : ParticleTweaksParticleTypes.COMFY_SMOKE_B;
		original.call(instance, parameters, x, y, z, velocityX, velocityY, velocityZ);
	}

	@WrapOperation(
		method = "animateTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
			ordinal = 1
		)
	)
	public void particleTweaks$animateTick(
		Level instance, ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Void> original
	) {
		if (parameters == ParticleTypes.FLAME) {
			if (instance.random.nextBoolean()) {
				original.call(instance, parameters, x, y, z, velocityX, velocityY, velocityZ);
			}
			parameters = ParticleTweaksParticleTypes.FLARE;
			y -= 0.125D;
		} else if (parameters == ParticleTypes.SOUL_FIRE_FLAME) {
			if (instance.random.nextBoolean()) {
				original.call(instance, parameters, x, y, z, velocityX, velocityY, velocityZ);
			}
			parameters = ParticleTweaksParticleTypes.SOUL_FLARE;
			y -= 0.125D;
		}
		original.call(instance, parameters, x, y, z, velocityX, velocityY, velocityZ);
	}

}
