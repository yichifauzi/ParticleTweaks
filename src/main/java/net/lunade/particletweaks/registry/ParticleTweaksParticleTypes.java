package net.lunade.particletweaks.registry;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.lunade.particletweaks.ParticleTweaksConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ParticleTweaksParticleTypes {
	public static final SimpleParticleType FLOWING_LAVA = register("flowing_lava");
	public static final SimpleParticleType FLOWING_WATER = register("flowing_water");
	public static final SimpleParticleType SPLASH = register("splash");
	public static final SimpleParticleType CAVE_DUST = register("cave_dust");
	public static final SimpleParticleType POOF = register("poof");

	public static void init() {
	}

	@NotNull
	private static SimpleParticleType register(@NotNull String name, boolean alwaysShow) {
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, ParticleTweaksConstants.id(name), FabricParticleTypes.simple(alwaysShow));
	}

	@NotNull
	private static SimpleParticleType register(@NotNull String name) {
		return register(name, false);
	}

	@NotNull
	private static <T extends ParticleOptions> ParticleType<T> register(
		String string,
		boolean alwaysShow,
		Function<ParticleType<T>, MapCodec<T>> function,
		Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> function2
	) {
		return register(ParticleTweaksConstants.id(string), alwaysShow, function, function2);
	}

	@NotNull
	private static <T extends ParticleOptions> ParticleType<T> register(
		ResourceLocation resourceLocation,
		boolean alwaysShow,
		Function<ParticleType<T>, MapCodec<T>> function,
		Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> function2
	) {
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, resourceLocation, new ParticleType<T>(alwaysShow) {
			@Override
			public @NotNull MapCodec<T> codec() {
				return function.apply(this);
			}

			@NotNull
			@Override
			public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
				return function2.apply(this);
			}
		});
	}
}
