package de.cosmohdx.griefergames.v1_18_2.mixins;

import de.cosmohdx.griefergames.feature.itempreview.EnchantmentGlintPass;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
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
  private ModelPart lid;

  @Shadow
  private ModelPart bottom;

  @Shadow
  private ModelPart lock;

  @Inject(
      method = "render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
      at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V")
  )
  private void griefergames$glint(BlockEntity entity, float partialTick, PoseStack pose, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
    if (!EnchantmentGlintPass.requested()) {
      return;
    }
    var consumer = buffer.getBuffer(RenderType.entityGlint());
    this.lid.render(pose, consumer, light, overlay);
    this.bottom.render(pose, consumer, light, overlay);
    this.lock.render(pose, consumer, light, overlay);
  }
}
