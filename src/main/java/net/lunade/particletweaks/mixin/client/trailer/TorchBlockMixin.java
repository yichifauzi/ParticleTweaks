package net.lunade.particletweaks.mixin.client.trailer;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.particletweaks.config.ParticleTweaksConfigGetter;
import net.lunade.particletweaks.impl.TorchParticleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(TorchBlock.class)
public class TorchBlockMixin {

	@WrapWithCondition(
		method = "animateTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
			ordinal = 0
		)
	)
	public boolean particleTweaks$trailerSmoke(
		Level instance, ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ
	) {
		return !ParticleTweaksConfigGetter.trailerTorches();
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
		Level instance, ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Void> original,
		BlockState state, Level world, BlockPos pos
	) {
		if (ParticleTweaksConfigGetter.trailerTorches()) {
			if (instance.random.nextBoolean()) {
				Minecraft minecraft = Minecraft.getInstance();
				Vec3 cameraPos = minecraft.gameRenderer.getMainCamera().getPosition();
				Vec3 posDiff = cameraPos
					.subtract(0D, cameraPos.y, 0D)
					.subtract(new Vec3(x, 0D, z))
					.normalize().scale(0.0625D);
				original.call(instance, parameters, x + posDiff.x, y - 0.075D, z + posDiff.z, velocityX, velocityY, velocityZ);
			}
			TorchParticleUtil.onAnimateTick(pos);
		} else {
			original.call(instance, parameters, x, y, z, velocityX, velocityY, velocityZ);
		}
	}

}
