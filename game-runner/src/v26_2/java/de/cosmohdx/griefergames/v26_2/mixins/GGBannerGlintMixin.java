package de.cosmohdx.griefergames.v26_2.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import de.cosmohdx.griefergames.v26_2.TooltipFeatures;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.BannerSpecialRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Adds foil to both banner geometry pieces after the normal pattern render. */
@Mixin(BannerSpecialRenderer.class)
public abstract class GGBannerGlintMixin {
  @Shadow @Final private BannerRenderer bannerRenderer;
  @Shadow @Final private BannerBlock.AttachmentType attachment;

  @Inject(method = "submit(Lnet/minecraft/world/level/block/entity/BannerPatternLayers;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IIZI)V", at = @At("TAIL"))
  private void ggaddon$renderBannerGlint(BannerPatternLayers patterns, PoseStack pose,
      SubmitNodeCollector collector, int light, int overlay, boolean foil, int tint,
      CallbackInfo ci) {
    if (!foil || !TooltipFeatures.headEnchantmentGlintEnabled()) return;
    GGBannerModelAccess models = (GGBannerModelAccess) this.bannerRenderer;
    collector.order(1).submitModel(models.ggaddon$bannerModel(this.attachment), Unit.INSTANCE,
        pose, RenderTypes.entityGlint(), light, OverlayTexture.NO_OVERLAY, tint, null);
    collector.order(1).submitModel(models.ggaddon$flagModel(this.attachment), 0.0F,
        pose, RenderTypes.entityGlint(), light, OverlayTexture.NO_OVERLAY, tint, null);
  }
}
