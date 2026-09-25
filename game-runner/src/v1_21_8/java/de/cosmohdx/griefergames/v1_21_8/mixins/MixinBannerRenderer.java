package de.cosmohdx.griefergames.v1_21_8.mixins;

import de.cosmohdx.griefergames.feature.itempreview.EnchantmentGlintPass;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.BannerFlagModel;
import net.minecraft.client.model.BannerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BannerRenderer.class)
public class MixinBannerRenderer {

  @Inject(method = "renderBanner", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
  private static void griefergames$glint(PoseStack pose, MultiBufferSource buffer, int light, int overlay, float rotation, BannerModel model, BannerFlagModel flag, float swing, DyeColor color, BannerPatternLayers layers, CallbackInfo ci) {
    if (!EnchantmentGlintPass.requested()) {
      return;
    }
    var consumer = buffer.getBuffer(RenderType.entityGlint());
    model.renderToBuffer(pose, consumer, light, overlay);
    flag.renderToBuffer(pose, consumer, light, overlay);
  }
}
