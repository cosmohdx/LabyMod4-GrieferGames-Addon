package de.cosmohdx.griefergames.v1_20_4.mixins;

import de.cosmohdx.griefergames.feature.itempreview.EnchantmentGlintPass;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkullBlockRenderer.class)
public class MixinSkullBlockRenderer {

  @Inject(
      method = "renderSkull",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/client/model/SkullModelBase;renderToBuffer:(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
          shift = At.Shift.AFTER
      )
  )
  private static void griefergames$glint(Direction direction, float yRot, float animation, PoseStack pose, MultiBufferSource buffer, int light, SkullModelBase model, RenderType renderType, CallbackInfo ci) {
    if (!EnchantmentGlintPass.requested() || model == null) {
      return;
    }
    model.renderToBuffer(pose, buffer.getBuffer(RenderType.entityGlint()), light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
  }
}
