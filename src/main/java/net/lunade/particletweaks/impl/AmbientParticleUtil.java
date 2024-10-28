package net.lunade.particletweaks.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.particletweaks.registry.ParticleTweaksParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class AmbientParticleUtil {

	public static void tick(ClientLevel world) {
		Minecraft minecraft = Minecraft.getInstance();
		BlockPos pos = minecraft.gameRenderer.getMainCamera().getBlockPosition();
		animateTick(world, pos.getX(), pos.getY(), pos.getZ());
	}

	private static void animateTick(@NotNull ClientLevel level, int posX, int posY, int posZ) {
		RandomSource randomSource = level.random;
		BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
		for (int i = 0; i < 33; ++i) {
			spawnAmbientWindParticles(level, posX, posY, posZ, 32, randomSource, mutableBlockPos);
		}
	}

	private static void spawnAmbientWindParticles(
		@NotNull ClientLevel level, int posX, int posY, int posZ, int range, @NotNull RandomSource random, @NotNull BlockPos.MutableBlockPos blockPos
	) {
		int i = posX + random.nextIntBetweenInclusive(-range, range);
		int j = posY + random.nextIntBetweenInclusive(-range, range);
		int k = posZ + random.nextIntBetweenInclusive(-range, range);
		int heightMapY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i, k);
		blockPos.set(i, j, k);

		if (heightMapY > j
			&& level.getBlockState(blockPos).isAir()
			&& !level.canSeeSkyFromBelowWater(blockPos)
			&& level.getBrightness(LightLayer.SKY, blockPos) <= 7
		) {
			int levelMin = level.getMinBuildHeight();
			int levelMax = level.getMaxBuildHeight();
			int difference = levelMax - levelMin;
			if (random.nextBoolean() && (random.nextFloat() * (posY / difference)) <= 0.0015F) {
				level.addParticle(
					ParticleTweaksParticleTypes.CAVE_DUST,
					i,
					j,
					k,
					0D,
					0D,
					0D
				);
			}
		}
	}
}
