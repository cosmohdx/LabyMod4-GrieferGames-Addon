package de.cosmohdx.griefergames.v26_2.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import de.cosmohdx.griefergames.v26_2.TooltipFeatures;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** The special chest item renderer receives the foil flag but omits its glint pass. */
@Mixin(ChestSpecialRenderer.class)
public abstract class GGChestGlintMixin {
  @Shadow @Final private ChestModel model;
  @Shadow @Final private float openness;

  @Inject(method = "submit", at = @At("TAIL"))
  private void ggaddon$renderChestGlint(PoseStack pose, SubmitNodeCollector collector,
      int light, int overlay, boolean foil, int tint, CallbackInfo ci) {
    if (!foil || !TooltipFeatures.headEnchantmentGlintEnabled()) return;
    collector.order(1).submitModel(this.model, this.openness, pose,
        RenderTypes.entityGlint(), light, OverlayTexture.NO_OVERLAY, tint, null);
  }
}
