package de.cosmohdx.griefergames.v1_21_4.mixins;

import de.cosmohdx.griefergames.feature.itempreview.EnchantmentGlintPass;
import net.minecraft.client.renderer.special.SkullSpecialRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.component.ResolvableProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkullSpecialRenderer.class)
public class MixinSkullSpecialRenderer {

  @Inject(method = "render(Lnet/minecraft/world/item/component/ResolvableProfile;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IIZ)V", at = @At("HEAD"))
  private void griefergames$start(ResolvableProfile profile, ItemDisplayContext context, PoseStack pose, MultiBufferSource buffer, int light, int overlay, boolean foil, CallbackInfo ci) {
    EnchantmentGlintPass.request(foil);
  }

  @Inject(method = "render(Lnet/minecraft/world/item/component/ResolvableProfile;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IIZ)V", at = @At("RETURN"))
  private void griefergames$end(ResolvableProfile profile, ItemDisplayContext context, PoseStack pose, MultiBufferSource buffer, int light, int overlay, boolean foil, CallbackInfo ci) {
    EnchantmentGlintPass.clear();
  }
}
