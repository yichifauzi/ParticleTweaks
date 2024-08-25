package net.lunade.particletweaks.mixin.client;

import net.lunade.particletweaks.impl.ParticleTweakInterface;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TerrainParticle.class, priority = 999)
public abstract class TerrainParticleMixin implements ParticleTweakInterface {

	@Inject(
		method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/world/level/block/state/BlockState;)V",
		at = @At("TAIL")
	)
	private void particleTweaks$initNonPos(
		ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, BlockState state,
		CallbackInfo info
	) {
		this.particleTweaks$setNewSystem(true);
		this.particleTweaks$setScaler(0.35F);
		this.particleTweaks$setFadeInsteadOfScale(true);
		this.particleTweaks$setSlowsInFluid(true);
		this.particleTweaks$setMovesWithFluid(true);
		this.particleTweaks$setCanBurn(!state.getBlock().asItem().components().has(DataComponents.FIRE_RESISTANT));
	}

	@Inject(
		method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V",
		at = @At("TAIL")
	)
	private void particleTweaks$init(
		ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, BlockState state, BlockPos blockPos,
		CallbackInfo info
	) {
		this.particleTweaks$setNewSystem(true);
		this.particleTweaks$setScaler(0.35F);
		this.particleTweaks$setFadeInsteadOfScale(true);
		this.particleTweaks$setSlowsInFluid(true);
		this.particleTweaks$setMovesWithFluid(true);
		this.particleTweaks$setCanBurn(!state.getBlock().asItem().components().has(DataComponents.FIRE_RESISTANT));
	}

}
