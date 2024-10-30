package net.lunade.particletweaks.config.cloth;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ParticleTweaksConfigScreenBuilder {

    @Contract(pure = true)
    public static @NotNull ConfigScreenFactory<Screen> buildScreen() {
        return ParticleTweaksConfig::buildScreen;
    }

}
