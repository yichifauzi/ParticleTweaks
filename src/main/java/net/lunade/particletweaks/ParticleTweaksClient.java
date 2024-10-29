package net.lunade.particletweaks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.lunade.particletweaks.impl.AmbientParticleUtil;
import net.lunade.particletweaks.impl.FlowingFluidParticleUtil;
import net.lunade.particletweaks.particle.CaveDustParticle;
import net.lunade.particletweaks.particle.ComfySmokeParticle;
import net.lunade.particletweaks.particle.FlareParticle;
import net.lunade.particletweaks.particle.FluidFlowParticle;
import net.lunade.particletweaks.particle.PoofParticle;
import net.lunade.particletweaks.registry.ParticleTweaksParticleTypes;

@Environment(EnvType.CLIENT)
public class ParticleTweaksClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientChunkEvents.CHUNK_UNLOAD.register((clientLevel, levelChunk) -> FlowingFluidParticleUtil.clearCascadesInChunk(levelChunk.getPos()));
		ClientLifecycleEvents.CLIENT_STOPPING.register((clientLevel) -> FlowingFluidParticleUtil.clearCascades());
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> FlowingFluidParticleUtil.clearCascades());

		ClientTickEvents.START_WORLD_TICK.register((clientLevel) -> {
			FlowingFluidParticleUtil.tickCascades(clientLevel);
			AmbientParticleUtil.tick(clientLevel);
		});

		ParticleTweaksParticleTypes.init();

		ParticleFactoryRegistry particleRegistry = ParticleFactoryRegistry.getInstance();
		particleRegistry.register(ParticleTweaksParticleTypes.FLOWING_LAVA, FluidFlowParticle.LavaFactory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.FLOWING_WATER, FluidFlowParticle.WaterFactory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.SPLASH, FluidFlowParticle.SplashFactory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.CAVE_DUST, CaveDustParticle.Factory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.POOF, PoofParticle.Factory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.FLARE, FlareParticle.Factory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.SOUL_FLARE, FlareParticle.SoulFactory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.COMFY_SMOKE_A, ComfySmokeParticle.Factory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.COMFY_SMOKE_B, ComfySmokeParticle.Factory::new);
	}
}
