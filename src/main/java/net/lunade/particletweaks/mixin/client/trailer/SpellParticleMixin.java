package net.lunade.particletweaks.mixin.client.trailer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.particletweaks.config.ParticleTweaksConfigGetter;
import net.lunade.particletweaks.impl.ParticleTweakInterface;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = SpellParticle.class, priority = 1002)
public abstract class SpellParticleMixin extends TextureSheetParticle implements ParticleTweakInterface {

	@Unique
	private float particleTweaks$yRotPerTick;
	@Unique
	private float particleTweaks$yRot;
	@Unique
	private float particleTweaks$prevYRot;

	@Unique
	private float particleTweaks$zRotPerTick;

	protected SpellParticleMixin(ClientLevel world, double d, double e, double f) {
		super(world, d, e, f);
	}

	@Inject(method = "<init>*", at = @At("TAIL"))
	private void particleTweaks$init(CallbackInfo info) {
		if (ParticleTweaksConfigGetter.trailerSpell()) {
			this.particleTweaks$setNewSystem(true);
			this.particleTweaks$setScaler(0.15F);
			this.particleTweaks$setScalesToZero();
			this.particleTweaks$setSwitchesExit(false);

			RandomSource random = RandomSource.createNewThreadLocalInstance();
			this.particleTweaks$yRotPerTick = (random.nextFloat() - 0.5F) * 0.075F;
			this.particleTweaks$zRotPerTick = (random.nextFloat() - 0.5F) * 0.075F;
		}
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void particleTweaks$tick(CallbackInfo info) {
		if (ParticleTweaksConfigGetter.trailerSpell()) {
			this.oRoll = this.roll;
			this.roll += this.particleTweaks$zRotPerTick;

			this.particleTweaks$prevYRot = this.particleTweaks$yRot;
			this.particleTweaks$yRot += this.particleTweaks$yRotPerTick;
		}
	}

	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		if (ParticleTweaksConfigGetter.trailerSpell()) {
			Quaternionf quaternionf = new Quaternionf();
			this.getFacingCameraMode().setRotation(quaternionf, camera, tickDelta);
			quaternionf.rotateZ(Mth.lerp(tickDelta, this.oRoll, this.roll));
			quaternionf.rotateY(Mth.lerp(tickDelta, this.particleTweaks$prevYRot, this.particleTweaks$yRot));

			this.renderRotatedQuad(vertexConsumer, camera, quaternionf, tickDelta);
		} else {
			super.render(vertexConsumer, camera, tickDelta);
		}
	}

}
