package de.cosmohdx.griefergames.v26_2.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import de.cosmohdx.griefergames.v26_2.TooltipFeatures;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.model.object.skull.SkullModelBase.State;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.PlayerHeadSpecialRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Player heads use this renderer, separately from other skull item models. */
@Mixin(PlayerHeadSpecialRenderer.class)
public abstract class GGPlayerHeadGlintMixin {
  @Shadow @Final private SkullModelBase modelBase;

  @Inject(method = "submit(Lnet/minecraft/client/renderer/PlayerSkinRenderCache$RenderInfo;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IIZI)V", at = @At("TAIL"))
  private void ggaddon$playerHeadGlint(PlayerSkinRenderCache.RenderInfo skin, PoseStack pose,
      SubmitNodeCollector collector, int light, int overlay, boolean foil, int tint,
      CallbackInfo ci) {
    if (!foil || !TooltipFeatures.headEnchantmentGlintEnabled()) return;
    collector.order(1).submitModel(this.modelBase, new State(), pose,
        RenderTypes.entityGlint(), light, OverlayTexture.NO_OVERLAY, tint, null);
  }
}
