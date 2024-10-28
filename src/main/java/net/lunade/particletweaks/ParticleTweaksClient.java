package net.lunade.particletweaks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.lunade.particletweaks.particle.FluidFlowParticle;
import net.lunade.particletweaks.registry.ParticleTweaksParticleTypes;

@Environment(EnvType.CLIENT)
public class ParticleTweaksClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ParticleTweaksParticleTypes.init();

		ParticleFactoryRegistry particleRegistry = ParticleFactoryRegistry.getInstance();
		particleRegistry.register(ParticleTweaksParticleTypes.FLOWING_LAVA, FluidFlowParticle.LavaFactory::new);
		particleRegistry.register(ParticleTweaksParticleTypes.FLOWING_WATER, FluidFlowParticle.WaterFactory::new);
	}
}
