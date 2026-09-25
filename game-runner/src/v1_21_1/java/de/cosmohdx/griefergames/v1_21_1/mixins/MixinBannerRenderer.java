package de.cosmohdx.griefergames.v1_21_1.mixins;

import de.cosmohdx.griefergames.feature.itempreview.EnchantmentGlintPass;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BannerRenderer.class)
public class MixinBannerRenderer {

  @Shadow
  private ModelPart flag;

  @Shadow
  private ModelPart pole;

  @Shadow
  private ModelPart bar;

  @Inject(
      method = "render(Lnet/minecraft/world/level/block/entity/BannerBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
      at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", ordinal = 1)
  )
  private void griefergames$glint(BannerBlockEntity entity, float partialTick, PoseStack pose, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
    if (!EnchantmentGlintPass.requested()) {
      return;
    }
    var consumer = buffer.getBuffer(RenderType.entityGlint());
    this.flag.render(pose, consumer, light, overlay);
    this.pole.render(pose, consumer, light, overlay);
    this.bar.render(pose, consumer, light, overlay);
  }
}
