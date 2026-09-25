package de.cosmohdx.griefergames.v1_16_5.mixins;

import de.cosmohdx.griefergames.feature.itempreview.EnchantmentGlintPass;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SkullBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkullBlockRenderer.class)
public class MixinSkullBlockRenderer {

  @Shadow
  private static Map<SkullBlock.Type, SkullModel> MODEL_BY_TYPE;

  @Inject(
      method = "renderSkull",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/client/model/SkullModel;renderToBuffer:(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
          shift = At.Shift.AFTER
      )
  )
  private static void griefergames$glint(Direction direction, float yRot, SkullBlock.Type type, GameProfile profile, float animation, PoseStack pose, MultiBufferSource buffer, int light, CallbackInfo ci) {
    if (!EnchantmentGlintPass.requested()) {
      return;
    }
    SkullModel model = MODEL_BY_TYPE.get(type);
    if (model == null) {
      return;
    }
    model.renderToBuffer(pose, buffer.getBuffer(RenderType.entityGlint()), light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
  }
}
