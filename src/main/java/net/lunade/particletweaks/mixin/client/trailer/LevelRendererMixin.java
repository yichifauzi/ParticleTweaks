package net.lunade.particletweaks.mixin.client.trailer;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.particletweaks.registry.ParticleTweaksParticleTypes;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;

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
		if (parameters == ParticleTypes.POOF) {
			parameters = ParticleTweaksParticleTypes.POOF;
		}
		return original.call(parameters, alwaysSpawn, canSpawnOnMinimal, x, y, z, velocityX, velocityY, velocityZ);
	}
}
