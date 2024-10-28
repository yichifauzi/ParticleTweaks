package net.lunade.particletweaks.impl;

import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlowingFluidParticleUtil {

	public static @Nullable Vec3 handleFluidInteraction(
		Level level,
		Vec3 pos,
		Vec3 movement,
		Particle particle,
		boolean safeInLavaOrFire,
		boolean slowsInFluid,
		boolean flowsWithFluid,
		double fluidMovementScale
	) {
		if (!safeInLavaOrFire || slowsInFluid || flowsWithFluid) {
			BlockPos blockPos = BlockPos.containing(pos);
			BlockState blockState = level.getBlockState(blockPos);
			FluidState fluidState = blockState.getFluidState();
			boolean isFluidHighEnough = false;
			boolean atRiskOfLava = !safeInLavaOrFire && fluidState.is(FluidTags.LAVA);

			if (slowsInFluid || flowsWithFluid || atRiskOfLava) {
				isFluidHighEnough = !fluidState.isEmpty() && (fluidState.getHeight(level, blockPos) + (float) blockPos.getY()) >= pos.y;
			}

			boolean willBurn = atRiskOfLava && isFluidHighEnough;
			if (!safeInLavaOrFire && fluidState.isEmpty() && blockState.is(BlockTags.FIRE)) {
				AABB shape = blockState.getShape(level, blockPos).bounds().move(blockPos);
				if (shape.contains(pos)) {
					willBurn = true;
				}
			}

			if (willBurn) {
				level.addParticle(
					ParticleTypes.SMOKE,
					pos.x,
					pos.y,
					pos.z,
					0D,
					0D,
					0D
				);
				particle.remove();
				return null;
			}

			if (slowsInFluid && isFluidHighEnough) {
				movement = new Vec3(movement.x * 0.8D, movement.y * 0.1D, movement.z * 0.8D);
			}

			if (flowsWithFluid && isFluidHighEnough) {
				Vec3 flow = fluidState.getFlow(level, blockPos);
				movement = movement.add(flow.scale(fluidMovementScale));
			}
		}

		return movement;
	}

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
