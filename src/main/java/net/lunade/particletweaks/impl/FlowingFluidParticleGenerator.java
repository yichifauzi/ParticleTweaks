package net.lunade.particletweaks.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class FlowingFluidParticleGenerator {

	public static void onAnimateTick(
		Level world,
		BlockPos pos,
		@NotNull FluidState state,
		RandomSource random,
		int horizontalChance,
		int downChance,
		boolean horizontalParticles,
		ParticleOptions particle
	) {
		if (!state.isSource() && !state.isEmpty()) {
			Vec3 rawFlow = state.getFlow(world, pos);
			Vec3 flowVec = rawFlow.normalize();
			Direction flowDir = Direction.getNearest(flowVec);
			float fluidHeight = state.getHeight(world, pos);
			boolean isDown = state.getValue(FlowingFluid.FALLING);
			if ((isDown || horizontalParticles) && random.nextInt(isDown ? downChance : horizontalChance) == 0) {
				Vec3 bottomCenter = pos.getBottomCenter();
				if (!isDown) {
					spawnParticleFromDirection(world, bottomCenter, flowDir, false, fluidHeight, random, particle);
				} else {
					for (Direction direction : Direction.Plane.HORIZONTAL) {
						spawnParticleFromDirection(world, bottomCenter, direction, true, fluidHeight, random, particle);
					}
				}
			}
		}
	}

	private static void spawnParticleFromDirection(
		@NotNull Level world, @NotNull Vec3 bottomCenter, @NotNull Direction direction, boolean isDown, float fluidHeight, RandomSource random, ParticleOptions particle
	) {
		BlockPos otherPos = BlockPos.containing(bottomCenter).relative(direction);
		BlockState otherState = world.getBlockState(otherPos);
		if (otherState.getCollisionShape(world, otherPos).isEmpty() && (otherState.getFluidState().isEmpty() || !isDown)) {
			Vec3 directionOffset = Vec3.atLowerCornerOf(direction.getNormal()).scale(0.5D);
			Vec3 offsetPos = bottomCenter.add(directionOffset);
			double yOffset = isDown ? random.nextDouble() * fluidHeight : fluidHeight;
			double horizontalOffset = random.nextDouble() * 0.9D;
			offsetPos = offsetPos.add(horizontalOffset * Math.abs(direction.getStepZ()), yOffset, horizontalOffset * Math.abs(direction.getStepX()));
			Vec3 velocity = directionOffset.scale(isDown ? 0.075D : 0.3D);
			world.addParticle(particle, offsetPos.x, offsetPos.y, offsetPos.z, velocity.x, 0D, velocity.z);
		}
	}

}
