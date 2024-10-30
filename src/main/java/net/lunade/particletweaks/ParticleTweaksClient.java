package net.lunade.particletweaks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.lunade.particletweaks.impl.CaveDustSpawner;
import net.lunade.particletweaks.impl.FlowingFluidParticleUtil;
import net.lunade.particletweaks.impl.TorchParticleUtil;
import net.lunade.particletweaks.particle.CampfireFlareParticle;
import net.lunade.particletweaks.particle.CaveDustParticle;
import net.lunade.particletweaks.particle.ComfySmokeParticle;
import net.lunade.particletweaks.particle.FlareParticle;
import net.lunade.particletweaks.particle.FluidFlowParticle;
import net.lunade.particletweaks.particle.PoofParticle;
import net.lunade.particletweaks.particle.RippleParticle;
import net.lunade.particletweaks.particle.SmallBubbleParticle;
import net.lunade.particletweaks.particle.WaveParticle;
import net.lunade.particletweaks.particle.WaveSeedParticle;
import net.lunade.particletweaks.registry.ParticleTweaksParticleTypes;

@Environment(EnvType.CLIENT)
public class ParticleTweaksClient implements ClientModInitializer {
	public static boolean areConfigsInit = false;

	@Override
	public void onInitializeClient() {
		ClientChunkEvents.CHUNK_UNLOAD.register((clientLevel, levelChunk) -> {
			FlowingFluidParticleUtil.clearCascadesInChunk(levelChunk.getPos());
			TorchParticleUtil.clearTorchesInChunk(levelChunk.getPos());
		});
		ClientLifecycleEvents.CLIENT_STOPPING.register((clientLevel) -> {
			FlowingFluidParticleUtil.clearCascades();
			TorchParticleUtil.clearTorches();
		});
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			FlowingFluidParticleUtil.clearCascades();
			TorchParticleUtil.clearTorches();
		});

		ClientTickEvents.START_WORLD_TICK.register((clientLevel) -> {
			FlowingFluidParticleUtil.tickCascades(clientLevel);
			TorchParticleUtil.tickTorches(clientLevel);
			CaveDustSpawner.tick(clientLevel);
		});

		ParticleTweaksParticleTypes.init();

		ParticleFactoryRegistry particleRegistry = ParticleFactoryRegistry.getInstance();
		particleRegistry.register(ParticleTweaksParticleTypes.FLOWING_LAVA, FluidFlowParticle.LavaFactory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.FLOWING_WATER, FluidFlowParticle.WaterFactory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.SMALL_BUBBLE, SmallBubbleParticle.Factory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.SMALL_CASCADE, FluidFlowParticle.SmallCascadeFactory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.CASCADE_A, FluidFlowParticle.CascadeFactory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.CASCADE_B, FluidFlowParticle.CascadeFactory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.SPLASH, FluidFlowParticle.SplashFactory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.RIPPLE, RippleParticle.Factory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.WAVE_OUTLINE, WaveParticle.OutlineFactory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.WAVE, WaveParticle.Factory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.WAVE_SEED, WaveSeedParticle.Factory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.CAVE_DUST, CaveDustParticle.Factory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.POOF, PoofParticle.Factory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.FLARE, FlareParticle.Factory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.SOUL_FLARE, FlareParticle.SoulFactory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.CAMPFIRE_FLARE, CampfireFlareParticle.Factory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.SOUL_CAMPFIRE_FLARE, CampfireFlareParticle.SoulFactory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.COMFY_SMOKE_A, ComfySmokeParticle.Factory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.COMFY_SMOKE_B, ComfySmokeParticle.Factory::new);
	}
}
