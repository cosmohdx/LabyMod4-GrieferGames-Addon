package de.cosmohdx.griefergames.v26_2.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import de.cosmohdx.griefergames.v26_2.TooltipFeatures;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.model.object.skull.SkullModelBase.State;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SkullSpecialRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Minecraft 26.2's skull special renderer receives the foil flag but does not draw the foil pass. */
@Mixin(SkullSpecialRenderer.class)
public abstract class GGSkullGlintRenderMixin {
  @Shadow @Final private SkullModelBase model;
  @Shadow @Final private float animation;

  @Inject(method = "submit", at = @At("TAIL"))
  private void ggaddon$renderSkullGlint(PoseStack pose, SubmitNodeCollector collector,
      int light, int overlay, boolean foil, int tint, CallbackInfo ci) {
    if (!foil || !TooltipFeatures.headEnchantmentGlintEnabled()) {
      return;
    }
    State state = new State();
    state.animationPos = this.animation;
    collector.order(1).submitModel(this.model, state, pose, RenderTypes.entityGlint(),
        light, OverlayTexture.NO_OVERLAY, tint, null);
  }
}
