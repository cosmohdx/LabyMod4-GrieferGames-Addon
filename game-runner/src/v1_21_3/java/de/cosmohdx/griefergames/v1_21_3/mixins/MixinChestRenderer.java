package de.cosmohdx.griefergames.v1_21_3.mixins;

import de.cosmohdx.griefergames.feature.itempreview.EnchantmentGlintPass;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestRenderer.class)
public class MixinChestRenderer {

  @Shadow
  private ChestModel singleModel;

  @Inject(
      method = "render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
      at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V")
  )
  private void griefergames$glint(BlockEntity entity, float partialTick, PoseStack pose, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
    if (!EnchantmentGlintPass.requested() || this.singleModel == null) {
      return;
    }
    this.singleModel.renderToBuffer(pose, buffer.getBuffer(RenderType.entityGlint()), light, overlay);
  }
}
