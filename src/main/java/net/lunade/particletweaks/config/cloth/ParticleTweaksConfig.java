package net.lunade.particletweaks.config.cloth;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.particletweaks.ParticleTweaksClient;
import net.lunade.particletweaks.ParticleTweaksConstants;
import net.minecraft.client.gui.screens.Screen;

@Config(name = "particletweaks")
public class ParticleTweaksConfig extends PartitioningSerializer.GlobalData {

	@ConfigEntry.Category("config")
	@ConfigEntry.Gui.TransitiveObject
	public final ParticleTweaksParticleConfig config = new ParticleTweaksParticleConfig();

	@Environment(EnvType.CLIENT)
	public static Screen buildScreen(Screen parent) {
		var configBuilder = ConfigBuilder.create().setParentScreen(parent).setTitle(ParticleTweaksConstants.text("component.title"));
		configBuilder.setSavingRunnable(() -> AutoConfig.getConfigHolder(ParticleTweaksConfig.class).save());
		ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();

		var gameplayTab = configBuilder.getOrCreateCategory(ParticleTweaksConstants.text("config"));
		ParticleTweaksParticleConfig.setupEntries(gameplayTab, entryBuilder);

		return configBuilder.build();
	}

	public static ParticleTweaksConfig get() {
		if (!ParticleTweaksClient.areConfigsInit) {
			AutoConfig.register(ParticleTweaksConfig.class, PartitioningSerializer.wrap(GsonConfigSerializer::new));
			ParticleTweaksClient.areConfigsInit = true;
		}
		return AutoConfig.getConfigHolder(ParticleTweaksConfig.class).getConfig();
	}

}
