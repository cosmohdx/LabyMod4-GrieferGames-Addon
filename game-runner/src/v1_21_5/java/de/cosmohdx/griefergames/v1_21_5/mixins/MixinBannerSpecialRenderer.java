package de.cosmohdx.griefergames.v1_21_5.mixins;

import de.cosmohdx.griefergames.feature.itempreview.EnchantmentGlintPass;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.BannerSpecialRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BannerSpecialRenderer.class)
public class MixinBannerSpecialRenderer {

  @Inject(method = "render(Lnet/minecraft/world/level/block/entity/BannerPatternLayers;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IIZ)V", at = @At("HEAD"))
  private void griefergames$start(BannerPatternLayers layers, ItemDisplayContext context, PoseStack pose, MultiBufferSource buffer, int light, int overlay, boolean foil, CallbackInfo ci) {
    EnchantmentGlintPass.request(foil);
  }

  @Inject(method = "render(Lnet/minecraft/world/level/block/entity/BannerPatternLayers;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IIZ)V", at = @At("RETURN"))
  private void griefergames$end(BannerPatternLayers layers, ItemDisplayContext context, PoseStack pose, MultiBufferSource buffer, int light, int overlay, boolean foil, CallbackInfo ci) {
    EnchantmentGlintPass.clear();
  }
}
