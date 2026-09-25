package de.cosmohdx.griefergames.v1_21_5.mixins;

import de.cosmohdx.griefergames.feature.itempreview.EnchantmentGlintPass;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestSpecialRenderer.class)
public class MixinChestSpecialRenderer {

  @Shadow
  private ChestModel model;

  @Inject(method = "render", at = @At("RETURN"))
  private void griefergames$glint(ItemDisplayContext context, PoseStack pose, MultiBufferSource buffer, int light, int overlay, boolean foil, CallbackInfo ci) {
    if (!foil || !EnchantmentGlintPass.enabled() || this.model == null) {
      return;
    }
    this.model.renderToBuffer(pose, buffer.getBuffer(RenderType.entityGlint()), light, overlay);
  }
}
