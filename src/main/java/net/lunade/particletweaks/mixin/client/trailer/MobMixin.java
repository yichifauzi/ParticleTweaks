package net.lunade.particletweaks.mixin.client.trailer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(Mob.class)
public class MobMixin {

	@ModifyExpressionValue(
		method = "spawnAnim",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/core/particles/ParticleTypes;POOF:Lnet/minecraft/core/particles/SimpleParticleType;"
		)
	)
	public SimpleParticleType particleTweaks$useBubblePoofUnderwater(SimpleParticleType original) {
		return LivingEntity.class.cast(this).isUnderWater() ? ParticleTypes.BUBBLE : original;
	}
}
