package net.lunade.particletweaks.impl;

import java.util.ArrayList;
import java.util.List;
import net.lunade.particletweaks.registry.ParticleTweaksParticleTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
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
		boolean createCascades,
		ParticleOptions particle
	) {
		if (!state.isSource() && !state.isEmpty()) {
			Vec3 rawFlow = state.getFlow(world, pos);
			Vec3 flowVec = rawFlow.normalize();
			Direction flowDir = Direction.getNearest(flowVec);
			float fluidHeight = state.getHeight(world, pos);
			boolean isDown = state.getValue(FlowingFluid.FALLING);
			if ((isDown || horizontalParticles) && random.nextInt(isDown ? downChance : horizontalChance) == 0) {
				if (!isDown) {
					spawnParticleFromDirection(world, pos, flowDir, 1, false, 0.225D, 0.3D, fluidHeight, random, particle);
				} else {
					for (Direction direction : Direction.Plane.HORIZONTAL) {
						spawnParticleFromDirection(world, pos, direction, 1, true, 0.075D, 0.1D, fluidHeight, random, particle);
					}
				}
			}

			if (createCascades) {
				if (isDown) {
					FluidState belowFluidState = world.getFluidState(pos.below());
					if (belowFluidState.isSource()) {
						BlockPos immutablePos = pos.immutable();
						if (!CASCADES.contains(immutablePos)) {
							CASCADES.add(immutablePos);
						}
					}
				} else {
					BlockPos flowingToPos = pos.relative(flowDir);
					BlockState flowingToBlockState = world.getBlockState(flowingToPos);
					if (flowingToBlockState.getCollisionShape(world, flowingToPos).isEmpty()) {
						FluidState flowingEndFluidState = world.getFluidState(flowingToPos.below());
						if (flowingEndFluidState.isSource()) {
							BlockPos immutablePos = pos.immutable();
							if (!CASCADES.contains(immutablePos)) {
								CASCADES.add(immutablePos);
							}
						}
					}
				}
			}
		}
	}

	private static void spawnParticleFromDirection(
		@NotNull Level world,
		@NotNull BlockPos pos,
		@NotNull Direction direction,
		int count,
		boolean isFalling,
		double minVelocityScale,
		double maxVelocityScale,
		float fluidHeight,
		RandomSource random,
		ParticleOptions particle
	) {
		BlockPos otherPos = pos.relative(direction);
		BlockState otherState = world.getBlockState(otherPos);
		if (otherState.getCollisionShape(world, otherPos).isEmpty() && (otherState.getFluidState().isEmpty() || !isFalling)) {
			Vec3 directionOffset = Vec3.atLowerCornerOf(direction.getNormal()).scale(0.5D);
			Vec3 offsetPos = pos.getBottomCenter().add(directionOffset);
			for (int i = 0; i < count; i++) {
				double yOffset = isFalling ? random.nextDouble() * fluidHeight : fluidHeight;
				Vec3 particleOffsetPos = offsetPos.add(
					random.triangle(0D, 0.65D) * Math.abs(direction.getStepZ()),
					yOffset,
					random.triangle(0D, 0.65D) * Math.abs(direction.getStepX())
				);
				Vec3 velocity = directionOffset.scale(random.triangle((minVelocityScale + maxVelocityScale) * 0.5D, maxVelocityScale - minVelocityScale));
				world.addParticle(particle, particleOffsetPos.x, particleOffsetPos.y, particleOffsetPos.z, velocity.x, 0D, velocity.z);
			}
		}
	}

	private static final ArrayList<BlockPos> CASCADES = new ArrayList<>();

	public static void clearCascades() {
		CASCADES.clear();
	}

	public static void clearCascadesInChunk(ChunkPos chunkPos) {
		CASCADES.removeIf(blockPos -> (new ChunkPos(blockPos).equals(chunkPos)));
	}

	public static void tickCascades(ClientLevel world) {
		CASCADES.removeIf(blockPos ->
			!onCascadeTick(
				world,
				blockPos,
				world.getFluidState(blockPos),
				world.random
			)
		);
	}

	public static boolean onCascadeTick(
		Level world,
		BlockPos pos,
		@NotNull FluidState state,
		RandomSource random
	) {
		if (!state.isSource() && !state.isEmpty()) {
			int cascadeStrength = 3;
			Vec3 rawFlow = state.getFlow(world, pos);
			Vec3 flowVec = rawFlow.normalize();
			Direction flowDir = Direction.getNearest(flowVec);
			boolean isDown = state.hasProperty(FlowingFluid.FALLING) && state.getValue(FlowingFluid.FALLING);
			boolean spawnParticles = false;

			if (isDown) {
				FluidState belowFluidState = world.getFluidState(pos.below());
				if (belowFluidState.isSource()) {
					for (Direction direction : Direction.Plane.HORIZONTAL) {
						FluidState otherFluidState = world.getFluidState(pos.relative(direction));
						if (otherFluidState.is(state.getType()) && otherFluidState.hasProperty(FlowingFluid.FALLING) && otherFluidState.getValue(FlowingFluid.FALLING)) {
							cascadeStrength += 1;
						}
					}

					FluidState aboveFluidState = world.getFluidState(pos.above());
					if (aboveFluidState.is(state.getType()) && aboveFluidState.hasProperty(FlowingFluid.FALLING) && aboveFluidState.getValue(FlowingFluid.FALLING)) {
						cascadeStrength += 1;
					}

					spawnParticles = true;
				}
			} else {
				BlockPos flowingToPos = pos.relative(flowDir);
				BlockState flowingToBlockState = world.getBlockState(flowingToPos);
				if (flowingToBlockState.getCollisionShape(world, flowingToPos).isEmpty()) {
					FluidState flowingEndFluidState = world.getFluidState(flowingToPos.below());
					if (flowingEndFluidState.isSource()) {
						spawnParticles = true;
					}
				}
			}

			if (spawnParticles) {
				List<Direction> directions = isDown ? Direction.Plane.HORIZONTAL.shuffledCopy(random) : List.of(flowDir);
				for (Direction direction : directions) {
					spawnParticleFromDirection(
						world,
						pos,
						direction,
						random.nextInt((int) (cascadeStrength * 1.25D), (int) (cascadeStrength * 1.5D)),
						true,
						0.05D,
						0.2D,
						0.6F,
						random,
						ParticleTweaksParticleTypes.SPLASH
					);
				}
				return true;
			}
		}
		return false;
	}

}
